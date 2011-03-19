package in.partake.util;

import org.junit.Assert;
import org.junit.Test;

public class PairTest {

    @Test
    public void testCreateAndGet() {
        ComparablePair<Integer, Integer> pair = new ComparablePair<Integer, Integer>(1, 2);
        Assert.assertEquals(Integer.valueOf(1), pair.getFirst());
        Assert.assertEquals(Integer.valueOf(2), pair.getSecond());
    }

    @Test
    public void testEquals() {
        ComparablePair<Integer, Integer> pair1 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair2 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair3 = new ComparablePair<Integer, Integer>(1, 3);
        ComparablePair<Integer, Integer> pair4 = new ComparablePair<Integer, Integer>(2, 2);
        
        Assert.assertEquals(pair1, pair2);
        Assert.assertFalse(pair1.equals(pair3));
        Assert.assertFalse(pair1.equals(pair4));
        Assert.assertFalse(pair1.equals(null));
    }
    
    @Test
    public void testCompare() {
        ComparablePair<Integer, Integer> pair1 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair2 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair3 = new ComparablePair<Integer, Integer>(1, 3);
        ComparablePair<Integer, Integer> pair4 = new ComparablePair<Integer, Integer>(2, 2);
        ComparablePair<Integer, Integer> pair5 = new ComparablePair<Integer, Integer>(0, 2);
        
        Assert.assertTrue(pair1.compareTo(pair2) == 0);
        Assert.assertTrue(pair1.compareTo(pair3) < 0);
        Assert.assertTrue(pair1.compareTo(pair4) < 0);
        Assert.assertTrue(pair1.compareTo(pair5) > 0);
    }

    @Test(expected = NullPointerException.class)
    public void testCompareToNull() {
    	new ComparablePair<Integer, Integer>(1, 2).compareTo(null);
    }
}
