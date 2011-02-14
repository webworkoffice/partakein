package in.partake.model.dao;

import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

import java.util.List;

public interface IEventRelationAccess extends IAccess<EventRelation, EventRelationPK> {

    /**
     * remove all event relations whose srcEventId is <code>eventId</code>.
     * @param con
     * @param srcEventId
     * @throws DAOException
     */
    public abstract void removeByEventId(PartakeConnection con, String srcEventId) throws DAOException;

    /**
     * get all event relations whose srcEventId is <code>eventId</code>
     * @param con
     * @param srcEventId
     * @return
     * @throws DAOException
     */
    public abstract List<EventRelation> findByEventId(PartakeConnection con, String srcEventId) throws DAOException;
}
