package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.model.EnrollmentEx;
import in.partake.model.UserEx;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the comparator which created by {@link Enrollment#getPriorityBasedComparator()}.
 *
 * @see Enrollment#getPriorityBasedComparator()
 * @author skypencil (@eller86)
 */
public final class ParticipationComparatorTest {
    private Comparator<EnrollmentEx> comparator;
    private UserEx user;

    @Before
    public void createComparator() {
        comparator = EnrollmentEx.getPriorityBasedComparator();
        user = null;

        Assert.assertNotNull(comparator);
    }

    @Test
    public void sortEmptyList() {
        List<EnrollmentEx> list = Collections.emptyList();
        Collections.sort(list, comparator);
    }

    @Test
    public void sortAscSortedValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id1", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, null), user, 0),
                new EnrollmentEx(new Enrollment("id2", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, null), user, 1)
        });
        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() > list.get(1).getPriority());
    }

    @Test
    public void sortDescSortedValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id1", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, null), user, 1),
                new EnrollmentEx(new Enrollment("id2", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, null), user, 0),
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() > list.get(1).getPriority());
    }

    @Test
    public void sortSamePriorityValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id1", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
                new EnrollmentEx(new Enrollment("id2", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(1)), user, 0),
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
        Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) < 0);
    }

    @Test
    public void sortSamePriorityAndDateValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id1", "userID1", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
                new EnrollmentEx(new Enrollment("id2", "userID2", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
        Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) == 0);
        Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) < 0);
    }

    @Test
    public void sortAllSameValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id1", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
                new EnrollmentEx(new Enrollment("id2", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
        Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) == 0);
        Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) == 0);
    }

    @Test
    public void sortVariousValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id01", "userId00", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
                new EnrollmentEx(new Enrollment("id02", "userId01", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 1),
                new EnrollmentEx(new Enrollment("id03", "userId02", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 2),
                new EnrollmentEx(new Enrollment("id04", "userId03", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(1)), user, 0),
                new EnrollmentEx(new Enrollment("id05", "userId04", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(1)), user, 1),
                new EnrollmentEx(new Enrollment("id06", "userId05", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(1)), user, 2),
                new EnrollmentEx(new Enrollment("id07", "userId06", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(2)), user, 0),
                new EnrollmentEx(new Enrollment("id08", "userId07", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(2)), user, 1),
                new EnrollmentEx(new Enrollment("id09", "userId08", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(2)), user, 2),
                new EnrollmentEx(new Enrollment("id10", "userId10", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(0)), user, 0),
                new EnrollmentEx(new Enrollment("id11", "userId11", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(0)), user, 1),
                new EnrollmentEx(new Enrollment("id12", "userId12", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(0)), user, 2),
                new EnrollmentEx(new Enrollment("id13", "userId13", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(1)), user, 0),
                new EnrollmentEx(new Enrollment("id14", "userId14", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(1)), user, 1),
                new EnrollmentEx(new Enrollment("id15", "userId15", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(1)), user, 2),
                new EnrollmentEx(new Enrollment("id16", "userId16", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(2)), user, 0),
                new EnrollmentEx(new Enrollment("id17", "userId17", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(2)), user, 1),
                new EnrollmentEx(new Enrollment("id18", "userId18", new UUID(0, 0), "eventId", "comment", null, true,  null, null, new DateTime(2)), user, 2),
        });

        Collections.sort(list, comparator);

        Assert.assertEquals("userId12", list.get(0).getUserId());
        Assert.assertEquals("userId15", list.get(1).getUserId());
        Assert.assertEquals("userId18", list.get(2).getUserId());
        Assert.assertEquals("userId11", list.get(3).getUserId());
        Assert.assertEquals("userId14", list.get(4).getUserId());
        Assert.assertEquals("userId17", list.get(5).getUserId());
        Assert.assertEquals("userId10", list.get(6).getUserId());
        Assert.assertEquals("userId13", list.get(7).getUserId());
        Assert.assertEquals("userId16", list.get(8).getUserId());
        Assert.assertEquals("userId02", list.get(9).getUserId());
        Assert.assertEquals("userId05", list.get(10).getUserId());
        Assert.assertEquals("userId08", list.get(11).getUserId());
        Assert.assertEquals("userId01", list.get(12).getUserId());
        Assert.assertEquals("userId04", list.get(13).getUserId());
        Assert.assertEquals("userId07", list.get(14).getUserId());
        Assert.assertEquals("userId00", list.get(15).getUserId());
        Assert.assertEquals("userId03", list.get(16).getUserId());
        Assert.assertEquals("userId06", list.get(17).getUserId());
    }


    @Test
    public void sortNullValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                null,
                null
        });
        Collections.sort(list, comparator);
        Assert.assertNull(list.get(0));
        Assert.assertNull(list.get(1));
    }

    @Test
    public void sortParicipationAndNull() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("id", "userID", new UUID(0, 0), "eventId", "comment", null, false, null, null, new DateTime(0)), user, 0),
                null
        });

        Collections.sort(list, comparator);
        Assert.assertNull(list.get(0));
        Assert.assertNotNull(list.get(1));
    }

    // throwing NullPointerException is needed? really?
    @Test(expected = NullPointerException.class)
    public void sortNullId() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment(null, null, null, null, "comment", ParticipationStatus.CANCELLED, false, ModificationStatus.CHANGED, null, null), user, 0),
                new EnrollmentEx(new Enrollment(null, null, null, null, "comment", ParticipationStatus.CANCELLED, false, ModificationStatus.CHANGED, null, null), user, 0),
        });
        Collections.sort(list, comparator);
    }
}
