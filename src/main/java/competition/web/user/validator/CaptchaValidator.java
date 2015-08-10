package competition.web.user.validator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * @author Decebal Suiu
 */
public class CaptchaValidator extends AbstractValidator<String> {

	private String expectedText;

	public CaptchaValidator(String expectedText) {
		this.expectedText = expectedText;
	}

	public void setExpectedText(String expectedText) {
		this.expectedText = expectedText;
	}

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		String captchaText = validatable.getValue();
		if (!expectedText.equals(captchaText)) {
			error(validatable, "user.errorCodeValidation");
		}
	}

}
