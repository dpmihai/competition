package competition.web.security.event;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import competition.web.common.event.AjaxUpdateEvent;

/**
 * @author Decebal Suiu
 */
public class LogoutEvent extends AjaxUpdateEvent {

	public LogoutEvent(Component source, AjaxRequestTarget target) {
		super(source, target);
	}

}
