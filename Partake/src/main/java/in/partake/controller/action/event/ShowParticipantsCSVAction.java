package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.EventParticipationListPermission;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class ShowParticipantsCSVAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter(UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        InputStream is = new ShowParticipantsCSVTransaction(user, eventId).execute();
        return renderAttachmentStream(is, "text/csv");
    }
}

class ShowParticipantsCSVTransaction extends Transaction<InputStream> {
    private UserEx user;
    private String eventId;
    
    public ShowParticipantsCSVTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }
    
    @Override
    protected InputStream doExecute(PartakeConnection con) throws DAOException, PartakeException {
        ParticipationList list = calculateParticipationList(con);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos));

        for (Enrollment participation : list.getEnrolledParticipations()) {
            UserEx attendant = UserDAOFacade.getUserEx(con, participation.getUserId());

            String[] lst = new String[4];
            lst[0] = attendant.getTwitterLinkage().getScreenName();
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
            UserEx attendant = UserDAOFacade.getUserEx(con, participation.getUserId());

            String[] lst = new String[4];
            lst[0] = attendant.getTwitterLinkage().getScreenName();
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
            throw new PartakeException(ServerErrorCode.ERROR_IO);
        }
        
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    private ParticipationList calculateParticipationList(PartakeConnection con) throws DAOException, PartakeException {
        EventEx event = EventDAOFacade.getEventEx(con, eventId); 
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        // Only owner can retrieve the participants list.
        if (!EventParticipationListPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        List<EnrollmentEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, eventId); 
        return event.calculateParticipationList(participations);
    }
}
