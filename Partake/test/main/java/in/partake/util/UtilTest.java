package in.partake.util;

import in.partake.util.Util;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UtilTest {

	@Test
	public void testToRemoveTags() {
		assertEquals("hogefugahoge", Util.removeTags("<b>hoge<i>fuga</i>hoge</b>"));
		assertEquals("hogefugahoge", Util.removeTags("<b>hogefugahoge</b><a href=\"hogehoge\"></a>"));
		assertEquals("hogefugahoge", Util.removeTags("<b hoge=\"fuga\">hoge<i>fuga</i>hoge</b>"));
	}
	
	@Test
	public void testToValidateHashtag() {
		Assert.assertTrue(Util.isValidHashtag("#abc"));
		Assert.assertTrue(Util.isValidHashtag("#ABC"));
		Assert.assertTrue(Util.isValidHashtag("#abc-abc"));
		Assert.assertTrue(Util.isValidHashtag("#abc_abc"));
		Assert.assertTrue(Util.isValidHashtag("#abcdefghijklmnopqrstuvwxyz"));
		Assert.assertTrue(Util.isValidHashtag("#ABCDEFGHIJKLMNOPQRSTUVWXYZ"));

		Assert.assertTrue(!Util.isValidHashtag("#"));

	}
}
