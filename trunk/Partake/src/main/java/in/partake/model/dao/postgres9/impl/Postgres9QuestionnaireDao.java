package in.partake.model.dao.postgres9.impl;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IQuestionnaireAccess;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.Questionnaire;

public class Postgres9QuestionnaireDao extends Postgres9Dao implements IQuestionnaireAccess {
    static final String TABLE_NAME = "QuestionnaireEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9QuestionnaireDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void put(PartakeConnection con, Questionnaire t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Questionnaire find(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<Questionnaire> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Questionnaire> findQuestionnairesByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

}
