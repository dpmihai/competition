package competition.web.user.validator;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.trg.search.Search;

import competition.service.GeneralService;

public class DuplicatePropertyValidator implements IValidator<String> {
	
	private static final long serialVersionUID = 1L;
	
	private Class objectClass;
	private String propertyName;
	private String errorKey;
	
	@SpringBean
	private GeneralService generalService;

	public DuplicatePropertyValidator(Class objectClass, String propertyName, String errorKey) {
		Injector.get().inject(this);
		this.objectClass = objectClass;
		this.propertyName = propertyName;
		this.errorKey = errorKey;
	}	

	@Override
	public void validate(IValidatable<String> validatable) {
		String name = validatable.getValue();

		Search search = new Search(objectClass);
		search.addFilterEqual(propertyName, name);
		int count = generalService.count(search);
		if (count > 0) {
			validatable.error(new ValidationError(errorKey));
		}
		
	}

}