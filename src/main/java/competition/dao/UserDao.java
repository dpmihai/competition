package competition.dao;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import competition.domain.entity.User;

import com.trg.dao.jpa.GenericDAO;

/**
 * @author Decebal Suiu
 */
public interface UserDao extends GenericDAO<User, String>, UserDetailsService {
    

}
