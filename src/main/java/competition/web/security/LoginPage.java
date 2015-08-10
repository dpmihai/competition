package competition.web.security;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;

import competition.web.common.form.FormPanel;

/**
 * @author decebal
 */
public class LoginPage extends WebPage {

	public LoginPage() {
		super();

		LoginPanel loginPanel = new LoginPanel(FormPanel.CONTENT_ID) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onOk(AjaxRequestTarget target) {
				super.onOk(target);
				continueToOriginalDestination();
			}

		};
		FormPanel<Void> formPanel = new FormPanel<Void>("panel", loginPanel);
		formPanel.getCancelButton().setVisible(false);
		add(formPanel);
	}

}
