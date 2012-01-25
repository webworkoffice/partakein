package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

class EntityURLShortenerMapper extends Postgres9EntityDataMapper<ShortenedURLData> {   
    public ShortenedURLData map(JSONObject obj) {
        return new ShortenedURLData(obj).freeze();
    }
}

public class Postgres9UrlShortenerDao extends Postgres9Dao implements IURLShortenerAccess {
    static final String TABLE_NAME = "URLShortenerEntities";
    static final int CURRENT_VERSION = 1;
    static final String INDEX_TABLE_NAME = "URLShortenerIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityURLShortenerMapper mapper;

    public Postgres9UrlShortenerDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityURLShortenerMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, originalURL TEXT NOT NULL, serviceType TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "URL" + " ON " + INDEX_TABLE_NAME + "(originalURL, serviceType)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, ShortenedURLData t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "originalURL", "serviceType" }, new Object[] { t.getOriginalURL(), t.getServiceType() }); 
        if (id == null)
            id = entityDao.getFreshId(pcon);

        // TODO: EventRelation should be merged into Event.
        Postgres9Entity entity = new Postgres9Entity(id, CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());

        if (entityDao.exists(pcon, id))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "originalURL", "serviceType" } , new Object[] { id, t.getOriginalURL(), t.getServiceType() });
    }

    @Override
    public ShortenedURLData find(PartakeConnection con, ShortenedURLDataPK pk) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "originalURL", "serviceType" }, new Object[] { pk.getOriginalURL(), pk.getServiceType() }); 
        if (id == null)
            return null;

        return findById(con, id);
    }

    ShortenedURLData findById(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public void remove(PartakeConnection con, ShortenedURLDataPK pk) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "originalURL", "serviceType" }, new Object[] { pk.getOriginalURL(), pk.getServiceType() }); 
        if (id == null)
            return;

        removeById(con, id);
    }

    void removeById(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);        
    }

    @Override
    public DataIterator<ShortenedURLData> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, ShortenedURLData>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public ShortenedURLData findByURL(PartakeConnection con, String originalURL) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "originalURL" }, new Object[] { originalURL }); 
        if (id == null)
            return null;

        return findById(pcon, id);
    }

    @Override
    public void removeByURL(PartakeConnection con, String originalURL) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "originalURL" }, new Object[] { originalURL }); 
        if (id == null)
            return;

        removeById(pcon, id);
    }
}
