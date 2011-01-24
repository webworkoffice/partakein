package in.partake.model.dao;

import in.partake.model.dto.BinaryData;


public interface IBinaryAccess {
    
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addBinaryWithId(PartakeConnection con, BinaryData data) throws DAOException;
    public BinaryData getBinaryById(PartakeConnection con, String id) throws DAOException;
    public void removeBinary(PartakeConnection con, String id) throws DAOException;
    
    /** Use ONLY in unit tests.*/
    public void truncate(PartakeConnection con) throws DAOException;
}
