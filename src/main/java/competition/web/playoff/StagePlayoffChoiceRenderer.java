package competition.web.playoff;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import competition.domain.entity.StagePlayoff;

public class StagePlayoffChoiceRenderer extends ChoiceRenderer<StagePlayoff> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(StagePlayoff object) {
		if (object == null) {
			return super.getDisplayValue(object);
		}
		
		return object.getName();
	}

}

