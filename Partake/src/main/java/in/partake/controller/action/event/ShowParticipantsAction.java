package in.partake.controller.action.event;

import java.util.List;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;

public class ShowParticipantsAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_PARTICIPATION_LIST))
            return renderInvalid(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        List<EnrollmentEx> participations = EventService.get().getEnrollmentEx(eventId);
        ParticipationList list = event.calculateParticipationList(participations);

        attributes.put(Constants.ATTR_EVENT, event);
        attributes.put(Constants.ATTR_PARTICIPATIONLIST, list);

        return render("events/participants/show.jsp");
    }
}
