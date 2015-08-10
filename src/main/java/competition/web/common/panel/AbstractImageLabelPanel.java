package competition.web.common.panel;

import org.apache.wicket.markup.html.basic.Label;

public abstract class AbstractImageLabelPanel extends AbstractImagePanel {

	private static final long serialVersionUID = -235720652876281757L;

	public AbstractImageLabelPanel(String id) {
		super(id);
		add(getLabel());
	}

	public AbstractImageLabelPanel(String id, String imageClass) {
		super(id, imageClass);
		add(getLabel());
	}

	public abstract String getDisplayString();

	protected Label getLabel() {
		return new Label("label", getDisplayString());
	}

}