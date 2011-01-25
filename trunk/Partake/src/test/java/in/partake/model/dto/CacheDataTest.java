package in.partake.model.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CacheDataTest {
    private CacheData[] samples;
    
    @Before
    public void createSampleData() {
        Date now = new Date(new Date().getTime() + 2000); // 2 [s]
        samples = new CacheData[] {
                new CacheData(),
                new CacheData("id1", new byte[] { 0, 1, 2, 3, 4 }, now),
                new CacheData("id2", new byte[] { -1, 2, 3 }, now),                
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (CacheData source : samples) {
            Assert.assertEquals(source.getId(), new CacheData(source).getId());
            Assert.assertArrayEquals(source.getData(), new CacheData(source).getData());
            Assert.assertEquals(source.getInvalidAfter(), new CacheData(source).getInvalidAfter());
        }
    }

    @Test
    public void testEqualsMethod() {
        for (CacheData source : samples) {
            Assert.assertEquals(source, new CacheData(source));
        }
        
        for (CacheData d1 : samples) {
            for (CacheData d2 : samples) {
                if (d1 == d2) { continue; }
                Assert.assertFalse(d1.equals(d2));
            }
        }
    }
    
    @Test
    public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
        for (CacheData source : samples) {
            CacheData copy = new CacheData(source);

            for (Field field : CacheData.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    if (field.get(source) instanceof byte[]) {
                        Assert.assertArrayEquals((byte[]) field.get(source), (byte[]) field.get(copy));
                    } else {
                        Assert.assertEquals(field.get(source), field.get(copy));
                    }
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyConstructorByNullValue() {
        new CacheData(null);
    }

    @Test
    public void testCopyConstructorByFlozenInstance() {
        CacheData source = new CacheData();
        Assert.assertFalse(source.isFrozen());

        source.freeze();
        Assert.assertTrue(source.isFrozen());

        Assert.assertFalse(new CacheData(source).isFrozen());
    }
}
