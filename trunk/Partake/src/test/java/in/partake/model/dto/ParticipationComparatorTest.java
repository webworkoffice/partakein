package in.partake.model.dto;

import in.partake.model.EnrollmentEx;
import in.partake.model.UserEx;
import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, null), user, 0),
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, null), user, 1)
        });
        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() > list.get(1).getPriority());
    }

    @Test
    public void sortDescSortedValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, null), user, 1),
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, null), user, 0),                
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() > list.get(1).getPriority());
    }

    @Test
    public void sortSamePriorityValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(0)), user, 0),
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(1)), user, 0),                
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
        Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) < 0);
    }

    @Test
    public void sortSamePriorityAndDateValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(0)), user, 0),
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(0)), user, 0),                
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
        Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) == 0);
        Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) < 0);
    }

    @Test
    public void sortAllSameValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(0)), user, 0),
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(0)), user, 0),                
        });

        Collections.sort(list, comparator);
        Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
        Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) == 0);
        Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) == 0);
    }

    @Test
    public void sortVariousValues() {
        List<EnrollmentEx> list = Arrays.asList(new EnrollmentEx[] {
                new EnrollmentEx(new Enrollment("userID00", "eventId", "comment", null, false, null, new Date(0)), user, 0),
                new EnrollmentEx(new Enrollment("userID01", "eventId", "comment", null, false, null, new Date(0)), user, 1),
                new EnrollmentEx(new Enrollment("userID02", "eventId", "comment", null, false, null, new Date(0)), user, 2),
                new EnrollmentEx(new Enrollment("userID03", "eventId", "comment", null, false, null, new Date(1)), user, 0),                
                new EnrollmentEx(new Enrollment("userID04", "eventId", "comment", null, false, null, new Date(1)), user, 1),
                new EnrollmentEx(new Enrollment("userID05", "eventId", "comment", null, false, null, new Date(1)), user, 2),
                new EnrollmentEx(new Enrollment("userID06", "eventId", "comment", null, false, null, new Date(2)), user, 0),
                new EnrollmentEx(new Enrollment("userID07", "eventId", "comment", null, false, null, new Date(2)), user, 1),                
                new EnrollmentEx(new Enrollment("userID08", "eventId", "comment", null, false, null, new Date(2)), user, 2),                
                new EnrollmentEx(new Enrollment("userID10", "eventId", "comment", null, false, null, new Date(0)), user, 0),
                new EnrollmentEx(new Enrollment("userID11", "eventId", "comment", null, false, null, new Date(0)), user, 1),
                new EnrollmentEx(new Enrollment("userID12", "eventId", "comment", null, false, null, new Date(0)), user, 2),
                new EnrollmentEx(new Enrollment("userID13", "eventId", "comment", null, false, null, new Date(1)), user, 0),                
                new EnrollmentEx(new Enrollment("userID14", "eventId", "comment", null, false, null, new Date(1)), user, 1),
                new EnrollmentEx(new Enrollment("userID15", "eventId", "comment", null, false, null, new Date(1)), user, 2),
                new EnrollmentEx(new Enrollment("userID16", "eventId", "comment", null, false, null, new Date(2)), user, 0),
                new EnrollmentEx(new Enrollment("userID17", "eventId", "comment", null, false, null, new Date(2)), user, 1),                
                new EnrollmentEx(new Enrollment("userID18", "eventId", "comment", null, false, null, new Date(2)), user, 2),                
        });

        Collections.sort(list, comparator);
        Assert.assertEquals("userId00", list.get(15).getUserId());
        Assert.assertEquals("userId01", list.get(12).getUserId());
        Assert.assertEquals("userId02", list.get(9).getUserId());
        Assert.assertEquals("userId03", list.get(16).getUserId());
        Assert.assertEquals("userId04", list.get(13).getUserId());
        Assert.assertEquals("userId05", list.get(10).getUserId());
        Assert.assertEquals("userId06", list.get(17).getUserId());
        Assert.assertEquals("userId07", list.get(14).getUserId());
        Assert.assertEquals("userId08", list.get(11).getUserId());
        
        Assert.assertEquals("userId10", list.get(6).getUserId());
        Assert.assertEquals("userId11", list.get(3).getUserId());
        Assert.assertEquals("userId12", list.get(0).getUserId());
        Assert.assertEquals("userId13", list.get(7).getUserId());
        Assert.assertEquals("userId14", list.get(4).getUserId());
        Assert.assertEquals("userId15", list.get(1).getUserId());
        Assert.assertEquals("userId16", list.get(8).getUserId());
        Assert.assertEquals("userId17", list.get(5).getUserId());
        Assert.assertEquals("userId18", list.get(2).getUserId());
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
                new EnrollmentEx(new Enrollment("userID", "eventId", "comment", null, false, null, new Date(0)), user, 0),
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
                new EnrollmentEx(new Enrollment(null, null, "comment", ParticipationStatus.CANCELLED, false, LastParticipationStatus.CHANGED, null), user, 0),
                new EnrollmentEx(new Enrollment(null, null, "comment", ParticipationStatus.CANCELLED, false, LastParticipationStatus.CHANGED, null), user, 0),
        });
        Collections.sort(list, comparator);
    }
}