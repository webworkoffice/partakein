package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityEnrollmentMapper extends Postgres9EntityDataMapper<Enrollment> {
    public Enrollment map(JSONObject obj) {
        return new Enrollment(obj).freeze();
    }
}

public class Postgres9EnrollmentDao extends Postgres9Dao implements IEnrollmentAccess {
    static final String TABLE_NAME = "EnrollmentEntities";
    static final int CURRENT_VERSION = 1;
    static final String INDEX_TABLE_NAME = "EnrollmentIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEnrollmentMapper mapper;

    public Postgres9EnrollmentDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEnrollmentMapper();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, userId TEXT NOT NULL, ticketId TEXT NOT NULL, eventId TEXT NOT NULL, status, enrolledAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "UserIdTicketId" + " ON " + INDEX_TABLE_NAME + "(userId, ticketId)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "TicketId" + " ON " + INDEX_TABLE_NAME + "(ticketId, enrolledAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "TicketIdStatus" + " ON " + INDEX_TABLE_NAME + "(ticketId, status, enrolledAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, enrolledAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventIdStatus" + " ON " + INDEX_TABLE_NAME + "(ticketId, status, enrolledAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId, enrolledAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, Enrollment t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDate());

        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon,
                new String[] { "id", "userId", "ticketId", "eventId", "status", "enrolledAt" },
                new Object[] { t.getId(), t.getUserId(), t.getTicketId().toString(), t.getEventId(), t.getStatus().toString(), t.getModifiedAt() });
    }

    @Override
    public Enrollment find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<Enrollment> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, Enrollment>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public Enrollment findByTicketIdAndUserId(PartakeConnection con, UUID ticketId, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "userId", "ticketId" }, new Object[] { userId, ticketId.toString() });
        if (id == null)
            return null;

        return find(con, id);
    }

    @Override
    public void removeByEventTicketIdAndUserId(PartakeConnection con, UUID eventTicketId, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "userId", "ticketId" }, new Object[] { userId, eventTicketId.toString() });
        if (id == null)
            return;

        remove(con, id);
    }

    @Override
    public List<Enrollment> findByTicketId(PartakeConnection con, UUID eventTicketId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE ticketId = ? ORDER BY enrolledAt DESC OFFSET ? LIMIT ?",
                new Object[] { eventTicketId.toString(), offset, limit });

        Postgres9IdMapper<Enrollment> idMapper = new Postgres9IdMapper<Enrollment>((Postgres9Connection) con, mapper, entityDao);
        DataIterator<Enrollment> it = new Postgres9DataIterator<Enrollment>(idMapper, psars);
        return DAOUtil.freeze(DAOUtil.convertToList(it));
    }

    @Override
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY enrolledAt DESC OFFSET ? LIMIT ?",
                new Object[] { eventId, offset, limit });

        Postgres9IdMapper<Enrollment> idMapper = new Postgres9IdMapper<Enrollment>((Postgres9Connection) con, mapper, entityDao);
        DataIterator<Enrollment> it = new Postgres9DataIterator<Enrollment>(idMapper, psars);
        return DAOUtil.freeze(DAOUtil.convertToList(it));
    }


    @Override
    public List<Enrollment> findByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE userId = ? ORDER BY enrolledAt DESC OFFSET ? LIMIT ?",
                new Object[] { userId, offset, limit });

        Postgres9IdMapper<Enrollment> idMapper = new Postgres9IdMapper<Enrollment>((Postgres9Connection) con, mapper, entityDao);
        DataIterator<Enrollment> it = new Postgres9DataIterator<Enrollment>(idMapper, psars);
        return DAOUtil.freeze(DAOUtil.convertToList(it));
    }

    @Override
    public int countByUserId(PartakeConnection con, String userId, ParticipationStatus status) throws DAOException {
        return indexDao.count((Postgres9Connection) con,
                new String[] { "userId", "status" },
                new String[] { userId, status.toString() });
    }

    @Override
    public int countByUserId(PartakeConnection con, String userId) throws DAOException {
        return indexDao.count((Postgres9Connection) con, "userId", userId);
    }

    @Override
    public int countByTicketId(PartakeConnection con, UUID eventTicketId, ParticipationStatus status) throws DAOException {
        return indexDao.count((Postgres9Connection) con,
                new String[] { "ticketId", "status" },
                new Object[] { eventTicketId.toString(), status.toString() });
    }

    @Override
    public int countByEventId(PartakeConnection con, String eventId, ParticipationStatus status) throws DAOException {
        return indexDao.count((Postgres9Connection) con,
                new String[] { "eventId", "status" },
                new Object[] { eventId.toString(), status.toString() });
    }


    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
