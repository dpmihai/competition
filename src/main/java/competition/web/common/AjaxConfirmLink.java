package competition.web.common;

import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

public abstract class AjaxConfirmLink<T> extends AjaxLink<T> {

	private static final long serialVersionUID = 3034440259588339615L;

	protected String message;
	
	public AjaxConfirmLink(String id) {
		super(id);
	}

	public AjaxConfirmLink(String id, String message) {
		super(id);
		this.message = message;
	}

	public AjaxConfirmLink(String id, IModel<T> object) {
		super(id, object);
	}

	public AjaxConfirmLink(String id, IModel<T> object, String message) {
		super(id, object);
		this.message = message;
	}

    public boolean showDialog() {
        return true;
    }	

	public String getMessage() {
		return message;
	}
	
	@Override
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);
		attributes.getAjaxCallListeners().add(new ConfirmAjaxCallListener(message));
	}	
	
}
