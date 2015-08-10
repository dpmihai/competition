package competition.web.user;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import competition.domain.entity.User;
import competition.web.common.form.FormContentPanel;
import competition.web.user.validator.CaptchaValidator;
import competition.web.user.validator.DuplicatePropertyValidator;

/**
 * @author Decebal Suiu
 */
public class UserPanel extends FormContentPanel<User> {

	private PasswordTextField password;
	private PasswordTextField confirmPassword;

	private CaptchaValidator captchaValidator;
	private String captchaText = RandomStringUtils.randomAlphabetic(5).toUpperCase();

	public UserPanel(String id, IModel<User> model) {
		super(id, model);

		TextField<String> username = new TextField<String>("username");
		username.setRequired(true);
		username.add(new DuplicatePropertyValidator(User.class, "username", "user.errorUsernameExists"));
		username.setLabel(Model.of("Utilizator"));
		username.add(StringValidator.minimumLength(3));
		username.add(StringValidator.maximumLength(15));
		addWithFeedback(username);

		password = new PasswordTextField("password");
		password.setRequired(true);
		password.setLabel(Model.of("Parola"));
		password.add(StringValidator.minimumLength(6));
		addWithFeedback(password);

		confirmPassword = new PasswordTextField("confirmPassword", new Model<String>());
		confirmPassword.setRequired(true);
		confirmPassword.setLabel(Model.of("Confirmare Parola"));
		addWithFeedback(confirmPassword);

		TextField<String> email = new TextField<String>("email");
		email.setRequired(true);
		email.add(EmailAddressValidator.getInstance());
		email.setLabel(Model.of("Email"));
		addWithFeedback(email);
		
		TextField<String> team = new TextField<String>("team");
		team.setRequired(true);
		team.add(new DuplicatePropertyValidator(User.class, "team", "user.errorUserTeamExists"));
		team.setLabel(Model.of("Team"));
		addWithFeedback(team);

		CaptchaImageResource captchaImageResource = new CaptchaImageResource(captchaText);
		final Image captchaImage = new NonCachingImage("captchaImage", captchaImageResource);
		captchaImage.setOutputMarkupId(true);
		add(captchaImage);

		add(new AjaxLink<Void>("captchaReload") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				Image newCaptchaImage = createCaptchaImage();
				UserPanel.this.replace(newCaptchaImage);
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
		addWithFeedback(captcha);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		getForm().add(new EqualPasswordInputValidator(password, confirmPassword));
	}

	private Image createCaptchaImage() {
		captchaText = RandomStringUtils.randomAlphabetic(5).toUpperCase();
		CaptchaImageResource captchaImageResource = new CaptchaImageResource(captchaText);
		Image captchaImage = new NonCachingImage("captchaImage", captchaImageResource);
		captchaImage.setOutputMarkupId(true);

		return captchaImage;
	}	

}
