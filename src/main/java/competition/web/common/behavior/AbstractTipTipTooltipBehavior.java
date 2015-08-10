package competition.web.common.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.Model;

/**
 * @author Decebal Suiu
 */
public abstract class AbstractTipTipTooltipBehavior extends /*AbstractBehavior*/ AbstractTooltipBehavior {

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
	super.onComponentTag(component, tag);
//	tag.addBehavior(new SimpleAttributeModifier("title", getTooltip()));	
	tag.addBehavior(new AttributeAppender("class", Model.of("tooltip"), " "));
    }

//    public abstract String getTooltip();
    
}