package in.partake.model.dto;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class MessageTest extends AbstractPartakeModelTest<DirectMessage> {

	@Override
	protected DirectMessage createModel() {
		return new DirectMessage();
	}

	@Test
	public void testToCopy() {
		Date date = new Date(0L);
		DirectMessage message = new DirectMessage("id", "userId", "message", "eventId", date);
		DirectMessage copied = message.copy();
		Assert.assertEquals(message, copied);
		Assert.assertEquals(message, new DirectMessage("id", "userId", "message", "eventId", date));
		Assert.assertNotSame(message.getCreatedAt(), copied.getCreatedAt());

		// avoid NullPointerException at copy?
		try {
			new DirectMessage("id", "userId", "message", "eventId", null).copy();
		} catch (NullPointerException e) {
			Assert.fail("should do null check at copy");
		}
	}

	@Test
	public void getCreatedAtExecutesDefensiveCopy() {
		Date date = new Date(0L);
		DirectMessage message = new DirectMessage("id", "userId", "message", "eventId", date);
		Assert.assertNotSame(date, message.getCreatedAt());
		message.getCreatedAt().setTime(1L);
		Assert.assertEquals(date, message.getCreatedAt());

		// avoid NullPointerException at defensive copy?
		try {
			new DirectMessage("id", "userId", "message", "eventId", null).getCreatedAt();
		} catch (NullPointerException e) {
			Assert.fail("should do null check at defensive copy");
		}
	}

	@Test
	public void setCreatedAtExecutesDefensiveCopy() {
		Date date = new Date(0L);
		DirectMessage message = new DirectMessage("id", "userId", "message", "eventId", date);
		message.setCreatedAt(date);
		date.setTime(1L);
		Assert.assertEquals(0L, message.getCreatedAt().getTime());

		// avoid NullPointerException at defensive copy?
		try {
			new DirectMessage("id", "userId", "message", "eventId", date).setCreatedAt(null);
		} catch (NullPointerException e) {
			Assert.fail("should do null check at defensive copy");
		}
	}

	@Test
	public void constructorExecutesDefensiveCopy() {
		Date date = new Date(0L);
		DirectMessage message = new DirectMessage("id", "userId", "message", "eventId", date);
		date.setTime(1L);
		Assert.assertEquals(0L, message.getCreatedAt().getTime());

		// avoid NullPointerException at defensive copy?
		try {
			new DirectMessage("id", "userId", "message", "eventId", null);
		} catch (NullPointerException e) {
			Assert.fail("should do null check at defensive copy");
		}
	}
}
