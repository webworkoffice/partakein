package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.EventParticipationListPermission;
import in.partake.model.UserTicketEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import au.com.bytecode.opencsv.CSVWriter;

public class ShowParticipantsCSVAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        UUID ticketId = getValidTicketIdParameter(UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        InputStream is = new ShowParticipantsCSVTransaction(user, ticketId).execute();
        return renderAttachmentStream(is, "text/csv");
    }
}

class ShowParticipantsCSVTransaction extends DBAccess<InputStream> {
    private UserEx user;
    private UUID ticketId;

    public ShowParticipantsCSVTransaction(UserEx user, UUID ticketId) {
        this.user = user;
        this.ticketId = ticketId;
    }

    @Override
    protected InputStream doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
    	EventTicket ticket = daos.getEventTicketAccess().find(con, ticketId);
    	if (ticket == null)
    		throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

    	Event event = daos.getEventAccess().find(con, ticket.getEventId());
        if (event == null)
        	throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        // Only owner can retrieve the participants list.
        if (!EventParticipationListPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        List<UserTicketEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
        EventTicketHolderList list = ticket.calculateParticipationList(event, participations);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos));

        doWrite(con, daos, list, writer);

        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void doWrite(PartakeConnection con, IPartakeDAOs daos, EventTicketHolderList list, CSVWriter writer) throws DAOException, PartakeException {
        for (UserTicket participation : list.getEnrolledParticipations()) {
            UserEx attendant = UserDAOFacade.getUserEx(con, daos, participation.getUserId());

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

        for (UserTicket participation : list.getSpareParticipations()) {
            UserEx attendant = UserDAOFacade.getUserEx(con, daos, participation.getUserId());

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
    }
}
