package competition.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import competition.dao.UserDao;
import competition.domain.entity.User;
import competition.service.UserService;

import com.googlecode.genericdao.dao.jpa.GeneralDAO;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private GeneralDAO generalDao;

	@Autowired
	private UserDao userDao;

	public User createUser(User user) {
		User savedUser = userDao.merge(user);
		return savedUser;
	}

}
