package in.partake.model.dao;

import in.partake.model.dto.EventRelation;

import java.util.List;

public interface IEventRelationAccess {

	/**
     *  event の event relation を、<code>relations</code> に設定します。
     */
    public abstract void setEventRelations(PartakeConnection con, String eventId, List<EventRelation> relations) throws DAOException;
    
    /**
     *  event の relation を取得します。
     */
    public abstract List<EventRelation> getEventRelations(PartakeConnection con, String eventId) throws DAOException;

}
