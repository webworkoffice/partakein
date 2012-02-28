package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Envelope;

public interface IEnvelopeAccess extends IAccess<Envelope, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
}
