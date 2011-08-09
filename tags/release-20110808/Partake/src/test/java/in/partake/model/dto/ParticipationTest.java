package in.partake.model.dto;

import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
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
public final class ParticipationTest extends AbstractPartakeModelTest<Enrollment> {
	Enrollment[] samples;

	@Before
	public void createSamples() {
		samples = new Enrollment[]{
				new Enrollment(),
				new Enrollment("userId0", "eventId0", "comment-a", ParticipationStatus.NOT_ENROLLED, false, ModificationStatus.NOT_ENROLLED, AttendanceStatus.UNKNOWN, new Date()),
				new Enrollment("userId1", "eventId1", "comment-b", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.ABSENT, new Date()),
				new Enrollment("userId2", "eventId2", "comment-c", ParticipationStatus.RESERVED, true, ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()),
				new Enrollment("userId3", "eventId3", "comment-d", ParticipationStatus.CANCELLED, false, ModificationStatus.NOT_ENROLLED, AttendanceStatus.UNKNOWN, new Date()),
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
			Assert.assertEquals(source.isVIP(), new Enrollment(source).isVIP());
			Assert.assertEquals(source.getModificationStatus(), new Enrollment(source).getModificationStatus());
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

	@Override
	protected Enrollment createModel() {
		return new Enrollment();
	}
}
