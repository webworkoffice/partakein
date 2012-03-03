package in.partake.controller.interceptor;

import in.partake.base.PartakeRuntimeException;
import in.partake.controller.base.PartakeInvalidResultException;
import in.partake.controller.base.PartakeResultException;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.session.PartakeSession;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.rosaloves.bitlyj.BitlyException;

/**
 * Exception を catch し、適切な処理を行う。
 * @author shinyak
 *
 */
public class PartakeExceptionInterceptor extends AbstractInterceptor {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PartakeExceptionInterceptor.class);


    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            return invocation.invoke();
        } catch (PartakeRuntimeException e) {
            setServerErrorCode(invocation, e.getServerErrorCode());            
            return "error";
        } catch (RuntimeException e) {
            ServerErrorCode ec = ServerErrorCode.UNKNOWN_ERROR;
            if (e.getCause() instanceof BitlyException) {
                // TODO: なんで bitly は RuntimeException にくるまれてやってくるのか？ 
                setServerErrorCode(invocation, ServerErrorCode.BITLY_ERROR);
                ec = ServerErrorCode.BITLY_ERROR;
            }

            logger.error(ec.getReasonString(), e);
            return "error";
        } catch (DAOException e) {
            setServerErrorCode(invocation, ServerErrorCode.DB_ERROR);
            logger.error(ServerErrorCode.DB_ERROR.getReasonString(), e);
            return "error";
        } catch (PartakeInvalidResultException e) {
            // invalid は redirect がはいるので、session に保持しておく
            // TODO: PartakeSession を使え
            final ActionContext context = invocation.getInvocationContext();
            context.getSession().put(Constants.ATTR_ERROR_DESCRIPTION, e.getDescription());
            
            return e.getResult();
        } catch (PartakeResultException e) {
            return e.getResult();
        } catch (Exception e) {
            logger.error("Error happened.", e);
            throw e;
        }
    }


    private void setServerErrorCode(ActionInvocation invocation, ServerErrorCode ec) {
        final ActionContext context = invocation.getInvocationContext();
        if (context.getSession().containsKey(Constants.ATTR_PARTAKE_SESSION)) {
            PartakeSession session = (PartakeSession) context.getSession().get(Constants.ATTR_PARTAKE_SESSION);
            if (ec != null)
                session.setLastServerError(ec);
        }
    }
    
}
