package in.partake.view;

import org.junit.Assert;
import org.junit.Test;

public class ViewHelperTest {

    @Test
    public void testToCleanupHTML() throws Exception {
        String dirty = "<script>alert('hoge')</script>";
        String sanity = ViewHelper.cleanupHTML(dirty);
        
        System.out.println(sanity);
        Assert.assertTrue(!sanity.contains("script"));
    }
}
