package in.partake.view;

import org.apache.struts2.StrutsTestCase;
import org.junit.Assert;
import org.junit.Test;

import static in.partake.view.ViewHelper.h;

public class ViewHelperTest extends StrutsTestCase {
    
    @Test
    public void testToEscapeHTML() {
        Assert.assertEquals("", h(""));
        Assert.assertEquals("", h(null));

        Assert.assertEquals(" ", h(" "));
        Assert.assertEquals("test", h("test"));

        Assert.assertEquals("&amp;", h("&"));
        Assert.assertEquals("&lt;", h("<"));
        Assert.assertEquals("&gt;", h(">"));
        Assert.assertEquals("&quot;", h("\""));
        Assert.assertEquals("&apos;", h("\'"));

        Assert.assertEquals("", h(Character.toString('\0')));      // NUL
        Assert.assertEquals("", h(Character.toString('\u202E')));  // RLO

        Assert.assertEquals("\t", h("\t"));
        Assert.assertEquals("\r", h("\r"));
        Assert.assertEquals("\n", h("\n"));
        Assert.assertEquals("\r\n", h("\r\n"));

        Assert.assertEquals("&amp;&lt;tag&gt;", h("&<tag>"));
        Assert.assertEquals("漢字＆ひらがな", h("漢字＆ひらがな"));
        Assert.assertEquals("サロゲートペア→𠮟", h("サロゲートペア→𠮟"));
        Assert.assertEquals("double &quot;quoted&quot;", h("double \"quoted\""));
    }

    
    @Test
    public void testToCleanupHTML() throws Exception {
        String dirty = "<script>alert('hoge')</script>";
        String sanity = ViewHelper.cleanupHTML(dirty);
        
        System.out.println(sanity);
        Assert.assertTrue(!sanity.contains("script"));
    }
}
