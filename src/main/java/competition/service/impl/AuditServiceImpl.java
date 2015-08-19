package competition.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GeneralDAO;

import competition.domain.entity.Login;
import competition.service.AuditService;;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {
	
	@Autowired
	private GeneralDAO generalDao;

	@Override
	public void createLogin(Login login) {
		generalDao.merge(login);				
	}

}
