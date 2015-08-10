package competition.web.stage;

import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.form.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Stage;
import competition.domain.entity.UserScore;
import competition.service.BusinessService;
import competition.web.BaseTemplatePage;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.common.panel.InfoPanel;
import competition.web.competition.CompetitionNewsPage;
import competition.web.game.AddEditUserScoresPanel;
import competition.web.game.TickerPanel;
import competition.web.security.SecurityUtil;
import competition.web.team.TopTeamsPage;
import competition.web.util.DateUtil;

public class ViewStagePage extends BaseTemplatePage {
	
	private static final Logger LOG = LoggerFactory.getLogger(ViewStagePage.class);

	@SpringBean
	private BusinessService businessService;

	private WebMarkupContainer container;
	private DropDownChoice<Stage> stageChoice;	

	private Stage currentStage = null;
	private int competitionId = -1;

	public ViewStagePage(PageParameters pageParameters) {
		super(pageParameters);
		setOutputMarkupId(true);			
		
		competitionId = pageParameters.get("competitionId").toInt();
		currentStage = businessService.getCurrentStage(competitionId);
		
		add(new TickerPanel("tickerGames", competitionId));
				
		Panel stagePanel = createStagePanel();

		AjaxLink prevLink = new AjaxLink("previousStage") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				previous(target);
			}
		};
		prevLink.add(new SimpleTooltipBehavior("Etapa anterioara"));
		add(prevLink);

		AjaxLink nextLink = new AjaxLink("nextStage") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				next(target);
			}
		};
		nextLink.add(new SimpleTooltipBehavior("Etapa urmatoare"));
		add(nextLink);

		add(new SaveScoresLink("saveScores"));
		add(new TeamsTopLink("teamsTop"));
		add(new NewsLink("news"));
				
		stageChoice = new DropDownChoice<Stage>("stage", new PropertyModel<Stage>(this, "currentStage"), new StagesModel(
				competitionId), new StageChoiceRenderer());		
		stageChoice.setOutputMarkupPlaceholderTag(true);
		add(stageChoice);
		stageChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateStage(target, false);
			}
		});

		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);

		container.add(stagePanel);

	}

	private void previous(AjaxRequestTarget target) {
		// stages were not created
		if (currentStage == null) {
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);		
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu s-au creat inca etapele."));
			dialog.show(target);
			return;
		}
		Stage prev = businessService.getPreviousStage(currentStage);
		if (prev != null) {
			currentStage = prev;
			updateStage(target, true);
		}
	}

	private void next(AjaxRequestTarget target) {
		// stages were not created
		if (currentStage == null) {
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);			
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu s-au creat inca etapele."));
			dialog.show(target);
			return;
		}
		Stage next = businessService.getNextStage(currentStage);
		if (next != null) {
			currentStage = next;
			updateStage(target, true);
		}
	}

	public void updateStage(AjaxRequestTarget target, boolean updateStageChoice) {
		Panel stagePanel = createStagePanel();
		container.replace(stagePanel);
		target.add(container);
		if (updateStageChoice) {
			target.add(stageChoice);
		}
	}

	private Panel createStagePanel() {
		Panel stagePanel;
		if (currentStage == null) {
			stagePanel = new EmptyPanel("stagePanel");
		} else {
			stagePanel = new ViewStagePanel("stagePanel", new Model<Stage>(currentStage));
		}
		return stagePanel;
	}

	private final class SaveScoresLink extends AjaxLink {

		public SaveScoresLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			editScores(target);
			
		}					
		
	}
	
	private final class TeamsTopLink extends AjaxLink {

		public TeamsTopLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewTeamsTop();			
		}					
		
	}
	
	private final class NewsLink extends AjaxLink {

		public NewsLink(String id) {
			super(id);
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			viewNews();			
		}					
		
	}


	private void editScores(AjaxRequestTarget target) {
		
		if (SecurityUtil.getLoggedUser() == null) {
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);			
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Trebuie sa va logati pentru a edita scorurile."));
			dialog.show(target);
			return;
		}
		
		// stages were not created
		if (currentStage == null) {
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);			
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu s-au creat inca etapele."));
			dialog.show(target);
			return;
		}
		
		if (!businessService.isUserRegistered(currentStage.getCompetitionId(), SecurityUtil.getLoggedUsername())) {
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);			
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu sunteti inregistrat pentru aceasta competitie. Contactati administratorul."));
			dialog.show(target);
			return;
		}
		
		Date stageDate = currentStage.getFixtureDate();
		Date nowDate = new Date();
		boolean ok = ( (DateUtil.getYear(stageDate) == DateUtil.getYear(nowDate)) &&
			           (DateUtil.getDayOfYear(stageDate) - DateUtil.getDayOfYear(nowDate) > 0) );		
		if (!ok) {		
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);			
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Scorurile unei etape pot fi completate pana cu o zi inainte de data de start."));
			dialog.show(target);
			return;
		}
				  
		dialog.setTitle("Editeaza scoruri : " + currentStage.getName());
		dialog.setAutoSize(true);
				
		dialog.setContent(new AddEditUserScoresPanel(dialog.getContentId(), currentStage) {

			@Override
			public void onAddScores(AjaxRequestTarget target, Form form, List<UserScore> scores) {
				try {
					ModalWindow.closeCurrent(target);					
					businessService.saveUserScores(scores);
					updateStage(target, true);
				} catch (Exception e) {
					e.printStackTrace();
					form.error(e.getMessage());
				}
			}

			@Override
			public void onCancel(AjaxRequestTarget target) {
				ModalWindow.closeCurrent(target);
			}

		});
		dialog.show(target);
	}		
	
	private void viewTeamsTop() {
		PageParameters pp = new PageParameters();
		pp.add("competitionId", String.valueOf(competitionId));		
		setResponsePage(TopTeamsPage.class, pp);
	}
	
	private void viewNews() {
		PageParameters pp = new PageParameters();
		pp.add("competitionId", String.valueOf(competitionId));		
		setResponsePage(CompetitionNewsPage.class, pp);
	}

}
