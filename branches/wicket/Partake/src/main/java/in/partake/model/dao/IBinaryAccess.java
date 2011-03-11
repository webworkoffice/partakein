package in.partake.model.dao;

import in.partake.model.dto.BinaryData;


public interface IBinaryAccess extends IAccess<BinaryData, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;    
}
