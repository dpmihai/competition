package competition.web.user.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.service.ChartService;
import competition.web.security.SecurityUtil;


public class ChartPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private BusinessService businessService;
	
	@SpringBean
	private ChartService chartService;
	
	private String username;
	private WebMarkupContainer chartContainer;
	
	private final String BAF = "Baftosul";
	private final String GAZ = "Gazduitorul";
	private final String VIZ = "Vizitatorul";
	private final String ECH = "Echilibratul";
	
	private String prize = BAF;
	private WebMarkupContainer prizeContainer;
	

	public ChartPanel(String id, final IModel<Competition> competition ) {
		super(id);
		
		this.username = SecurityUtil.getLoggedUsername();	
		
		String title = competition.getObject().getName() + " - Grafic";				
		add(new Label("fullTopLabel", title));		
		
		Form<String> form = new Form<String>("userForm");
		
		chartContainer = new WebMarkupContainer("chartContainer");
		chartContainer.setOutputMarkupId(true);
		chartContainer.add(getHTML5Panel(competition.getObject(), "chart", "UserPointsPerCompetition"));
		
		form.add(chartContainer);
		
		ArrayList<String> names = new ArrayList<String>();
		try {
			List<User> users = businessService.getRegisteredUsers(competition.getObject().getId());
			for (User user : users) {
				names.add(user.getUsername());
			}
		} catch (Exception e) {

		}
		
		DropDownChoice<String> userChoice = new DropDownChoice<String>("users",	new PropertyModel<String>(this, "username"), names);
		userChoice.setOutputMarkupPlaceholderTag(true);				
		userChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {									
				//OpenFlashChart ofc = getOFC(competition.getObject(), "chart", "UserPointsPerCompetition");
				ChartHTML5Panel chartPanel = getHTML5Panel(competition.getObject(), "chart", "UserPointsPerCompetition");
				chartContainer.replace(chartPanel);
		        target.add(chartContainer);		        
			}
		});    			
		
		form.add(userChoice);
		
		add(form);
		
		
		
		Form<String> prizeForm = new Form<String>("prizeForm");
		
		prizeContainer = new WebMarkupContainer("prizeContainer");
		prizeContainer.setOutputMarkupId(true);
		prizeContainer.add(getHTML5Panel(competition.getObject(), "prizeChart", BAF));
		
		prizeForm.add(prizeContainer);
		
		List<String> prizes = Arrays.asList(BAF, GAZ, VIZ, ECH);		
		
		final DropDownChoice<String> prizeChoice = new DropDownChoice<String>("prizes",	new PropertyModel<String>(this, "prize"), prizes);
		prizeChoice.setOutputMarkupPlaceholderTag(true);				
		prizeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {		
				
				ChartHTML5Panel chartPanel = getHTML5Panel(competition.getObject(), "prizeChart", prizeChoice.getDefaultModelObjectAsString());
				prizeContainer.replace(chartPanel);	
				target.add(prizeContainer);	
				
//				OpenFlashChart ofc = getOFC(competition.getObject(), "prizeChart", prizeChoice.getDefaultModelObjectAsString());
//				prizeContainer.replace(ofc);
//		        target.add(prizeContainer);		        
			}
		});    			
		
		prizeForm.add(prizeChoice);
		
		add(prizeForm);
				
      
	}		
	
//	private OpenFlashChart getOFC(Competition competition, String id, String chartName) {
//		Map<String, Object> parameterValues = new HashMap<String, Object>();
//		parameterValues.put("Competition", competition.getName());
//		parameterValues.put("User", username);
//		String jsonData = chartService.getJsonData(chartName, parameterValues);			
//		OpenFlashChart ofc =  new OpenFlashChart(id, "100%", "100%", new Model<String>(jsonData));
//		ofc.setOutputMarkupId(true);
//		return ofc;
//	}
	
	private ChartHTML5Panel getHTML5Panel(Competition competition, String id, String chartName) {
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("Competition", competition.getName());
		parameterValues.put("User", username);
		String jsonData = chartService.getJsonData(chartName, parameterValues);		
		System.out.println(jsonData);
		ChartHTML5Panel panel =  new ChartHTML5Panel(id, "100%", "350", new Model<String>(jsonData));
		panel.setOutputMarkupId(true);
		return panel;
	}

   
}
