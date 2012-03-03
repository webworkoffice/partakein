package in.partake.model.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

import org.junit.Before;
import org.junit.Test;

public class EventRelationAccessTest extends AbstractDaoTestCaseBase<IEventRelationAccess, EventRelation, EventRelationPK> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventRelationAccess());
    }

    @Override
    protected EventRelation create(long pkNumber, String pkSalt, int objNumber) {
        return new EventRelation(pkSalt + pkNumber, pkSalt + pkNumber, (objNumber & 2) > 0, (objNumber & 1) > 0);
    }

    // TODO: findByEventId
    @Test
    public void testFindByEventId() throws DAOException {
        // Create several event relations.
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    EventRelation relation = new EventRelation("srcEventId-" + i, "dstEventId-" + i + "-" + j, true, false);
                    dao.put(con, relation);
                }
            }
            con.commit();
        } finally {
            con.invalidate();
        }

        // Checks find by event relations.
        con = getPool().getConnection();
        try {
            for (int i = 0; i < 10; ++i) {
                List<EventRelation> rels = dao.findByEventId(con, "srcEventId-" + i);
                Assert.assertEquals(10, rels.size());
                
                ArrayList<String> dsts = new ArrayList<String>();
                for (EventRelation rel : rels)
                    dsts.add(rel.getDstEventId());
                
                Collections.sort(dsts);
                for (int j = 0; j < 10; ++j) {
                    Assert.assertEquals("dstEventId-" + i + "-" + j, dsts.get(j));
                }
            }
        } finally {
            con.invalidate();
        }
    }

    // TODO: removeByEventId
    @Test
    public void testRemoveByEventId() throws DAOException {
        // Create several event relations.
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    EventRelation relation = new EventRelation("srcEventId-" + i, "dstEventId-" + i + "-" + j, true, false);
                    dao.put(con, relation);
                }
            }
            con.commit();
        } finally {
            con.invalidate();
        }

        // 
        con = getPool().getConnection();
        try { 
            con.beginTransaction();
            dao.removeByEventId(con, "srcEventId-" + 0);
            dao.removeByEventId(con, "srcEventId-" + 2);
            dao.removeByEventId(con, "srcEventId-" + 4);
            dao.removeByEventId(con, "srcEventId-" + 6);
            dao.removeByEventId(con, "srcEventId-" + 8);
            con.commit();
        } finally {
            con.invalidate();
        }
        
        // Checks find by event relations.
        con = getPool().getConnection();
        try {
            for (int i = 0; i < 10; ++i) {
                List<EventRelation> rels = dao.findByEventId(con, "srcEventId-" + i);
                if (i % 2 == 0) {
                    Assert.assertEquals(0, rels.size());
                } else {
                    Assert.assertEquals(10, rels.size());
                    
                    ArrayList<String> dsts = new ArrayList<String>();
                    for (EventRelation rel : rels)
                        dsts.add(rel.getDstEventId());
                    
                    Collections.sort(dsts);
                    for (int j = 0; j < 10; ++j) {
                        Assert.assertEquals("dstEventId-" + i + "-" + j, dsts.get(j));
                    }
                }
            }
        } finally {
            con.invalidate();
        }
    }
}
