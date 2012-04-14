package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProvider;

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
public final class EnrollmentTest extends AbstractPartakeModelTest<Enrollment> {
    @Override
    protected Enrollment copy(Enrollment t) {
        return new Enrollment(t);
    }

    @Override
    protected TestDataProvider<Enrollment> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEnrollmentProvider();
    }

    private Enrollment[] samples;

    @Before
    public void createSamples() {
        samples = new Enrollment[]{
                new Enrollment(),
                new Enrollment("id1", "userId0", "eventId0", "comment-a", ParticipationStatus.NOT_ENROLLED, false, ModificationStatus.NOT_ENROLLED, AttendanceStatus.UNKNOWN, new Date()),
                new Enrollment("id2", "userId1", "eventId1", "comment-b", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.ABSENT, new Date()),
                new Enrollment("id3", "userId2", "eventId2", "comment-c", ParticipationStatus.RESERVED, true, ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()),
                new Enrollment("id4", "userId3", "eventId3", "comment-d", ParticipationStatus.CANCELLED, false, ModificationStatus.NOT_ENROLLED, AttendanceStatus.UNKNOWN, new Date()),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (Enrollment source : samples) {
            Assert.assertEquals(source.getUserId(), new Enrollment(source).getUserId());
            Assert.assertEquals(source.getComment(), new Enrollment(source).getComment());
            Assert.assertEquals(source.getStatus(), new Enrollment(source).getStatus());
            Assert.assertEquals(source.isVIP(), new Enrollment(source).isVIP());
            Assert.assertEquals(source.getModificationStatus(), new Enrollment(source).getModificationStatus());
            Assert.assertEquals(source.getModifiedAt(), new Enrollment(source).getModifiedAt());
            if (source.getModifiedAt() != null)
                Assert.assertNotSame(source.getModifiedAt(), new Enrollment(source).getModifiedAt());
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

    @Test
    public void testCopyConstructorByFlozenInstance() {
        Enrollment source = new Enrollment();
        Assert.assertFalse(source.isFrozen());

        source.freeze();
        Assert.assertTrue(source.isFrozen());

        Assert.assertFalse(new Enrollment(source).isFrozen());
    }

}
