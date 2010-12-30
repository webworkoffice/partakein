package in.partake.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

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
	    Assert.assertEquals("a𠮟𠮟...", Util.shorten("a𠮟𠮟𠮟𠮟𠮟𠮟𠮟", 6));
    }

	@Test(expected = NullPointerException.class)
	public void shortenNullValueTest() {
		Util.shorten(null, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shortenNegativeValueTest() {
		Util.shorten("", -1);
	}
	
	@Test
	public void testToRemoveHash() {
	    Assert.assertEquals(null, Util.removeHash(null));
	    Assert.assertEquals("", Util.removeHash(""));
	    Assert.assertEquals("abc", Util.removeHash("abc"));
	    Assert.assertEquals("日本語", Util.removeHash("日本語"));
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeHash("𠮟𠮟𠮟𠮟𠮟𠮟"));
	    Assert.assertEquals("", Util.removeHash("#hoge"));
	    Assert.assertEquals("", Util.removeHash("#日本語"));
	    Assert.assertEquals("", Util.removeHash("#𠮟𠮟𠮟𠮟𠮟𠮟"));
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeHash("𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟"));
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeHash("𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟"));
	}

	@Test
	public void removeTagsTest() {
	    Assert.assertEquals("abc", Util.removeTags("abc"));
	    Assert.assertEquals("abc", Util.removeTags("<p>abc</p>"));
	    Assert.assertEquals("abc", Util.removeTags("abc<br />"));
	    Assert.assertEquals("abc", Util.removeTags("ab<br />c"));
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
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >\n> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >>\n hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>\n> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >\n>\n hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>>\n hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>\n>\n hoge -->"));
	}

	private static final Calendar[] SUPPORTED_CALENDAR = {
		new GregorianCalendar(TimeZone.getTimeZone("JST"), Locale.JAPANESE)
	};

	@Test
	public void oneDayBeforeTest() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2010);
			calendar.set(Calendar.MONTH, 0);
	
			for (int day = 2; day <= 31; ++day) {
				calendar.set(Calendar.DAY_OF_MONTH, day);
				Date now = calendar.getTime();
				calendar.set(Calendar.DAY_OF_MONTH, day - 1);
				Date yesterday = calendar.getTime();
				Assert.assertEquals(yesterday, Util.oneDayBefore(now));
			}
		}
	}

	@Test
	public void oneDayBeforeTestAt1stDay() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2010);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date now = calendar.getTime();

			calendar.set(Calendar.YEAR, 2009);
			calendar.set(Calendar.MONTH, 11);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date yesterday = calendar.getTime();

			Assert.assertEquals(yesterday, Util.oneDayBefore(now));
		}
	}

	@Test
	public void oneDayBeforeTestAtNotLeapYear() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2011);	// is NOT leap year
			calendar.set(Calendar.MONTH, 2);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date now = calendar.getTime();

			calendar.set(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date yesterday = calendar.getTime();

			Assert.assertEquals(yesterday, Util.oneDayBefore(now));
		}
	}

	@Test
	public void oneDayBeforeTestAtLeapYear() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2012);	// is leap year
			calendar.set(Calendar.MONTH, 2);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date now = calendar.getTime();

			calendar.set(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 29);
			Date yesterday = calendar.getTime();

			Assert.assertEquals(yesterday, Util.oneDayBefore(now));
		}
	}

	@Test
	public void halfDayBeforeTest() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2010);
			calendar.set(Calendar.MONTH, 0);
	
			for (int day = 2; day <= 31; ++day) {
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				Date now = calendar.getTime();

				calendar.set(Calendar.DAY_OF_MONTH, day - 1);
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				Date yesterday = calendar.getTime();

				Assert.assertEquals(yesterday, Util.halfDayBefore(now));
			}
		}
	}

	@Test
	public void halfDayBeforeTestAt1stDay() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2010);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			Date now = calendar.getTime();

			calendar.set(Calendar.YEAR, 2009);
			calendar.set(Calendar.MONTH, 11);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			Date yesterday = calendar.getTime();

			Assert.assertEquals(yesterday, Util.halfDayBefore(now));
		}
	}

	@Test
	public void halfDayBeforeTestAtNotLeapYear() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2011);	// is NOT leap year
			calendar.set(Calendar.MONTH, 2);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			Date now = calendar.getTime();

			calendar.set(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 28);
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			Date yesterday = calendar.getTime();

			Assert.assertEquals(yesterday, Util.halfDayBefore(now));
		}
	}

	@Test
	public void halfDayBeforeTestAtLeapYear() {
		for (Calendar calendar : SUPPORTED_CALENDAR) {
			calendar.clear();
			calendar.set(Calendar.YEAR, 2012);	// is leap year
			calendar.set(Calendar.MONTH, 2);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			Date now = calendar.getTime();

			calendar.set(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 29);
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			Date yesterday = calendar.getTime();

			Assert.assertEquals(yesterday, Util.halfDayBefore(now));
		}
	}

	// TODO Test for the summer time if support other timezone.


	@Test
	public void htmlEscapeTest() {
		Assert.assertEquals("", Util.h(""));
		Assert.assertEquals("", Util.h(null));

		Assert.assertEquals(" ", Util.h(" "));
		Assert.assertEquals("test", Util.h("test"));

		Assert.assertEquals("&amp;", Util.h("&"));
		Assert.assertEquals("&lt;", Util.h("<"));
		Assert.assertEquals("&gt;", Util.h(">"));
		Assert.assertEquals("&quot;", Util.h("\""));
		Assert.assertEquals("&apos;", Util.h("\'"));

		Assert.assertEquals("", Util.h(Character.toString('\0')));    	// NUL
		Assert.assertEquals("", Util.h(Character.toString('\u202E')));	// RLO

		Assert.assertEquals("\t", Util.h("\t"));
		Assert.assertEquals("\r", Util.h("\r"));
		Assert.assertEquals("\n", Util.h("\n"));
		Assert.assertEquals("\r\n", Util.h("\r\n"));

		Assert.assertEquals("&amp;&lt;tag&gt;", Util.h("&<tag>"));
		Assert.assertEquals("漢字＆ひらがな", Util.h("漢字＆ひらがな"));
		Assert.assertEquals("サロゲートペア→𠮟", Util.h("サロゲートペア→𠮟"));
		Assert.assertEquals("double &quot;quoted&quot;", Util.h("double \"quoted\""));
	}

	@Test
	public void testEncodeURIComponent() {
//		Assert.assertEquals("", Util.encodeURIComponent(null));
		Assert.assertEquals("", Util.encodeURIComponent(""));
		Assert.assertEquals("%20!%22%23%24%25%26'()*%2B%2C-.%2F%3B%3F%3A%40%3D", Util.encodeURIComponent(" !\"#$%&'()*+,-./;?:@="));
		Assert.assertEquals("Thyme%20%26time%3Dagain", Util.encodeURIComponent("Thyme &time=again"));
		Assert.assertEquals("%2521", Util.encodeURIComponent("%21"));
	}
}