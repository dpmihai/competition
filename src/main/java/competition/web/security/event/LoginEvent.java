package competition.web.security.event;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import competition.web.common.event.AjaxUpdateEvent;

/**
 * @author Decebal Suiu
 */
public class LoginEvent extends AjaxUpdateEvent {

	public LoginEvent(Component source, AjaxRequestTarget target) {
		super(source, target);
	}

}
