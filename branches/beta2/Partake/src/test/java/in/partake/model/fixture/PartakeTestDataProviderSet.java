package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.fixture.impl.BinaryTestDataProvider;
import in.partake.model.fixture.impl.CommentTestDataProvider;
import in.partake.model.fixture.impl.EnrollmentTestDataProvider;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.ImageTestDataProvider;
import in.partake.model.fixture.impl.OpenIDLinkageTestDataProvider;
import in.partake.model.fixture.impl.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.impl.UserPreferenceTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import java.util.ArrayList;

/**
 * A set of test data providers. 
 * @author shinyak
 *
 */
public class PartakeTestDataProviderSet {
    private ArrayList<TestDataProvider<?>> providers;
    
    private BinaryTestDataProvider binaryDataProvider;
    private CommentTestDataProvider commentDataprovider;
    private EnrollmentTestDataProvider enrollmentProvider;
    private EventTestDataProvider eventProvider;
    private ImageTestDataProvider imageProvider;
    private OpenIDLinkageTestDataProvider openIDLinkageProvider;
    private TwitterLinkageTestDataProvider twitterLinkageProvider;
    private UserTestDataProvider userProvider;
    private UserPreferenceTestDataProvider userPreferenceProvider;
    
    public PartakeTestDataProviderSet() {
        this.providers = new ArrayList<TestDataProvider<?>>();
        
        providers.add(binaryDataProvider = createBinaryTestDataProvider());
        providers.add(commentDataprovider = createCommentTestDataProvider());
        providers.add(enrollmentProvider = createEnrollmentTestDataProvider());
        providers.add(eventProvider = createEventTestDataProvider());
        providers.add(imageProvider = createImageTestDataProvider());
        providers.add(openIDLinkageProvider = createOpenIDLinkageTestDataProvider());
        providers.add(twitterLinkageProvider = createTwitterLinkageTestDataProvider());
        providers.add(userProvider = createUserTestDataProvider());
        providers.add(userPreferenceProvider = createUserPreferenceTestDataProvider());
    }
    
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        for (TestDataProvider<?> provider : providers) {
            provider.createFixtures(con, factory);
        }
    }

    public BinaryTestDataProvider getBinaryTestDataProvider() {
        return binaryDataProvider;
    }
    
    public CommentTestDataProvider getCommentDataProvider() {
        return commentDataprovider;
    }

    public EnrollmentTestDataProvider getEnrollmentProvider() {
        return enrollmentProvider;
    }

    public EventTestDataProvider getEventProvider() {
        return eventProvider;
    }

    public ImageTestDataProvider getImageProvider() {
        return imageProvider;
    }

    public OpenIDLinkageTestDataProvider getOpenIDLinkageProvider() {
        return openIDLinkageProvider;
    }

    public TwitterLinkageTestDataProvider getTwitterLinkageProvider() {
        return twitterLinkageProvider;
    }

    public UserTestDataProvider getUserProvider() {
        return userProvider;
    }

    public UserPreferenceTestDataProvider getUserPreferenceProvider() {
        return userPreferenceProvider;
    }
    
    private BinaryTestDataProvider createBinaryTestDataProvider() {
        return new BinaryTestDataProvider();
    }
    
    private CommentTestDataProvider createCommentTestDataProvider() {
        return new CommentTestDataProvider();
    }
    
    private EnrollmentTestDataProvider createEnrollmentTestDataProvider() {
        return new EnrollmentTestDataProvider();
    }

    private EventTestDataProvider createEventTestDataProvider() {
        return new EventTestDataProvider();
    }

    private ImageTestDataProvider createImageTestDataProvider() {
        return new ImageTestDataProvider();
    }

    private OpenIDLinkageTestDataProvider createOpenIDLinkageTestDataProvider() {
        return new OpenIDLinkageTestDataProvider();
    }
    
    private TwitterLinkageTestDataProvider createTwitterLinkageTestDataProvider() {
        return new TwitterLinkageTestDataProvider();
    }
    
    private UserTestDataProvider createUserTestDataProvider() {
        return new UserTestDataProvider();
    }
    
    private UserPreferenceTestDataProvider createUserPreferenceTestDataProvider() {
        return new UserPreferenceTestDataProvider();
    }
}
