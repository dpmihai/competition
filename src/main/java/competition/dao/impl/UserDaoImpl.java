package competition.dao.impl;

import javax.persistence.Query;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import competition.dao.UserDao;
import competition.domain.entity.User;

/**
 * @author Decebal Suiu
 */
public class UserDaoImpl extends AbstractBaseDaoImpl<User, String> implements UserDao {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		User user = find(username);
		if (user == null) {
			throw new UsernameNotFoundException("User '" + username + "' not found.");
		}

		return user;
	}

}
