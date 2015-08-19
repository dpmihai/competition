package competition.service;

import java.io.Serializable;
import java.util.List;

import com.googlecode.genericdao.search.ISearch;

public interface GeneralService {

    public <T> T find(Class<T> clazz, Serializable id);

    public List search(ISearch search);

    public int count(ISearch search);
    
    public Object searchUnique(ISearch search);

    public <T> List<T> getList(Class<T> clazz, T filterObj);

    public <T> T getFiltered(Class<T> clazz, T filterObj);

    public <T> T merge(T entity);
    
    public <T> List<T> merge(List<T> entities);
    
    public <T> void remove(T entity);
    
    public <T> void remove(List<T> entities);

}
