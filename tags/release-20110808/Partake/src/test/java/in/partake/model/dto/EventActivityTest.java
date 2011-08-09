package in.partake.model.dto;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class EventActivityTest extends AbstractPartakeModelTest<EventActivity> {

	@Override
	protected EventActivity createModel() {
		return new EventActivity();
	}

	@Test
	public void testToCopy() {
		Date date = new Date(0L);
		EventActivity activity = new EventActivity("id", "userId", "title", "content", date);
		EventActivity copied = activity.copy();
		Assert.assertEquals(activity, copied);
		Assert.assertEquals(activity, new EventActivity("id", "userId", "title", "content", date));
		Assert.assertNotSame(activity.getCreatedAt(), copied.getCreatedAt());

		// avoid NullPointerException at copy?
		try {
			new EventActivity("id", "userId", "title", "content", null).copy();
		} catch (NullPointerException e) {
			Assert.fail("should do null check at copy");
		}
	}

	@Test
	public void getCreatedAtExecutesDefensiveCopy() {
		Date date = new Date(0L);
		EventActivity activity = new EventActivity("id", "userId", "title", "content", date);
		Assert.assertNotSame(date, activity.getCreatedAt());
		activity.getCreatedAt().setTime(1L);
		Assert.assertEquals(date, activity.getCreatedAt());

		// avoid NullPointerException at defensive copy?
		try {
			new EventActivity("id", "userId", "title", "content", null).getCreatedAt();
		} catch (NullPointerException e) {
			Assert.fail("should do null check at defensive copy");
		}
	}

	@Test
	public void setCreatedAtExecutesDefensiveCopy() {
		Date date = new Date(0L);
		EventActivity activity = new EventActivity("id", "userId", "title", "content", date);
		activity.setCreatedAt(date);
		date.setTime(1L);
		Assert.assertEquals(0L, activity.getCreatedAt().getTime());

		// avoid NullPointerException at defensive copy?
		try {
			new EventActivity("id", "userId", "title", "content", date).setCreatedAt(null);
		} catch (NullPointerException e) {
			Assert.fail("should do null check at defensive copy");
		}
	}

	@Test
	public void constructorExecutesDefensiveCopy() {
		Date date = new Date(0L);
		EventActivity activity = new EventActivity("id", "userId", "title", "content", date);
		date.setTime(1L);
		Assert.assertEquals(0L, activity.getCreatedAt().getTime());

		// avoid NullPointerException at defensive copy?
		try {
			new EventActivity("id", "userId", "title", "content", null);
		} catch (NullPointerException e) {
			Assert.fail("should do null check at defensive copy");
		}
	}
}
