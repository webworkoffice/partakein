package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.model.fixture.TestDataProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author skypencil (@eller86)
 */
public final class CommentTest extends AbstractPartakeModelTest<Comment> {
    private Comment[] samples;

    @Override
    protected Comment copy(Comment t) {
        return new Comment(t);
    }

    @Override
    protected TestDataProvider<Comment> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getCommentDataProvider();
    }

    @Before
    public void createSamples() {
        samples = new Comment[] {
            new Comment("id1", "eventId1", "userId1", "comment1", false, new DateTime(0)),
            new Comment("id2", "eventId2", "userId2", "comment2", true, new DateTime(1)),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (Comment source : samples) {
            // Comment class doesn't override #equals() method.
            // Assert.assertEquals(source, new Comment(source));

            Assert.assertEquals(source.getId(), new Comment(source).getId());
            Assert.assertEquals(source.getEventId(), new Comment(source).getEventId());
            Assert.assertEquals(source.getUserId(), new Comment(source).getUserId());
            Assert.assertEquals(source.getComment(), new Comment(source).getComment());
            Assert.assertEquals(source.getCreatedAt(), new Comment(source).getCreatedAt());

            if (source.getCreatedAt() != null) {
                Assert.assertNotSame(source.getCreatedAt(), new Comment(source).getCreatedAt());
            }
        }
    }

    @Test
    public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
        for (Comment source : samples) {
            Comment copy = new Comment(source);

            for (Field field : Comment.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Assert.assertEquals(field.get(source), field.get(copy));
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyConstructorByNullValue() {
        new Comment((Comment) null);
    }

    @Test
    public void testCopyConstructorByFlozenInstance() {
        Comment source = getTestDataProvider().create();
        Assert.assertFalse(source.isFrozen());

        source.freeze();
        Assert.assertTrue(source.isFrozen());

        Assert.assertFalse(new Comment(source).isFrozen());
    }
}
