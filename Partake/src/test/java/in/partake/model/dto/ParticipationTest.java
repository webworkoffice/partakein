package in.partake.model.dto;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author skypencil (@eller86)
 */
public final class ParticipationTest {
	@Test
	public void testCopyConstructor() {
		Participation[] samples = new Participation[]{
				new Participation(),
				new Participation("userId0", "comment-a", ParticipationStatus.NOT_ENROLLED, 0, LastParticipationStatus.NOT_ENROLLED, new Date()),
				new Participation("userId1", "comment-b", ParticipationStatus.ENROLLED, 1, LastParticipationStatus.ENROLLED, new Date()),
				new Participation("userId2", "comment-c", ParticipationStatus.RESERVED, 2, LastParticipationStatus.CHANGED, new Date()),
				new Participation("userId3", "comment-d", ParticipationStatus.CANCELLED, -1, LastParticipationStatus.NOT_ENROLLED, new Date()),
		};

		for (Participation source : samples) {
			// Participation class doesn't override #equals() method.
//			Assert.assertEquals(source, new Participation(source));

			Assert.assertEquals(source.getUserId(), new Participation(source).getUserId());
			Assert.assertEquals(source.getComment(), new Participation(source).getComment());
			Assert.assertEquals(source.getStatus(), new Participation(source).getStatus());
			Assert.assertEquals(source.getPriority(), new Participation(source).getPriority());
			Assert.assertEquals(source.getLastStatus(), new Participation(source).getLastStatus());
			Assert.assertEquals(source.getModifiedAt(), new Participation(source).getModifiedAt());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCopyConstructorByNullValue() {
		new Participation(null);
	}

	@Test
	public void testCopyConstructorByFlozenInstance() {
		Participation source = new Participation();
		Assert.assertFalse(source.isFrozen());

		source.freeze();
		Assert.assertTrue(source.isFrozen());

		Assert.assertFalse(new Participation(source).isFrozen());
	}
}
