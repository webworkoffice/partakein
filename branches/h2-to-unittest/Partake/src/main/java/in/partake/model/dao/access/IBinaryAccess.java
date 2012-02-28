package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.BinaryData;


public interface IBinaryAccess extends IAccess<BinaryData, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;    
}
