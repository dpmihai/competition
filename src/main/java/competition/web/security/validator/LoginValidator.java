package competition.web.security.validator;

import java.util.Date;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.User;
import competition.service.CookieService;
import competition.service.GeneralService;
import competition.web.BaseAppSession;
import competition.web.BaseApplication;

/**
 * @author Decebal Suiu
 */
public class LoginValidator extends AbstractFormValidator {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(LoginValidator.class);

	private TextField<String> usernameTextField;
	private TextField<String> passwordTextField;
	private CheckBox remember;

	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private CookieService cookieService;

	public LoginValidator(TextField<String> usernameTextField, TextField<String> passwordTextField,  CheckBox remember) {
		super();

		this.usernameTextField = usernameTextField;
		this.passwordTextField = passwordTextField;
		this.remember = remember;

		Injector.get().inject(this);
	}

	public FormComponent<?>[] getDependentFormComponents() {
		return new FormComponent<?>[] { usernameTextField, passwordTextField, remember };
	}

	public void validate(Form<?> form) {			
		
		String username = usernameTextField.getInput();

		// check for activation
		User user = generalService.find(User.class, username);
		if (user == null) {
			form.error("Utilizator/Parola invalide.");
			return;
		}

		// login
		String password = passwordTextField.getInput();
		boolean logged = BaseAppSession.get().signIn(username, password);
		if (!logged) {
			// TODO
			// form.error(new ResourceModel("security.loginDenied"));
			form.error("Utilizator/Parola invalide.");
		} else {
			BaseApplication.get().addLoggedUser(username);
			Date date = new Date();
			LOG.info("User " + username + " logged at " + date);		
			// save cookies to be used for remember me functionality
			// see BaseApplication.newSession method were cookies are read			
			if (remember.getConvertedInput()) {		
				cookieService.addUserCookie(username);	
			}	
			cookieService.addRememberCookie(remember.getConvertedInput());								
		}
	}

}
