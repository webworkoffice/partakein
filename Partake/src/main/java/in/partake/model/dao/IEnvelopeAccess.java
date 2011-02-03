package in.partake.model.dao;

import in.partake.model.dto.Envelope;

public interface IEnvelopeAccess extends ITruncatable {
    public String getFreshId(PartakeConnection con) throws DAOException;
    public void enqueueEnvelope(PartakeConnection con, Envelope envelope) throws DAOException;    
    public DataIterator<Envelope> getEnvelopeIterator(PartakeConnection con) throws DAOException;

}
