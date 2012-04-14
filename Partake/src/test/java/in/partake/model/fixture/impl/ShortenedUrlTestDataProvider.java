package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;

public class ShortenedUrlTestDataProvider extends TestDataProvider<ShortenedURLData> {
    @Override
    public ShortenedURLData create(long pkNumber, String pkSalt, int objNumber) {
        return new ShortenedURLData("http://www.example.com/original/" + pkSalt + "/" + pkNumber, "bitly", "http://bit.ly/example/" + objNumber);
    }

    @Override
    public List<ShortenedURLData> createGetterSetterSamples() {
        List<ShortenedURLData> array = new ArrayList<ShortenedURLData>();
        array.add(new ShortenedURLData("http://www.example.com/original", "bitly", "http://short.en/ed"));
        array.add(new ShortenedURLData("http://www.example.com/original1", "bitly", "http://short.en/ed"));
        array.add(new ShortenedURLData("http://www.example.com/original", "bitly1", "http://short.en/ed"));
        array.add(new ShortenedURLData("http://www.example.com/original", "bitly", "http://short.en/ed1"));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getURLShortenerAccess().truncate(con);
    }
}
