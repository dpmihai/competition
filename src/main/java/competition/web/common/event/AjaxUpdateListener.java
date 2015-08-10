package competition.web.common.event;

/**
 * Components listening for events have to implement this interface.
 * 
 * @author Decebal Suiu
 */
public interface AjaxUpdateListener {

	/**
	 * You will typically first do a <code>(event instanceof MyEvent)</code> and
	 * finally <code>addToTarget(myComponent)</code>.
	 * 
	 * @param event event fired by event.getSource()
	 */
	public void onAjaxUpdate(AjaxUpdateEvent event);

}
