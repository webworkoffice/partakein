package in.partake.model.dto;

import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author skypencil (@eller86)
 */
public final class ParticipationTest {
	Enrollment[] samples;

	@Before
	public void createSamples() {
		samples = new Enrollment[]{
				new Enrollment(),
				new Enrollment("userId0", "eventId0", "comment-a", ParticipationStatus.NOT_ENROLLED, 0, LastParticipationStatus.NOT_ENROLLED, new Date()),
				new Enrollment("userId1", "eventId1", "comment-b", ParticipationStatus.ENROLLED, 1, LastParticipationStatus.ENROLLED, new Date()),
				new Enrollment("userId2", "eventId2", "comment-c", ParticipationStatus.RESERVED, 2, LastParticipationStatus.CHANGED, new Date()),
				new Enrollment("userId3", "eventId3", "comment-d", ParticipationStatus.CANCELLED, -1, LastParticipationStatus.NOT_ENROLLED, new Date()),
		};
	}

	@Test
	public void testCopyConstructor() {
		for (Enrollment source : samples) {
			// Participation class doesn't override #equals() method.
//			Assert.assertEquals(source, new Participation(source));

			Assert.assertEquals(source.getUserId(), new Enrollment(source).getUserId());
			Assert.assertEquals(source.getComment(), new Enrollment(source).getComment());
			Assert.assertEquals(source.getStatus(), new Enrollment(source).getStatus());
			Assert.assertEquals(source.getPriority(), new Enrollment(source).getPriority());
			Assert.assertEquals(source.getLastStatus(), new Enrollment(source).getLastStatus());
			Assert.assertEquals(source.getModifiedAt(), new Enrollment(source).getModifiedAt());
			if (source.getModifiedAt() != null) {
				Assert.assertNotSame(source.getModifiedAt(), new Enrollment(source).getModifiedAt());
			}
		}
	}

	@Test
	public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
		for (Enrollment source : samples) {
			Enrollment copy = new Enrollment(source);

			for (Field field : Enrollment.class.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					Assert.assertEquals(field.get(source), field.get(copy));
				}
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCopyConstructorByNullValue() {
		new Enrollment(null);
	}

	@Test
	public void testCopyConstructorByFlozenInstance() {
		Enrollment source = new Enrollment();
		Assert.assertFalse(source.isFrozen());

		source.freeze();
		Assert.assertTrue(source.isFrozen());

		Assert.assertFalse(new Enrollment(source).isFrozen());
	}
}
