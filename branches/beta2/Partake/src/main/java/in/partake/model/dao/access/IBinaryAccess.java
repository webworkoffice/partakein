package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.BinaryData;

import java.util.List;

// TODO: Rename this to IImageAccess.
public interface IBinaryAccess extends IAccess<BinaryData, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public List<String> findIdsByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
}
