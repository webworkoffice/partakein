package in.partake.session;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

public class PartakeSessionTest {

    @Test
    public void testCreate() {
        PartakeSession session = PartakeSession.createInitialPartakeSession();
        
        assertThat(session.getCSRFPrevention(), is(notNullValue()));
        assertThat(session.getLastServerError(), is(nullValue()));
        assertThat(session.getLastUserErrorCode(), is(nullValue()));
        
        session.setLastServerError(ServerErrorCode.INTENTIONAL_ERROR);
        assertThat(session.getLastServerError(), is(ServerErrorCode.INTENTIONAL_ERROR));
        assertThat(session.getLastUserErrorCode(), is(nullValue()));
        assertThat(session.hasServerErrorCode(), is(true));
        assertThat(session.hasUserErrorCode(), is(false));

        session.setLastUserError(UserErrorCode.INTENTIONAL_USER_ERROR);
        assertThat(session.getLastServerError(), is(ServerErrorCode.INTENTIONAL_ERROR));
        assertThat(session.getLastUserErrorCode(), is(UserErrorCode.INTENTIONAL_USER_ERROR));
        assertThat(session.hasServerErrorCode(), is(true));
        assertThat(session.hasUserErrorCode(), is(true));
    }
}
