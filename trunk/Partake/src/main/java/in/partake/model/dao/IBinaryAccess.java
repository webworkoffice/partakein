package in.partake.model.dao;

import in.partake.model.dto.BinaryData;


public interface IBinaryAccess extends ITruncatable {
    
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addBinary(PartakeConnection con, BinaryData data) throws DAOException;
    public BinaryData getBinary(PartakeConnection con, String id) throws DAOException;
    public void removeBinary(PartakeConnection con, String id) throws DAOException;
    
}
