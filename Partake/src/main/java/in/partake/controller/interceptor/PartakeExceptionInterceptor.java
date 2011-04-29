package in.partake.controller.interceptor;

import in.partake.controller.PartakeInvalidResultException;
import in.partake.controller.PartakeResultException;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.I18n;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PartakeExceptionInterceptor extends AbstractInterceptor {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PartakeExceptionInterceptor.class);


    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            return invocation.invoke();
        } catch (RuntimeException e) {
            logger.error("Uncaught Runtime Exception", e);
            return "error";
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return "error";
        } catch (PartakeInvalidResultException e) {
            // invalid は redirect がはいるので、session に保持しておく
            final ActionContext context = invocation.getInvocationContext();
            context.getSession().put(Constants.ATTR_ERROR_DESCRIPTION, e.getDescription());
            
            return e.getResult();
        } catch (PartakeResultException e) {
            return e.getResult();
        }
    }
}
