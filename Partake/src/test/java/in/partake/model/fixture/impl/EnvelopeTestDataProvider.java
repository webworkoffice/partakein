package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dto.Envelope;
import in.partake.model.fixture.TestDataProvider;

/**
 * Envelope のテストデータを作成します。
 * @author shinyak
 *
 */
public class EnvelopeTestDataProvider extends TestDataProvider<Envelope> {
    @Override
    public Envelope create() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Envelope create(long pkNumber, String pkSalt, int objNumber) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEnvelopeAccess dao = daos.getEnvelopeAccess();
        dao.truncate(con);
    }
}
