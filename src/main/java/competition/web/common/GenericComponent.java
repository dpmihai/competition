package competition.web.common;

import org.apache.wicket.model.IModel;

/**
 * @author Decebal Suiu
 */
public interface GenericComponent<T> {

    public IModel<T> getModel();

    public void setModel(IModel<T> model);

    public T getModelObject();

    public void setModelObject(T object);

}
