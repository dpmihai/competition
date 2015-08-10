package competition.web.common.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import competition.web.common.GenericComponent;

/**
 * @author Decebal Suiu
 */
public class GenericPanel<T> extends Panel implements GenericComponent<T> {

	private static final long serialVersionUID = 1L;

	public GenericPanel(String id) {
		super(id);
	}

	public GenericPanel(String id, IModel<T> model) {
		super(id, model);
	}

	@SuppressWarnings("unchecked")
	public final IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	@SuppressWarnings("unchecked")
	public final T getModelObject() {
		return (T) getDefaultModelObject();
	}

	public final void setModel(IModel<T> model) {
		setDefaultModel(model);
	}

	public final void setModelObject(T object) {
		// setDefaultModelObject(object); // this method compare the old object
		// (deleted in some case) with the new object
		getModel().setObject(object);
	}

}
