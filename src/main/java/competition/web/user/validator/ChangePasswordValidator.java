package competition.web.user.validator;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

import competition.domain.entity.User;
import competition.web.security.SecurityUtil;

public class ChangePasswordValidator extends AbstractFormValidator {

	private PasswordTextField oldPasswordTextField;
	private PasswordTextField newPasswordTextField;
	private PasswordTextField retypedPasswordTextField;

	public ChangePasswordValidator(PasswordTextField oldPasswordTextField, PasswordTextField newPasswordTextField,
			PasswordTextField retypedPasswordTextField) {
		this.oldPasswordTextField = oldPasswordTextField;
		this.newPasswordTextField = newPasswordTextField;
		this.retypedPasswordTextField = retypedPasswordTextField;
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return new FormComponent<?>[] { oldPasswordTextField, newPasswordTextField, retypedPasswordTextField };
	}

	@Override
	public void validate(Form<?> form) {
		String oldPassword = oldPasswordTextField.getInput();
		String newPassword = newPasswordTextField.getInput();
		String retypedPassword = retypedPasswordTextField.getInput();

		User user = SecurityUtil.getLoggedUser();
		if (!user.getPassword().equals(oldPassword)) {
			form.error("Parola veche nu se potriveste");
		}
		if (!newPassword.equals(retypedPassword)) {
			form.error("Parola Reintrodusa nu se potriveste cu Parola Noua");
		}
	}

}
