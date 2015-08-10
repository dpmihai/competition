package competition.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.trg.dao.jpa.GeneralDAO;
import com.trg.dao.jpa.GeneralDAOImpl;
import com.trg.search.jpa.JPASearchProcessor;

/**
 * @author Decebal Suiu
 */
@Repository
public class DecoratedGeneralDaoImpl extends GeneralDAOImpl implements GeneralDAO {

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

	@Autowired
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}

}
