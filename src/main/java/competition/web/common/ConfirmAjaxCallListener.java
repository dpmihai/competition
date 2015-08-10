package competition.web.common;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;

public class ConfirmAjaxCallListener extends AjaxCallListener {

	private static final long serialVersionUID = 2155228645806565335L;
	private String message;

	public ConfirmAjaxCallListener(String message) {
		this.message = message;
	}

	@Override
	public CharSequence getPrecondition(Component component) {
		String text = message.replaceAll("'", "\"");
		String extraJs = "if (!confirm('" + text + "')) return false; ";
		return extraJs;
	}	

}
