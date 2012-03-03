package in.partake.controller.api.event;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEventAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * user が event に登録するために、登録が必要な event たちを列挙する。
     * @param eventId
     * @param user
     * @return
     * @throws DAOException
     */
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
