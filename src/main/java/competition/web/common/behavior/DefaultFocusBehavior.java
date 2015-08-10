package competition.web.common.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * @author Decebal Suiu
 * @see http
 *      ://apache-wicket.1842946.n4.nabble.com/Default-Focus-Behavior-td1868610
 *      .html
 */
public class DefaultFocusBehavior extends Behavior {

	private Component component;

	@Override
	public void bind(Component component) {
		if (!(component instanceof FormComponent)) {
			throw new IllegalArgumentException("DefaultFocusBehavior: component must be instanceof FormComponent");
		}
		this.component = component;
		component.setOutputMarkupId(true);
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(OnLoadHeaderItem.forScript("document.getElementById('" + component.getMarkupId() + "').focus();"));
	}

}
