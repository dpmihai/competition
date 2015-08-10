package competition.web.stage;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

import competition.domain.entity.Stage;

public class StageChoiceRenderer extends ChoiceRenderer<Stage> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(Stage object) {
		if (object == null) {
			return super.getDisplayValue(object);
		}
		
		return object.getName();
	}

}
