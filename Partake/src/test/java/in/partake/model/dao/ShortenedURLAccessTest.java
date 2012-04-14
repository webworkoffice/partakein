package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;

import org.junit.Before;

public class ShortenedURLAccessTest extends AbstractDaoTestCaseBase<IURLShortenerAccess, ShortenedURLData, ShortenedURLDataPK> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getURLShortenerAccess());
    }

    @Override
    protected ShortenedURLData create(long pkNumber, String pkSalt, int objNumber) {
        if (pkSalt.contains("putfind")) {
            StringBuilder longURL = new StringBuilder();
            longURL.append("http://www.example.com/");
            for (int i = 0; i < 8192; ++i) {
                longURL.append('a');
            }
            longURL.append(pkNumber);
            return new ShortenedURLData(longURL.toString(), "bitly", "http://examp.le/" + objNumber);
        } else {
            return new ShortenedURLData("http://www.example.com/" + pkSalt + pkNumber, "bitly", "http://examp.le/" + objNumber);
        }
    }
}
