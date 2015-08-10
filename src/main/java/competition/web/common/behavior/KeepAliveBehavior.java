package competition.web.common.behavior;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.time.Duration;

/**
 * Prevent session expiration.
 * 
 * @author Decebal Suiu
 */
public class KeepAliveBehavior extends AbstractAjaxTimerBehavior {

    public KeepAliveBehavior() {
	this(20);
    }
    
    public KeepAliveBehavior(int minutes) {
	super(Duration.minutes(minutes));
    }

    @Override
    protected void onTimer(AjaxRequestTarget target) {
	// prevent wicket changing focus
	target.focusComponent(null);
    }

}
