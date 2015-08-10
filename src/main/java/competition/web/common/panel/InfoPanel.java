package competition.web.common.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class InfoPanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	public InfoPanel(String id, String message) {
		super(id);
		Label label = new Label("info", message);
		add(label);
	}

}
