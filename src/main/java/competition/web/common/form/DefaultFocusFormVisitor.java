package competition.web.common.form;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import competition.web.common.behavior.DefaultFocusBehavior;

/**
 * @author Decebal Suiu
 */
public class DefaultFocusFormVisitor implements IVisitor<Component, Void>, Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<FormComponent<?>> visited = new HashSet<FormComponent<?>>();
	private boolean found = false;

	public void component(Component object, IVisit<Void> visit) {
		if (!visited.contains(object) && (object instanceof FormComponent) && !(object instanceof Button)) {
			final FormComponent<?> fc = (FormComponent<?>) object;
			visited.add(fc);
			if (!found && fc.isEnabled() && fc.isVisible()
					&& (fc instanceof DropDownChoice || fc instanceof AbstractTextComponent)) {
				found = true;
				fc.add(new DefaultFocusBehavior());
			}
		}
	}

}