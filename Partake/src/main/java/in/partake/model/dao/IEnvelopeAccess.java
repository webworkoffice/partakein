package in.partake.model.dao;

import in.partake.model.dto.Envelope;

public interface IEnvelopeAccess extends IAccess<Envelope, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
}
