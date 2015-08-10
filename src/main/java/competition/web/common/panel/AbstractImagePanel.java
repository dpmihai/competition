package competition.web.common.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractImagePanel extends Panel {

	private static final long serialVersionUID = -1625354965548690034L;

	public AbstractImagePanel(String id) {
		this(id, null);
	}
	
	public AbstractImagePanel(String id, String imageClass) {
		super(id);
		add(getImage(imageClass));
		setRenderBodyOnly(true);
	}


	public abstract String getImageName();
	
	protected Component getImage(String imageClass) {
		ContextImage image = new ContextImage("image", getImageName());
		if (imageClass != null) {
		    image.add(AttributeModifier.replace("class", imageClass));
		}
		return image;
	}
	
}

