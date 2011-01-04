package in.partake.model.dto;

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
	Participation[] samples;

	@Before
	public void createSamples() {
		samples = new Participation[]{
				new Participation(),
				new Participation("userId0", "comment-a", ParticipationStatus.NOT_ENROLLED, 0, LastParticipationStatus.NOT_ENROLLED, new Date()),
				new Participation("userId1", "comment-b", ParticipationStatus.ENROLLED, 1, LastParticipationStatus.ENROLLED, new Date()),
				new Participation("userId2", "comment-c", ParticipationStatus.RESERVED, 2, LastParticipationStatus.CHANGED, new Date()),
				new Participation("userId3", "comment-d", ParticipationStatus.CANCELLED, -1, LastParticipationStatus.NOT_ENROLLED, new Date()),
		};
	}

	@Test
	public void testCopyConstructor() {
		for (Participation source : samples) {
			// Participation class doesn't override #equals() method.
//			Assert.assertEquals(source, new Participation(source));

			Assert.assertEquals(source.getUserId(), new Participation(source).getUserId());
			Assert.assertEquals(source.getComment(), new Participation(source).getComment());
			Assert.assertEquals(source.getStatus(), new Participation(source).getStatus());
			Assert.assertEquals(source.getPriority(), new Participation(source).getPriority());
			Assert.assertEquals(source.getLastStatus(), new Participation(source).getLastStatus());
			Assert.assertEquals(source.getModifiedAt(), new Participation(source).getModifiedAt());
			if (source.getModifiedAt() != null) {
				Assert.assertNotSame(source.getModifiedAt(), new Participation(source).getModifiedAt());
			}
		}
	}

	@Test
	public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
		for (Participation source : samples) {
			Participation copy = new Participation(source);

			for (Field field : Participation.class.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					Assert.assertEquals(field.get(source), field.get(copy));
				}
			}
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
