package in.partake.model.dto;

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
	private Comparator<Enrollment> comparator;

	@Before
	public void createComparator() {
		comparator = Enrollment.getPriorityBasedComparator();
		Assert.assertNotNull(comparator);
	}

	@Test
	public void sortEmptyList() {
		List<Enrollment> list = Collections.emptyList(); 
		Collections.sort(list, comparator);
	}

	@Test
	public void sortAscSortedValues() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment("userID", "eventId", "comment", null, 0, null, null),
				new Enrollment("userID", "eventId", "comment", null, 1, null, null)
		});
		Collections.sort(list, comparator);
		Assert.assertTrue(list.get(0).getPriority() > list.get(1).getPriority());
	}

	@Test
	public void sortDescSortedValues() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment("userID", "eventId", "comment", null, 1, null, null),
				new Enrollment("userID", "eventId", "comment", null, 0, null, null)
		});
		Collections.sort(list, comparator);
		Assert.assertTrue(list.get(0).getPriority() > list.get(1).getPriority());
	}

	@Test
	public void sortSamePriorityValues() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment("userID", "eventId", "comment", null, 0, null, new Date(0)),
				new Enrollment("userID", "eventId", "comment", null, 0, null, new Date(1))
		});
		Collections.sort(list, comparator);
		Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
		Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) < 0);
	}

	@Test
	public void sortSamePriorityAndDateValues() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment("userID2", "eventId", "comment", null, 0, null, new Date(0)),
				new Enrollment("userID1", "eventId", "comment", null, 0, null, new Date(0))
		});
		Collections.sort(list, comparator);
		Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
		Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) == 0);
		Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) < 0);
	}

	@Test
	public void sortAllSameValues() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment("userID", "eventId", "comment", null, 0, null, new Date(0)),
				new Enrollment("userID", "eventId", "comment", null, 0, null, new Date(0))
		});
		Collections.sort(list, comparator);
		Assert.assertTrue(list.get(0).getPriority() == list.get(1).getPriority());
		Assert.assertTrue(list.get(0).getModifiedAt().compareTo(list.get(1).getModifiedAt()) == 0);
		Assert.assertTrue(list.get(0).getUserId().compareTo(list.get(1).getUserId()) == 0);
	}

	@Test
	public void sortNullValues() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				null, 
				null
		});
		Collections.sort(list, comparator);
		Assert.assertNull(list.get(0));
		Assert.assertNull(list.get(1));
	}

	@Test
	public void sortParicipationAndNull() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment(null, null, "comment", null, 0, null, null),
				null
		});
		Collections.sort(list, comparator);
		Assert.assertNull(list.get(0));
		Assert.assertNotNull(list.get(1));
	}

	// throwing NullPointerException is needed? really?
	@Test(expected = NullPointerException.class)
	public void sortNullId() {
		List<Enrollment> list = Arrays.asList(new Enrollment[] {
				new Enrollment(null, null, "comment", ParticipationStatus.CANCELLED, 0, LastParticipationStatus.CHANGED, null),
				new Enrollment(null, null, "comment", ParticipationStatus.CANCELLED, 0, LastParticipationStatus.CHANGED, null)
		});
		Collections.sort(list, comparator);
	}
}