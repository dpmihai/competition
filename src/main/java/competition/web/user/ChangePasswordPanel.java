package competition.web.user;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import competition.web.common.form.FormContentPanel;
import competition.web.user.validator.ChangePasswordValidator;

public class ChangePasswordPanel extends FormContentPanel<Void> {

	protected String oldPassword;
	protected String newPassword;
	protected String retypedPassword;

	private PasswordTextField txtOldPassword;
	private PasswordTextField txtNewPassword;
	private PasswordTextField txtRetypedPassword;

	public ChangePasswordPanel() {
		txtOldPassword = new PasswordTextField("oldPassword", new PropertyModel<String>(this, "oldPassword"));
		txtOldPassword.setRequired(true);
		txtOldPassword.setLabel(Model.of("Parola veche"));
		addWithFeedback(txtOldPassword);

		txtNewPassword = new PasswordTextField("newPassword", new PropertyModel<String>(this, "newPassword"));
		txtNewPassword.setRequired(true);
		txtNewPassword.setLabel(Model.of("Parola noua"));
		txtNewPassword.add(StringValidator.minimumLength(6));
		addWithFeedback(txtNewPassword);

		txtRetypedPassword = new PasswordTextField("retypedPassword", new PropertyModel<String>(this, "retypedPassword"));
		txtRetypedPassword.setRequired(true);
		txtRetypedPassword.setLabel(Model.of("Parola reintrodusa"));
		addWithFeedback(txtRetypedPassword);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		getForm().add(new ChangePasswordValidator(txtOldPassword, txtNewPassword, txtRetypedPassword));
	}

}
