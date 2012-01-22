package in.partake.model.dao.postgres9.impl;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.pk.EnrollmentPK;

public class Postgres9EnrollmentDao extends Postgres9Dao implements IEnrollmentAccess {
    static final String TABLE_NAME = "EnrollmentEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9EnrollmentDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        entityDao.initialize((Postgres9Connection) con);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void put(PartakeConnection con, Enrollment t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Enrollment find(PartakeConnection con, EnrollmentPK key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, EnrollmentPK key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<Enrollment> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Enrollment> findByUserId(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
