package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ThumbnailData;

import java.util.List;

public interface IThumbnailAccess extends IAccess<ThumbnailData, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    // Should return ids ORDER BY createdAt DESC
    public List<String> findIdsByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
    public int countByUserId(PartakeConnection con, String userId) throws DAOException;
}
