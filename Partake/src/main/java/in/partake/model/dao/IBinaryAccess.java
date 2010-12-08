package in.partake.model.dao;

import in.partake.model.dto.BinaryData;


public interface IBinaryAccess {
    
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addBinaryWithId(PartakeConnection con, String id, BinaryData data) throws DAOException;
    public BinaryData getBinaryById(PartakeConnection con, String id) throws DAOException;
    public void removeBinary(PartakeConnection con, String id) throws DAOException;
}
