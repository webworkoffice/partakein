package in.partake.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import in.partake.model.EventEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.Constants;
import in.partake.service.EventService;
import in.partake.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

public class EventParticipantsController extends PartakeActionSupport {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventParticipantsController.class);

    private String contentType = null;
    private ByteArrayInputStream inputStream = null;
    private String eventId;

    public String getContentType() {
        return this.contentType;
    }

    public ByteArrayInputStream getInputStream() {
        return this.inputStream;
    }

    public String getEventId() {
        return eventId;
    }

    // ----------------------------------------------------------------------

    public String showParticipants() throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();

        eventId = getParameter("eventId");
        if (eventId == null) { throw new PartakeResultException(ERROR); }

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { throw new PartakeResultException(ERROR); }

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_PARTICIPATION_LIST)) {
            // TODO: Hmm...
            addErrorMessage("イベント参加者の取得権限がありません。");
            throw new PartakeResultException(PROHIBITED);
        }

        List<EnrollmentEx> participations = EventService.get().getEnrollmentEx(eventId);
        ParticipationList list = event.calculateParticipationList(participations);

        attributes.put(Constants.ATTR_EVENT, event);
        attributes.put(Constants.ATTR_PARTICIPATIONLIST, list);

        return SUCCESS;
    }

    public String makeAttendantVIP() throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();

        eventId = getParameter("eventId");
        if (StringUtils.isEmpty(eventId)) { throw new PartakeResultException(ERROR); }

        String userId = getParameter("userId");
        if (StringUtils.isEmpty(userId)) { throw new PartakeResultException(ERROR); }

        boolean vip = "true".equals(getParameter("vip"));

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { throw new PartakeResultException(ERROR); }

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS)) {
            addErrorMessage("イベント参加者の編集権限がありません。");
            throw new PartakeResultException(PROHIBITED);
        }

        if (EventService.get().makeAttendantVIP(eventId, userId, vip)) {
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    public String removeAttendant() throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();

        eventId = getParameter("eventId");
        if (StringUtils.isEmpty(eventId)) { throw new PartakeResultException(ERROR); }

        String userId = getParameter("userId");
        if (StringUtils.isEmpty(userId)) { throw new PartakeResultException(ERROR); }

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { throw new PartakeResultException(ERROR); }

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS)) {
            addErrorMessage("イベント参加者の編集権限がありません。");
            throw new PartakeResultException(PROHIBITED);
        }

        if (EventService.get().removeEnrollment(eventId, userId)) {
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    /**
     *
     * @return
     */
    public String showCSV() throws PartakeResultException {
        try {
            ParticipationList list = calculateParticipationList();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos));

            for (Enrollment participation : list.getEnrolledParticipations()) {
                UserEx user = UserService.get().getUserExById(participation.getUserId());

                String[] lst = new String[4];
                lst[0] = user.getTwitterLinkage().getScreenName();
                // TODO why don't you use in.partake.view.util.Helper.enrollmentStatus to get status?
                if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) {
                    lst[1] = "参加";
                } else if (ParticipationStatus.RESERVED.equals(participation.getStatus())) {
                    lst[1] = "仮参加";
                } else {
                    lst[1] = "(状態不明...)";
                    logger.warn("SHOULD NOT HAPPEN");
                }
                lst[2] = participation.getComment();
                lst[3] = participation.getModifiedAt().toString();
                writer.writeNext(lst);
            }

            for (Enrollment participation : list.getSpareParticipations()) {
                UserEx user = UserService.get().getUserExById(participation.getUserId());

                String[] lst = new String[4];
                lst[0] = user.getTwitterLinkage().getScreenName();
                if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) {
                    lst[1] = "補欠 (参加)";
                } else if (ParticipationStatus.RESERVED.equals(participation.getStatus())) {
                    lst[1] = "補欠 (仮参加)";
                } else {
                    lst[1] = "補欠 (状態不明...)";
                    logger.warn("SHOULD NOT HAPPEN");
                }
                lst[2] = participation.getComment();
                lst[3] = participation.getModifiedAt().toString();
                writer.writeNext(lst);
            }

            writer.flush();
            writer.close();

            this.contentType = "text/csv";
            this.inputStream = new ByteArrayInputStream(baos.toByteArray());
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR;
        } catch (DAOException e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    /**
     * eventId から participation list を計算する。
     *
     * @param user
     * @return
     * @throws PartakeResultException
     */
    private ParticipationList calculateParticipationList() throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();

        eventId = getParameter("eventId");
        if (eventId == null) { throw new PartakeResultException(ERROR); }
        
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { throw new PartakeResultException(ERROR); }

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_PARTICIPATION_LIST)) {
            addErrorMessage("イベント参加者の取得権限がありません。");
            throw new PartakeResultException(PROHIBITED);
        }

        List<EnrollmentEx> participations = EventService.get().getEnrollmentEx(eventId);
        return event.calculateParticipationList(participations);
    }
}
