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
}
