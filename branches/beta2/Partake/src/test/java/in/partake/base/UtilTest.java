package in.partake.base;


import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class UtilTest {
	@Test
	public void testEnsureRange() {
		Assert.assertThat(Util.ensureRange(10, 0, 100), is(10));
		Assert.assertThat(Util.ensureRange(-10, 0, 100), is(0));
		Assert.assertThat(Util.ensureRange(110, 0, 100), is(100));
		Assert.assertThat(Util.ensureRange(0, 0, 100), is(0));
		Assert.assertThat(Util.ensureRange(1000, 0, 100), is(100));
		Assert.assertThat(Util.ensureRange(Integer.MIN_VALUE, 0, Integer.MAX_VALUE), is(0));
		Assert.assertThat(Util.ensureRange(Integer.MAX_VALUE, 0, Integer.MAX_VALUE), is(Integer.MAX_VALUE));
		Assert.assertThat(Util.ensureRange(0, 1, 100), is(1));
	}

	@Test
	public void hashtagValidatorTest() {
	    Assert.assertTrue(Util.isValidHashtag("#hashtag"));
	    Assert.assertTrue(Util.isValidHashtag("#hash_tag"));
	    Assert.assertTrue(Util.isValidHashtag("#hashtag1"));
	    Assert.assertTrue(Util.isValidHashtag("#hÀshtag"));
	    Assert.assertTrue(Util.isValidHashtag("＃hashtag"));
	    Assert.assertTrue(Util.isValidHashtag("#hashタグ"));
	    Assert.assertTrue(Util.isValidHashtag("#ﾊｯｼｭﾀｸﾞ"));
	    Assert.assertTrue(Util.isValidHashtag("#À"));
	    Assert.assertTrue(Util.isValidHashtag("#012"));

	    Assert.assertFalse(Util.isValidHashtag("#hash\\tag"));
	    Assert.assertFalse(Util.isValidHashtag("#hash-tag"));
	    Assert.assertFalse(Util.isValidHashtag("#らき☆すた"));
	    Assert.assertFalse(Util.isValidHashtag("#まどか☆マギカ"));
	    Assert.assertFalse(Util.isValidHashtag("これは#ダメ"));
	    Assert.assertFalse(Util.isValidHashtag("これも、#ダメ"));
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

	@Test
	public void shortenNegativeValueTest() {
		Assert.assertEquals("", Util.shorten("", -1));
	}

	@Test
	public void testToRemoveHash() {
	    Assert.assertEquals(null, Util.removeURLFragment(null));
	    Assert.assertEquals("", Util.removeURLFragment(""));
	    Assert.assertEquals("abc", Util.removeURLFragment("abc"));
	    Assert.assertEquals("日本語", Util.removeURLFragment("日本語"));
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeURLFragment("𠮟𠮟𠮟𠮟𠮟𠮟"));
	    Assert.assertEquals("", Util.removeURLFragment("#hoge"));
	    Assert.assertEquals("", Util.removeURLFragment("#日本語"));
	    Assert.assertEquals("", Util.removeURLFragment("#𠮟𠮟𠮟𠮟𠮟𠮟"));
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeURLFragment("𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟"));
	    Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeURLFragment("𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟"));
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

				Assert.assertEquals(yesterday, TimeUtil.oneDayBefore(now));
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

			Assert.assertEquals(yesterday, TimeUtil.oneDayBefore(now));
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

			Assert.assertEquals(yesterday, TimeUtil.oneDayBefore(now));
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

			Assert.assertEquals(yesterday, TimeUtil.oneDayBefore(now));
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
				DateTime yesterday = new DateTime(calendar.getTimeInMillis());

				Assert.assertEquals(yesterday, TimeUtil.halfDayBefore(now));
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

			Assert.assertEquals(yesterday, TimeUtil.halfDayBefore(now));
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

			Assert.assertEquals(yesterday, TimeUtil.halfDayBefore(now));
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

			Assert.assertEquals(yesterday, TimeUtil.halfDayBefore(now));
		}
	}

	// TODO Test for the summer time if support other timezone.

	@Test
	public void testEncodeURIComponent() {
//		Assert.assertEquals("", Util.encodeURIComponent(null));
		Assert.assertEquals("", Util.encodeURIComponent(""));
		Assert.assertEquals("%20!%22%23%24%25%26'()*%2B%2C-.%2F%3B%3F%3A%40%3D~", Util.encodeURIComponent(" !\"#$%&'()*+,-./;?:@=~"));
		Assert.assertEquals("Thyme%20%26time%3Dagain", Util.encodeURIComponent("Thyme &time=again"));
		Assert.assertEquals("%2521", Util.encodeURIComponent("%21"));
	}
}