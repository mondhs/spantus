package org.spantus.dao.test;

import java.util.List;

import org.spantus.dao.UserDao;
import org.spantus.domain.User;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class UserDaoTest extends AbstractDependencyInjectionSpringContextTests{
	
	UserDao userDao;
	
	
	protected String[] getConfigLocations() {
		return new String[] {"classpath:/META-INF/spring/*-beans.xml"};
	}
	
	public void testUserDao(){
		User user = new User();
		user.setLogin("Test1");
		user = userDao.store(user);
		assertNotNull("After save id should be set", user.getId());
		User user2 = userDao.findById(user.getId());
		assertNotNull(user2);
		List<User> users = userDao.findAll();
		assertEquals(1, users.size());
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
