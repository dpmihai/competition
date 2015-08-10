package competition.web.user;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Question;
import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.security.SecurityUtil;
import competition.web.util.TeamUtil;

public class ViewAllUsersQuestionPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private int competitionId;
	
	@SpringBean
	private GeneralService service;
	
	@SpringBean
	private BusinessService businessService;
	
	public ViewAllUsersQuestionPanel(String id, IModel<Question> question, int competitionId) {
		super(id);	
		this.competitionId = competitionId;
		setDefaultModel(new CompoundPropertyModel<Question>(question.getObject()));
														
		Label qLabel = new Label("question", Model.of(question.getObject().getQuestionFragment()));
		qLabel.add(AttributeModifier.append("title", question.getObject().getQuestion()));
		add(qLabel);		
		
		String r = question.getObject().getResponse();
		try { 
			Integer.parseInt(r);
		} catch (NumberFormatException ex) {
			// it is a team name
			r = TeamUtil.getAbbreviation(r);
		}
		Label teamLabel = new Label("response", r);
		teamLabel.add(new SimpleTooltipBehavior(question.getObject().getResponse()));
		add(teamLabel);				
		
		ListView<SmallUserQuestionPanel> listView = new ListView<SmallUserQuestionPanel>("usersResults", new ScoresModel(question)) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<SmallUserQuestionPanel> item) {
               item.add(item.getModelObject());
            }

        };
        listView.setReuseItems(true);
        
        String username = SecurityUtil.getLoggedUsername();		
		listView.setVisible(username != null);
		add(listView);
		
	}
	
    class ScoresModel extends LoadableDetachableModel<List<SmallUserQuestionPanel>> {
    	
		private static final long serialVersionUID = 1L;
		private IModel<Question> question;
		
		public ScoresModel(IModel<Question> question) {
			super();
			this.question = question;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected List<SmallUserQuestionPanel> load() {			
			try {
				if (SecurityUtil.getLoggedUser() == null) {
					return new ArrayList<SmallUserQuestionPanel>();
				} else {
					List<SmallUserQuestionPanel> panels = new ArrayList<SmallUserQuestionPanel>();
					List<User> users = businessService.getRegisteredUsers(competitionId);
					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User u1, User u2) {
							return Collator.getInstance().compare(u1.getTeam(), u2.getTeam());						
						}
						
					});
					for (User user : users) {
						panels.add(new SmallUserQuestionPanel("userResult", user, question));
					}	
					return panels;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<SmallUserQuestionPanel>();
			}
		}

    }
		

}
