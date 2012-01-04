package in.partake.controller;

import in.partake.model.dao.DAOException;
import junit.framework.Assert;

import org.junit.Test;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class MypageTest extends AbstractPartakeControllerTest {
	@Test
	public void testLoginIsRequired() throws DAOException {
		ActionProxy proxy = getActionProxy("/mypage");
		MypageController controller = (MypageController) proxy.getAction();
		Assert.assertEquals(Action.LOGIN, controller.show());
	}
}
