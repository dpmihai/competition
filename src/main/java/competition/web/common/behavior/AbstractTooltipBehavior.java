package competition.web.common.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

/**
 * @author Decebal Suiu
 */
public abstract class AbstractTooltipBehavior extends Behavior {

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);
		tag.getAttributes().put("alt", getTooltip());
		tag.getAttributes().put("title", getTooltip());		
	}

	public abstract String getTooltip();

}