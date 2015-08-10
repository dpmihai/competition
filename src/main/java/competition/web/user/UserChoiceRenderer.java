package competition.web.user;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

import competition.domain.entity.User;

public class UserChoiceRenderer extends ChoiceRenderer<User> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(User object) {
		if (object == null) {
			return super.getDisplayValue(object);
		}
		
		return object.getUsername();
	}

}
