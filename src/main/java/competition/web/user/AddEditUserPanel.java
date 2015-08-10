package competition.web.user;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.User;
import competition.web.user.validator.CaptchaValidator;
import competition.web.user.validator.DuplicatePropertyValidator;

public class AddEditUserPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditUserPanel.class);
	private FeedbackPanel feedbackPanel;	
	private User user = null;
	private boolean add = false;
	
	public AddEditUserPanel(String id) {
		this(id, null);
	}
	
	public AddEditUserPanel(String id, User user) {
        super(id);
        this.user = user;
        if (user == null) {
        	add = true;
        	user = new User();
        }                        
        UserForm form = new UserForm("form", user);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddUser(AjaxRequestTarget target, Form form, User user) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class UserForm extends Form<User> {
        
		private static final long serialVersionUID = 1L;
		
		private PasswordTextField password;
		private PasswordTextField confirmPassword;
		private CaptchaValidator captchaValidator;
		private String captchaText = RandomStringUtils.randomAlphabetic(5).toUpperCase();

		public UserForm(String id, final User user) {

            super(id, new CompoundPropertyModel<User>(user));                                 
                        
            TextField<String> username = new TextField<String>("username");
    		username.setRequired(true);
    		//username.add(new DuplicatePropertyValidator(User.class, "username", "user.errorUsernameExists"));
    		username.setLabel(Model.of("Utilizator"));
    		username.add(StringValidator.minimumLength(3));
    		username.add(StringValidator.maximumLength(15));
    		add(username);

    		System.out.println("****** add="+add);
    		password = new PasswordTextField("password");
    		if (add) {
    			password.setRequired(true);
    			password.add(StringValidator.minimumLength(6));
    		} else {
    			password.setRequired(false);
    		}
    		password.setLabel(Model.of("Parola"));    		
    		add(password);

    		confirmPassword = new PasswordTextField("confirmPassword", new Model<String>());
    		if (add) {
    			confirmPassword.setRequired(true);
    		} else {
    			confirmPassword.setRequired(false);
    		}
    		confirmPassword.setLabel(Model.of("Confirmare Parola"));
    		add(confirmPassword);

    		TextField<String> email = new TextField<String>("email");
    		email.setRequired(true);
    		email.add(EmailAddressValidator.getInstance());
    		email.setLabel(Model.of("Email"));
    		add(email);
    		
    		TextField<String> phone = new TextField<String>("phone");
    		phone.setLabel(Model.of("Telefon"));
    		add(phone);
    		
    		TextField<String> team = new TextField<String>("team");
    		team.setRequired(true);
    		//team.add(new DuplicatePropertyValidator(User.class, "team", "user.errorUserTeamExists"));
    		team.setLabel(Model.of("Team"));
    		add(team);
    		
    		TextField<String> avatarFile = new TextField<String>("avatarFile");
    		avatarFile.setRequired(true);
    		avatarFile.setLabel(Model.of("Avatar"));
    		add(avatarFile);

    		CaptchaImageResource captchaImageResource = new CaptchaImageResource(captchaText);
    		final Image captchaImage = new NonCachingImage("captchaImage", captchaImageResource);
    		captchaImage.setOutputMarkupId(true);
    		add(captchaImage);

    		add(new AjaxLink<Void>("captchaReload") {

    			@Override
    			public void onClick(AjaxRequestTarget target) {
    				Image newCaptchaImage = createCaptchaImage();
    				UserForm.this.replace(newCaptchaImage);
    				captchaValidator.setExpectedText(captchaText);

    				target.add(newCaptchaImage, captchaImage.getMarkupId());
    			}

    		});

    		TextField<String> captcha = new TextField<String>("captcha", new Model<String>()) {

    			@Override
    			protected final void onComponentTag(ComponentTag tag) {
    				super.onComponentTag(tag);

    				// clear the field after each render
    				tag.put("value", "");
    			}

    		};
    		captcha.setRequired(true);
    		captchaValidator = new CaptchaValidator(captchaText);
    		captcha.add(captchaValidator);
    		captcha.setLabel(Model.of("Verificare cod"));
    		add(captcha);          	    		

            AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                	User user = UserForm.this.getModelObject();                    
                    onAddUser(target, form, user);
                }

                 protected void onError(AjaxRequestTarget target, Form<?> form) {                    
                    target.add(feedbackPanel);
                }

            };
            add(addLink);


            add(new AjaxLink("cancel") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    onCancel(target);
                }

            });
        }    
		
		private Image createCaptchaImage() {
			captchaText = RandomStringUtils.randomAlphabetic(5).toUpperCase();
			CaptchaImageResource captchaImageResource = new CaptchaImageResource(captchaText);
			Image captchaImage = new NonCachingImage("captchaImage", captchaImageResource);
			captchaImage.setOutputMarkupId(true);

			return captchaImage;
		}	
		
		@Override
		protected void onInitialize() {
			super.onInitialize();
			if (add || (user.getPassword() != null)) {
				add(new EqualPasswordInputValidator(password, confirmPassword));
			}
		}


    }


}
