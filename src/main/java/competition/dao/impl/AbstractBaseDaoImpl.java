package competition.dao.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

@Repository
public class AbstractBaseDaoImpl<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	@Override
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

	@Override
	@Autowired
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}

	/*
	 * @Override public T save(T entity) { if
	 * (Auditable.class.isAssignableFrom(entity.getClass())) { ((Auditable)
	 * entity).setCreationDate(Calendar.getInstance() .getTime()); }
	 * 
	 * return super.save(entity); }
	 */

	public int count() {
		return super.count(null);
	}

}
