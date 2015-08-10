package competition.web.common.palette;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * @author Decebal Suiu
 */
public class StringChoiceRenderer implements IChoiceRenderer<String> {

	public Object getDisplayValue(String object) {
		return object;
	}

	public String getIdValue(String object, int index) {
		return object;
	}

}
