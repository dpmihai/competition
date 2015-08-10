package competition.web.competition;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

import competition.domain.entity.Competition;
import competition.web.common.form.FormContentPanel;
import competition.web.user.validator.DuplicatePropertyValidator;

public class CompetitionPanel extends FormContentPanel<Competition> {
	
	public CompetitionPanel(String id, IModel<Competition> model) {
		super(id, model);

		TextField<String> name = new TextField<String>("name");
		name.setRequired(true);
		name.add(new DuplicatePropertyValidator(Competition.class, "name", "competition.errorNameExists"));
		name.setLabel(Model.of("Nume"));
		name.add(StringValidator.minimumLength(3));
		name.add(StringValidator.maximumLength(50));
		addWithFeedback(name);
		
		TextField<String> imageFile = new TextField<String>("imageFile");
		imageFile.setRequired(true);		
		imageFile.setLabel(Model.of("Imagine"));		
		addWithFeedback(imageFile);				
		
	}	


}
