package in.partake.controller.action.event;

import java.util.ArrayList;
import java.util.List;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;

public abstract class AbstractEventAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    /**
     * user が event に登録するために、登録が必要な event たちを列挙する。
     * @param eventId
     * @param user
     * @return
     * @throws DAOException
     */
    // TODO: The same function exists in AbstractEventAPI. We should share the code.
    // TODO: This function should be moved into EventFacade or a similar class.
    protected List<Event> getRequiredEventsNotEnrolled(UserEx user, List<EventRelationEx> relations) throws DAOException {
        List<Event> requiredEvents = new ArrayList<Event>();
        for (EventRelationEx relation : relations) {
            if (!relation.isRequired()) { continue; }
            if (relation.getEvent() == null) { continue; }
            if (user != null) {
                ParticipationStatus status = UserService.get().getParticipationStatus(user.getId(), relation.getEvent().getId());
                if (status.isEnrolled()) { continue; }
            }
            requiredEvents.add(relation.getEvent());
        }

        return requiredEvents;
    }

}
