package competition.web.security;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import competition.web.common.form.FormContentPanel;
import competition.web.security.validator.LoginValidator;

/**
 * @author Decebal Suiu
 */
public class LoginPanel extends FormContentPanel<Void> {

    private static final long serialVersionUID = 1L;

    private TextField<String> username;
    private PasswordTextField password;     
    private CheckBox remember;
	private boolean rememberMe = false;
    
	public LoginPanel(String id) {
		super(id);

		username = new TextField<String>("username", new Model<String>());
		username.setRequired(true);
		username.setLabel(Model.of("Utilizator"));
		addWithFeedback(username);

		password = new PasswordTextField("password", new Model<String>());
		password.setRequired(true);
		password.setLabel(Model.of("Parola"));
		addWithFeedback(password);				
		
		remember = new CheckBox("rememberMe", new PropertyModel<Boolean>(this,"rememberMe"));
		add(remember);						
	}
        
    @Override
    protected void onInitialize() {
    	super.onInitialize();
        getForm().add(new LoginValidator(username, password, remember));
    }
 
}

