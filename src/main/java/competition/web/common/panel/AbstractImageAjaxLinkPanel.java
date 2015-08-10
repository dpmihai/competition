package competition.web.common.panel;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.Model;

/**
 * @author Decebal Suiu
 */
public abstract class AbstractImageAjaxLinkPanel<T extends Serializable> extends AbstractAjaxLinkPanel<T> {

	private static final long serialVersionUID = -6588160340667114918L;

	public AbstractImageAjaxLinkPanel(String id, Model<T> model) {
		super(id, model);
		link.add(getImage());
	}

	public abstract String getImageName();

	protected Component getImage() {
		return new ContextImage("image", getImageName());
	}

}
