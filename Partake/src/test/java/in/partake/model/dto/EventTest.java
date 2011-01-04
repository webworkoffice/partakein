package in.partake.model.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author skypencil (@eller86)
 */
public final class EventTest {
	Event[] samples;

	@Before
	public void createSamples() {
		samples = new Event[] {
				new Event(),
				new Event("id", "shortId", "title", "summary", "category",
						new Date(), new Date(), new Date(), 0, "url", "place",
						"address", "description", "#hashTag", "ownerId", null,
						"foreImageId", "backImageId", true, "passcode",
						new Date(), new Date(), -1),
				new Event("id2", "shortId2", "title2", "summary2", "category2",
						new Date(1), new Date(2), new Date(3), 1, "url2", "place2",
						"address2", "description2", "#hashTag2", "ownerId2", new ArrayList<String>(),
						"foreImageId2", "backImageId2", false, "passcode2",
						new Date(4), new Date(5), 1)
		};
	}

	@Test
	public void testCopyConstructor() {
		for (Event source : samples) {
			// Event class doesn't override #equals() method.
			// Assert.assertEquals(source, new Event(source));

			Assert.assertEquals(source.getId(), new Event(source).getId());
			Assert.assertEquals(source.getShortId(), new Event(source).getShortId());
			Assert.assertEquals(source.getTitle(), new Event(source).getTitle());
			Assert.assertEquals(source.getSummary(), new Event(source).getSummary());
			Assert.assertEquals(source.getCategory(), new Event(source).getCategory());
			Assert.assertEquals(source.getDeadline(), new Event(source).getDeadline());
			Assert.assertEquals(source.getBeginDate(), new Event(source).getBeginDate());
			Assert.assertEquals(source.getEndDate(), new Event(source).getEndDate());
			Assert.assertEquals(source.getCapacity(), new Event(source).getCapacity());
			Assert.assertEquals(source.getUrl(), new Event(source).getUrl());
			Assert.assertEquals(source.getPlace(), new Event(source).getPlace());
			Assert.assertEquals(source.getAddress(), new Event(source).getAddress());
			Assert.assertEquals(source.getDescription(), new Event(source).getDescription());
			Assert.assertEquals(source.getHashTag(), new Event(source).getHashTag());
			Assert.assertEquals(source.getOwnerId(), new Event(source).getOwnerId());
			Assert.assertEquals(source.getManagerScreenNames(), new Event(source).getManagerScreenNames());
			Assert.assertEquals(source.getForeImageId(), new Event(source).getForeImageId());
			Assert.assertEquals(source.getBackImageId(), new Event(source).getBackImageId());
			Assert.assertEquals(source.isPrivate(), new Event(source).isPrivate());
			Assert.assertEquals(source.getPasscode(), new Event(source).getPasscode());
			Assert.assertEquals(source.getCreatedAt(), new Event(source).getCreatedAt());
			Assert.assertEquals(source.getModifiedAt(), new Event(source).getModifiedAt());
			Assert.assertEquals(source.getRevision(), new Event(source).getRevision());

			if (source.getDeadline() != null) {
				Assert.assertNotSame(source.getDeadline(), new Event(source).getDeadline());
			}
			if (source.getBeginDate() != null) {
				Assert.assertNotSame(source.getBeginDate(), new Event(source).getBeginDate());
			}
			if (source.getEndDate() != null) {
				Assert.assertNotSame(source.getEndDate(), new Event(source).getEndDate());
			}
			if (source.getManagerScreenNames() != null) {
				Assert.assertNotSame(source.getManagerScreenNames(), new Event(source).getManagerScreenNames());
			}
			if (source.getCreatedAt() != null) {
				Assert.assertNotSame(source.getCreatedAt(), new Event(source).getCreatedAt());
			}
			if (source.getModifiedAt() != null) {
				Assert.assertNotSame(source.getModifiedAt(), new Event(source).getModifiedAt());
			}
		}
	}

	@Test
	public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
		for (Event source : samples) {
			Event copy = new Event(source);

			for (Field field : Event.class.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					Assert.assertEquals(field.get(source), field.get(copy));
				}
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCopyConstructorByNullValue() {
		new Event(null);
	}

	@Test
	public void testCopyConstructorByFlozenInstance() {
		Event source = new Event();
		Assert.assertFalse(source.isFrozen());

		source.freeze();
		Assert.assertTrue(source.isFrozen());

		Assert.assertFalse(new Event(source).isFrozen());
	}
}
