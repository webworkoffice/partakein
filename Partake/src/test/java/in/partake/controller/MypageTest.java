package in.partake.controller;

import in.partake.model.dao.DAOException;
import junit.framework.Assert;

import org.apache.struts2.StrutsTestCase;
import org.junit.Test;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

//TODO: should extends PartakeControllerTestCase
public class MypageTest extends StrutsTestCase {
	@Test
	public void testLoginIsRequired() throws DAOException {
		ActionProxy proxy = getActionProxy("/mypage");
		MypageController controller = (MypageController) proxy.getAction();
		Assert.assertEquals(Action.LOGIN, controller.show());
	}
}
