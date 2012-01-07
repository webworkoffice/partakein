package in.partake.model.dao;

import in.partake.model.dao.access.IQuestionnaireAccess;
import in.partake.model.dto.Questionnaire;
import in.partake.model.dto.auxiliary.QuestionnaireType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QuestionnaireAccessTest extends AbstractDaoTestCaseBase<IQuestionnaireAccess, Questionnaire, String> {

    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getQuestionnaireAccess());
    }

    @Override
    protected Questionnaire create(long pkNumber, String pkSalt, int objNumber) {
        return new Questionnaire("questionnaire-" + pkSalt + pkNumber, "eventId", "question", objNumber, QuestionnaireType.CHOICE, "answers");
    }

    @Test
    public void testToFindByEventId() throws Exception {
        PartakeConnection con = getPool().getConnection();        
        try {
            String eventId = "eventId-fbei-" + System.currentTimeMillis();

            List<Questionnaire> originals = new ArrayList<Questionnaire>();
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                String id = dao.getFreshId(con);
                Questionnaire q = new Questionnaire(id, eventId, "Q?", i, QuestionnaireType.TEXT, null);
                originals.add(q);
                dao.put(con, q);
            }
            con.commit();

            List<Questionnaire> fetched = dao.findQuestionnairesByEventId(con, eventId);

            Assert.assertEquals(10, fetched.size());
            for (int i = 0; i < 10; ++i) {
                Assert.assertEquals(originals.get(i), fetched.get(i));
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToRemoveByEventId() throws Exception {
        String eventId1 = "eventId-rbei-1-" + System.currentTimeMillis();
        String eventId2 = "eventId-rbei-2-" + System.currentTimeMillis();

        List<Questionnaire> originals1 = new ArrayList<Questionnaire>();
        List<Questionnaire> originals2 = new ArrayList<Questionnaire>();

        PartakeConnection con = getPool().getConnection();        
        try {
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                String id = dao.getFreshId(con);
                System.out.println(id);
                Questionnaire q = new Questionnaire(id, eventId1, "Q?", i, QuestionnaireType.TEXT, null);
                originals1.add(q);
                dao.put(con, q);
            }
            for (int i = 0; i < 10; ++i) {
                String id = dao.getFreshId(con);
                Questionnaire q = new Questionnaire(id, eventId2, "Q?", i, QuestionnaireType.TEXT, null);
                originals2.add(q);
                dao.put(con, q);
            }
            con.commit();
            System.out.println("hoge");

            {
                List<Questionnaire> fetched1 = dao.findQuestionnairesByEventId(con, eventId1);
                List<Questionnaire> fetched2 = dao.findQuestionnairesByEventId(con, eventId2);

                Assert.assertEquals(10, fetched1.size());
                Assert.assertEquals(10, fetched2.size());
                for (int i = 0; i < 10; ++i) {
                    Assert.assertEquals(originals1.get(i), fetched1.get(i));
                    Assert.assertEquals(originals2.get(i), fetched2.get(i));
                }
            }

            con.beginTransaction();
            dao.removeByEventId(con, eventId1);
            con.commit();

            {
                List<Questionnaire> fetched1 = dao.findQuestionnairesByEventId(con, eventId1);
                List<Questionnaire> fetched2 = dao.findQuestionnairesByEventId(con, eventId2);

                Assert.assertEquals(0, fetched1.size());
                Assert.assertEquals(10, fetched2.size());
                for (int i = 0; i < 10; ++i) {
                    Assert.assertEquals(originals2.get(i), fetched2.get(i));
                }
            }

        } finally {
            con.invalidate();
        }
    }
}
