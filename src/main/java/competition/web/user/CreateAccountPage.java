package competition.web.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.User;
import competition.service.UserService;
import competition.web.BaseTemplatePage;
import competition.web.common.form.FormPanel;

/**
 * @author Decebal Suiu
 */
public class CreateAccountPage extends BaseTemplatePage {

	private User user = new User();

	private WebMarkupContainer container;

	@SpringBean
	private UserService userService;

	public CreateAccountPage() {
		super();

		IModel<User> model = new CompoundPropertyModel<User>(user);
		UserPanel userPanel = new UserPanel(FormPanel.CONTENT_ID, model) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onOk(AjaxRequestTarget target) {
				super.onOk(target);
				createUser(target);
			}

		};

		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);

		FormPanel<User> formPanel = new FormPanel<User>("formPanel", userPanel);
		formPanel.getCancelButton().setVisible(false);
		container.add(formPanel);
	}

	private void createUser(AjaxRequestTarget target) {
		userService.createUser(user);

		Label messageLabel = new Label(
				"formPanel",
				"<div class=\"success\">Contul a fost creat cu succes.</div>");
		messageLabel.setEscapeModelStrings(false);
		container.replace(messageLabel);
		target.add(container);
	}

}
