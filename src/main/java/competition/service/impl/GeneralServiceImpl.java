package competition.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import competition.service.GeneralService;

import com.trg.dao.jpa.GeneralDAO;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.Search;

@Service
@Transactional
public class GeneralServiceImpl implements GeneralService {

	@Autowired
	private GeneralDAO generalDao;

	@Transactional(readOnly = true)
	public <T> T find(Class<T> clazz, Serializable id) {
		return generalDao.find(clazz, id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List search(ISearch search) {
		return generalDao.search(search);
	}

	@Transactional(readOnly = true)
	public int count(ISearch search) {
		return generalDao.count(search);
	}

	@Transactional(readOnly = true)
	public Object searchUnique(ISearch search) {
		return generalDao.searchUnique(search);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public <T> List<T> getList(Class<T> clazz, T filterObj) {
		Filter filter = generalDao.getFilterFromExample(filterObj);

		Search search = new Search(clazz);
		return generalDao.search(search.addFilter(filter));
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public <T> T getFiltered(Class<T> clazz, T filterObj) {
		Filter filter = generalDao.getFilterFromExample(filterObj);
		Search search = new Search(clazz);
		search.setMaxResults(1);

		return (T) generalDao.searchUnique(search.addFilter(filter));
	}

	@Transactional
	public <T> T merge(T entity) {
		return generalDao.merge(entity);
	}
	
	@Transactional
	public <T> List<T> merge(List<T> entities) {
		List<T> result = new ArrayList<T>();
		for (T obj : entities) {
			result.add(generalDao.merge(obj));
		}
		return result;
	}

	@Transactional
	public <T> void remove(T entity) {
		generalDao.remove(entity);
	}
	
	@Transactional
	public <T> void remove(List<T> entities) {
		for (T obj : entities) {
			generalDao.remove(obj);
		}	
	}

}
