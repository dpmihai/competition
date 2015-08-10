package competition.web.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.Competition;
import competition.domain.entity.User;
import competition.domain.entity.UserRegistration;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.palette.ExtendedPalette;
import competition.web.common.palette.StringChoiceRenderer;
import competition.web.competition.CompetitionChoiceRenderer;

public class UserRegistrationPanel extends Panel {
		
	private static final long serialVersionUID = 1L;
	private Competition competition;
	private UsersRegistrationDataProvider provider;
	private ModalWindow dialog;
	private ExtendedPalette<String> palette;
	private List<String> registeredUsers = new ArrayList<String>();
	
	@SpringBean
	private GeneralService generalService;		
	
	@SpringBean
	private BusinessService businessService;		
	
	public UserRegistrationPanel(String id) {
		super(id);
		init();
	}
	
	private void init() {		                   	
		
		ArrayList<String> users = new UsersDataProvider().getUserNames();
		provider =  new UsersRegistrationDataProvider(getCompetitionId());
		
		Form form = new Form("form");
		
		form.add(new Label("title", "Inregistrare Utilizatori"));  
		
       	List<Competition> competitions = generalService.search(new Search(Competition.class).addSort(new Sort("name", true)));
       	if (competitions.size() > 0) {
       		competition = competitions.get(0);
       		provider.setCompetitionId(getCompetitionId());
			registeredUsers = provider.getRegisteredUsers();
       	}
		DropDownChoice<Competition> competitionChoice = new DropDownChoice<Competition>("competition", 
				new PropertyModel<Competition>(this, "competition"), competitions, new CompetitionChoiceRenderer());		
		competitionChoice.setOutputMarkupPlaceholderTag(true);
		form.add(competitionChoice);
		competitionChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				provider.setCompetitionId(getCompetitionId());
				registeredUsers = provider.getRegisteredUsers();
				target.add(palette);						
			}
		});     
		
		palette = new ExtendedPalette<String>("palette", 
				new PropertyModel<List<String>>(this, "registeredUsers"), 
				new Model<ArrayList<String>>(users) ,  
				new StringChoiceRenderer(), 10, false, true);
		palette.setOutputMarkupId(true);
		form.add(palette);
		
		form.add(new AjaxSubmitLink("register") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
            	 int competitionId = getCompetitionId();
            	 List<String> selected = getRegisteredUsers();            	 
            	 businessService.registerUsers(competitionId, selected);
            }

        });        
        
        dialog = new ModalWindow("dialog");
        form.add(dialog);
        
        add(form);
	}
	
	public int getCompetitionId() {
		int competitionId = -1;
		if (competition != null) {
			competitionId = competition.getId();			
		} 
		return competitionId;
	}
	
	public List<String> getRegisteredUsers() {
		return registeredUsers;
	}

}
