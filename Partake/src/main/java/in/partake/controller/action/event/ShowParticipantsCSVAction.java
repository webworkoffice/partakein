package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class ShowParticipantsCSVAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        ParticipationList list = calculateParticipationList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos));

        for (Enrollment participation : list.getEnrolledParticipations()) {
            UserEx user = DeprecatedUserDAOFacade.get().getUserExById(participation.getUserId());

            String[] lst = new String[4];
            lst[0] = user.getTwitterLinkage().getScreenName();
            // TODO why don't you use in.partake.view.util.Helper.enrollmentStatus to get status?
            if (ParticipationStatus.ENROLLED.equals(participation.getStatus()))
                lst[1] = "参加";
            else if (ParticipationStatus.RESERVED.equals(participation.getStatus()))
                lst[1] = "仮参加";
            else
                lst[1] = "(状態不明...)";
            lst[2] = participation.getComment();
            lst[3] = participation.getModifiedAt().toString();
            writer.writeNext(lst);
        }

        for (Enrollment participation : list.getSpareParticipations()) {
            UserEx user = DeprecatedUserDAOFacade.get().getUserExById(participation.getUserId());

            String[] lst = new String[4];
            lst[0] = user.getTwitterLinkage().getScreenName();
            if (ParticipationStatus.ENROLLED.equals(participation.getStatus()))
                lst[1] = "補欠 (参加)";
            else if (ParticipationStatus.RESERVED.equals(participation.getStatus()))
                lst[1] = "補欠 (仮参加)";
            else
                lst[1] = "補欠 (状態不明...)";
            lst[2] = participation.getComment();
            lst[3] = participation.getModifiedAt().toString();
            writer.writeNext(lst);
        }

        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO);
        }
        return renderAttachmentStream(new ByteArrayInputStream(baos.toByteArray()), "text/csv");
    }

    /**
     * eventId から participation list を計算する。
     *
     * @param user
     * @return
     * @throws PartakeResultException
     */
    private ParticipationList calculateParticipationList() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        String eventId = getValidEventIdParameter();

        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_PARTICIPATION_LIST))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        List<EnrollmentEx> participations = DeprecatedEventDAOFacade.get().getEnrollmentEx(eventId);
        return event.calculateParticipationList(participations);
    }
}
