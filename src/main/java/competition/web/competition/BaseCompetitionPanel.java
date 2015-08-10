package competition.web.competition;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import competition.domain.entity.Competition;
import competition.web.BaseAppSession;
import competition.web.common.panel.InfoPanel;
import competition.web.game.TickerPanel;
import competition.web.playoff.MainPlayoffPanel;
import competition.web.rss.RssFeedPanel;
import competition.web.stage.MainStagesPanel;
import competition.web.team.TeamRankingPanel;
import competition.web.user.TopUsersPanel;
import competition.web.user.UserQuestionPanel;
import competition.web.user.UserReportPanel;
import competition.web.user.chart.ChartPanel;

public class BaseCompetitionPanel extends Panel {	
	
	private WebMarkupContainer container;		
	
	private Competition competition;
	private boolean stagesSelected = false;

	private ReportsLink reportsLink;
	private QuestionsLink questionsLink;
	private ChartsLink chartsLink;
	private PlayoffLink playoffLink;
	private ModalWindow dialog;

	public BaseCompetitionPanel(String id, final IModel<Competition> competition, boolean top) {
		super(id, competition);

		this.competition = competition.getObject();				
		
		dialog = new ModalWindow("dialog");
        add(dialog);
		
		add(new TickerPanel("tickerGames", this.competition.getId()).setOutputMarkupId(true));
		
		add(new StagesLink("stages"));
		add(new UsersTopLink("usersTop"));
		add(new TeamsTopLink("teamsTop"));
		
		playoffLink = new PlayoffLink("playoff");
		playoffLink.setOutputMarkupId(true);
		add(playoffLink);
		
		reportsLink = new ReportsLink("reports");
		reportsLink.setOutputMarkupId(true);
		add(reportsLink);
		
		questionsLink = new QuestionsLink("questions");
		questionsLink.setOutputMarkupId(true);
		add(questionsLink);
		
		chartsLink = new ChartsLink("charts");
		chartsLink.setOutputMarkupId(true);
		add(chartsLink);
						
		add(new NewsLink("news"));
		
		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);		
		
		Panel panel;
		if (top) {
			panel = new TopUsersPanel("content", competition.getObject(), -1);
		} else {
			panel = new MainStagesPanel("content", competition);
			stagesSelected = true;
		}	
		panel.setOutputMarkupId(true);
		container.add(panel);
	}
	
	private final class StagesLink extends AjaxLink {

		public StagesLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewStages(target);			
		}					
		
	}
	
	private final class TeamsTopLink extends AjaxLink {

		public TeamsTopLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewTeamsTop(target);			
		}					
		
	}
	
	private final class UsersTopLink extends AjaxLink {

		public UsersTopLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewUsersTop(target);			
		}					
		
	}
	
	private final class NewsLink extends AjaxLink {

		public NewsLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewNews(target);			
		}					
		
	}
	
	private final class ReportsLink extends AjaxLink {

		public ReportsLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewReports(target);			
		}

		@Override
		public boolean isEnabled() {
			return BaseAppSession.get().isSignedIn();
		}		
						
	}
	
	private final class QuestionsLink extends AjaxLink {

		public QuestionsLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewQuestions(target);			
		}

		@Override
		public boolean isEnabled() {
			return BaseAppSession.get().isSignedIn();
		}

		@Override
		public boolean isVisible() {
			Date quizDate = competition.getQuizDate();
			return (quizDate != null);
		}												
	}
	
	private final class ChartsLink extends AjaxLink {

		public ChartsLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewCharts(target);			
		}

		@Override
		public boolean isEnabled() {
			return BaseAppSession.get().isSignedIn();
		}		
	}
	
	private final class PlayoffLink extends AjaxLink {

		public PlayoffLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewPlayoff(target);			
		}

		@Override
		public boolean isEnabled() {
			return BaseAppSession.get().isSignedIn();
		}

		@Override
		public boolean isVisible() {
			return competition.getPlayoffFirstStageId() != null;
		}												
	}
	
	private void viewTeamsTop(AjaxRequestTarget target) {
		stagesSelected = false;
		TeamRankingPanel panel = new TeamRankingPanel("content", competition.getId(), true);
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewNews(AjaxRequestTarget target) {
		stagesSelected = false;
		if ((competition.getRss() == null) || competition.getRss().trim().equals("")){
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Competitia nu are setat un link de rss."));
			dialog.show(target);
			return;
		}
		RssFeedPanel panel = new RssFeedPanel("content", competition, 12);
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewStages(AjaxRequestTarget target) {
		stagesSelected = true;
		MainStagesPanel panel = new MainStagesPanel("content", new Model<Competition>(competition));
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewUsersTop(AjaxRequestTarget target) {
		stagesSelected = false;
		TopUsersPanel panel = new TopUsersPanel("content", competition, -1);
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewReports(AjaxRequestTarget target) {
		stagesSelected = false;
		UserReportPanel panel = new UserReportPanel("content", new Model<Competition>(competition));	
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewQuestions(AjaxRequestTarget target) {
		stagesSelected = false;
		UserQuestionPanel panel = new UserQuestionPanel("content", new Model<Competition>(competition));	
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewCharts(AjaxRequestTarget target) {
		stagesSelected = false;
		ChartPanel panel = new ChartPanel("content", new Model<Competition>(competition));	
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	private void viewPlayoff(AjaxRequestTarget target) {
		stagesSelected = false;
		MainPlayoffPanel panel = new MainPlayoffPanel("content", new Model<Competition>(competition));
		panel.setOutputMarkupId(true);
		container.replace(panel);
		target.add(container);
	}
	
	public void refresh(AjaxRequestTarget target) {
		target.add(reportsLink);
		target.add(chartsLink);
		target.add(questionsLink);
		target.add(playoffLink);
		if (stagesSelected) {
			// refresh to show individual/general combo
			viewStages(target);
		}
	}

}
