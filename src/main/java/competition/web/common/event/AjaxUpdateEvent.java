package competition.web.common.event;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * Very simple event mechanism for loose coupling of wicket components for ajax
 * updates.
 * 
 * @author Decebal Suiu
 */
public class AjaxUpdateEvent {

	private final Component source;
	private final AjaxRequestTarget target;

	public AjaxUpdateEvent(final Component source, final AjaxRequestTarget target) {
		this.source = source;
		this.target = target;
	}

	public Component getSource() {
		return source;
	}

	public AjaxRequestTarget getTarget() {
		return target;
	}

	public void addToTarget(Component component) {
		target.add(component);
	}

	/**
	 * Fire this event, i.e. notify listeners. This is done by visiting all
	 * children of the source component's page.
	 */
	public final void fire() {
		Page page = getSource().getPage();
		if (page instanceof AjaxUpdateListener) {
			((AjaxUpdateListener) page).onAjaxUpdate(this);
		}
		page.visitChildren(new NotifyVisitor(this));
	}

	/**
	 * Visitor to notify all children of a page.
	 */
	/**
	 * Visitor to notify all children of a page.
	 */
	private static final class NotifyVisitor implements IVisitor<Component, Void> {
		
		private final AjaxUpdateEvent event;

		public NotifyVisitor(final AjaxUpdateEvent event) {
			this.event = event;
		}

		public void component(Component object, IVisit<Void> visit) {
			if (object instanceof AjaxUpdateListener) {
				((AjaxUpdateListener) object).onAjaxUpdate(event);
			}
		}
		
	}

}
