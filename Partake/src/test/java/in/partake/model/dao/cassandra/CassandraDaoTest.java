package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;

import org.junit.Test;

class ConnectionGetter implements Runnable {
    private CassandraConnectionPool pool;
    private int howMany;
    private boolean doubly;
    
    public ConnectionGetter(CassandraConnectionPool pool, int howMany, boolean doubly) {
        this.pool = pool;
        this.howMany = howMany;
        this.doubly = doubly;
    }
    
    
    @Override
    public void run() {
        try {
            String name = Thread.currentThread().getName();
            for (int i = 0; i < howMany; ++i) {
                if (doubly) {
                    System.out.println(name);
                    CassandraConnection con1 = pool.getConnection();
                    System.out.println(name + " -1");
                    CassandraConnection con2 = pool.getConnection();
                    System.out.println(name + " -2");
                    con2.invalidate();
                    System.out.println(name + " -3");
                    con1.invalidate();               
                    System.out.println(name + " -4");
                } else {
                    CassandraConnection con = pool.getConnection();
                    con.invalidate();
                }
            }
        } catch (DAOException e) {
            
        }
    }
}

public class CassandraDaoTest {
    
    @Test
    public void testToGetConnectionAndInvalidate() throws DAOException {
        CassandraConnectionPool pool = new CassandraConnectionPool();
        
        CassandraConnection con = pool.getConnection();
        con.invalidate();
    }
    
    @Test
    public void testToGetALotOfConnection1() throws DAOException {
        CassandraConnectionPool pool = new CassandraConnectionPool();
        for (int i = 0; i < 10000; ++i) {
            CassandraConnection con = pool.getConnection();
            con.invalidate();
        }
    }
    
    @Test
    public void testToGetALotOfConnection2() throws DAOException {
        for (int i = 0; i < 10000; ++i) {
            CassandraConnectionPool pool = new CassandraConnectionPool();
            CassandraConnection con = pool.getConnection();
            con.invalidate();
        }
    }
    
    @Test
    public void testToGetALotOfConnectionUsingMultiThreads1() throws DAOException {
        testToGetALotOfConnectionUsingMultiThreads(10, 10000, false);
    }

    @Test
    public void testToGetALotOfConnectionUsingMultiThreads2() throws DAOException {
        testToGetALotOfConnectionUsingMultiThreads(100, 1000, false);
    }

    @Test
    public void testToGetALotOfConnectionUsingMultiThreads3() throws DAOException {
        testToGetALotOfConnectionUsingMultiThreads(10, 10000, true);
    }

    @Test
    public void testToGetALotOfConnectionUsingMultiThreads4() throws DAOException {
        testToGetALotOfConnectionUsingMultiThreads(50, 1000, true);
    }
    
    private void testToGetALotOfConnectionUsingMultiThreads(int N, int M, boolean doubly) {
        Thread[] ts = new Thread[N];
        for (int i = 0; i < N; ++i) {
            ts[i] = new Thread(new ConnectionGetter(new CassandraConnectionPool(), M, doubly));
        }
        
        
        for (int i = 0; i < N; ++i) {
            ts[i].start();
            System.out.println("starting... " + i);
        }
        
        System.out.println("All thread started.");
        
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // ignore.
            }
        }
    }
}
