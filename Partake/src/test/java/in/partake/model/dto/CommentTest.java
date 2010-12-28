package in.partake.model.dto;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author skypencil (@eller86)
 */
public final class CommentTest {
	@Test
	public void testCopyConstructor() {
		Comment[] samples = new Comment[] {
				new Comment(),
				new Comment("id1", "eventId1", "userId1", "comment1", new Date()),
				new Comment("id2", "eventId2", "userId2", "comment2", new Date(1)),
		};

		for (Comment source : samples) {
			// Comment class doesn't override #equals() method.
			// Assert.assertEquals(source, new Comment(source));

			Assert.assertEquals(source.getId(), new Comment(source).getId());
			Assert.assertEquals(source.getEventId(), new Comment(source).getEventId());
			Assert.assertEquals(source.getUserId(), new Comment(source).getUserId());
			Assert.assertEquals(source.getComment(), new Comment(source).getComment());
			Assert.assertEquals(source.getCreatedAt(), new Comment(source).getCreatedAt());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCopyConstructorByNullValue() {
		new Event(null);
	}

	@Test
	public void testCopyConstructorByFlozenInstance() {
		Comment source = new Comment();
		Assert.assertFalse(source.isFrozen());

		source.freeze();
		Assert.assertTrue(source.isFrozen());

		Assert.assertFalse(new Comment(source).isFrozen());
	}
}
