package in.partake.util;

import java.util.Date;

import org.junit.Test;

import junit.framework.Assert;


public class UtilTest {

	@Test
	public void dateConverterTest() {
		Date date1 = new Date();
		Date date2 = Util.dateFromTimeString(Util.getTimeString(date1));
		Assert.assertEquals(date1, date2);
	}

	@Test
	public void dateConverterCornerTest1() {
		Date date1 = new Date(Long.MAX_VALUE);
		Date date2 = Util.dateFromTimeString(Util.getTimeString(date1));
		Assert.assertEquals(date1, date2);
	}

	@Test
	public void dateConverterCornerTest2() {
		Date date1 = new Date(0);
		Date date2 = Util.dateFromTimeString(Util.getTimeString(date1));
		Assert.assertEquals(date1, date2);
	}

	@Test
	public void hashtagValidatorTest() {
	    Assert.assertTrue(Util.isValidHashtag("#hashtag"));
	    Assert.assertTrue(Util.isValidHashtag("#hash_tag"));
	    Assert.assertTrue(Util.isValidHashtag("#hashtag1"));
	    Assert.assertTrue(Util.isValidHashtag("#hÀshtag"));
	    Assert.assertTrue(Util.isValidHashtag("＃hashtag"));

	    Assert.assertFalse(Util.isValidHashtag("#012"));
	    Assert.assertFalse(Util.isValidHashtag("#hash\\tag"));
	    Assert.assertFalse(Util.isValidHashtag("#hash-tag"));
	    Assert.assertFalse(Util.isValidHashtag("#hashタグ"));
	    Assert.assertFalse(Util.isValidHashtag("#À"));
	}
	
	@Test
    public void shortenAlphabetTest() {
        Assert.assertEquals("ABCAB", Util.shorten("ABCAB", 6)); 
        Assert.assertEquals("ABCABC", Util.shorten("ABCABC", 6)); 
        Assert.assertEquals("ABC...", Util.shorten("ABCABCD", 6)); 
        Assert.assertEquals("ABC...", Util.shorten("ABCABCDE", 6));

        Assert.assertEquals("", Util.shorten("ABCABC", 0)); 
        Assert.assertEquals(".", Util.shorten("ABCABC", 1)); 
        Assert.assertEquals("..", Util.shorten("ABCABC", 2)); 
        Assert.assertEquals("...", Util.shorten("ABCABC", 3));
	}
	
	@Test
    public void shortenJapaneseTest() {
        Assert.assertEquals("日本語", Util.shorten("日本語", 6)); 
        Assert.assertEquals("日本語...", Util.shorten("日本語は難しい", 6)); 
        Assert.assertEquals("日本語...", Util.shorten("日本語難しすぎ", 6)); 
        Assert.assertEquals("日本語...", Util.shorten("日本語aほえほえ", 6)); 
    }
	
	@Test
    public void shortenSurrogatePairTest() {
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.shorten("𠮟𠮟𠮟𠮟𠮟𠮟", 6));
	    Assert.assertEquals("𠮟𠮟𠮟...", Util.shorten("𠮟𠮟𠮟𠮟𠮟𠮟𠮟", 6));
    }
	
	@Test
	public void removeTagsTest() {
	    Assert.assertEquals("abc", Util.removeTags("abc"));
	    Assert.assertEquals("abc", Util.removeTags("<p>abc</p>"));
	    Assert.assertEquals("abc", Util.removeTags("abc<br />"));
	    Assert.assertEquals("abc", Util.removeTags("<br />abc"));
	    Assert.assertEquals("abc", Util.removeTags("abc<br>"));
	    Assert.assertEquals("abc", Util.removeTags("<br>abc"));
	    Assert.assertEquals("abc", Util.removeTags("abc<!-- comment -->"));
	    Assert.assertEquals("abc", Util.removeTags("<!-- comment -->abc"));
	    Assert.assertEquals("ab\r\nc", Util.removeTags("<p>ab\r\nc</p>"));
	    Assert.assertEquals("ab\r\nc", Util.removeTags("<p \r\n>ab\r\nc</p>"));
	    Assert.assertEquals("ab\r\nc", Util.removeTags("<p>ab\r\nc</p \r\n>"));
	    Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >> hoge -->"));
	    Assert.assertEquals("abc", Util.removeTags("abc<!-- comment << hoge -->"));
	    Assert.assertEquals("abc", Util.removeTags("abc<!-- comment <> hoge -->"));
	    Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >< hoge -->"));
	}
}
