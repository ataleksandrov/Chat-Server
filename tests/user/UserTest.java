package user;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import chatApp.server.Server;
import chatApp.user.User;

public class UserTest {
	
	private static Server s;
	
	@BeforeClass
	public static void bindServer() {
		s = new Server();
	}

	@Test
	public void test() {
		User user = new User();
		assertNotNull(user);
	}

}
