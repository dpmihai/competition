package competition.web.stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.form.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.UserScore;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.common.panel.InfoPanel;
import competition.web.game.AddEditUserScoresPanel;
import competition.web.security.SecurityUtil;
import competition.web.util.DateUtil;

public class MainStagesPanel extends Panel {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainStagesPanel.class);

	@SpringBean
	private BusinessService businessService;
	
	@SpringBean
	private GeneralService generalService;

	private WebMarkupContainer container;
	private DropDownChoice<Stage> stageChoice;	
	private ModalWindow dialog;

	private Stage currentStage = null;
	private int competitionId = -1;

	public MainStagesPanel(String id, IModel<Competition> model) {
		super(id, model);
		setOutputMarkupId(true);			
		
		competitionId = model.getObject().getId();
		currentStage = businessService.getCurrentShownStage(competitionId);
		
		Competition competition = generalService.find(Competition.class, competitionId);
		
		String title = competition.getName() + " - Etape";	
		add(new Label("fullLabel", title));
		
		dialog = new ModalWindow("dialog");
        add(dialog);
						
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
		boolean ok = SecurityUtil.getLoggedUser().isAdmin() ||
				     ( (DateUtil.getYear(stageDate) == DateUtil.getYear(nowDate)) &&
			           ((DateUtil.getDayOfYear(stageDate) - DateUtil.getDayOfYear(nowDate) > 0) ||
			        	( (DateUtil.getDayOfYear(stageDate) == DateUtil.getDayOfYear(nowDate)) && (DateUtil.getHour(nowDate) <= 12) )	   
			           )
			          ) ||
			          (DateUtil.getYear(stageDate) > DateUtil.getYear(nowDate));		
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
					// if entered by admin after game finished and score already entered 
					// (in case of an user error accepted by all other users)
					// must recompute users scores
					List<Integer> gameIds = new ArrayList<Integer>();
					for (UserScore score : scores) {
						Game game = generalService.find(Game.class, score.getGameId());
						if ((game.getHostsScore() != null) && (game.getGuestsScore() != null)) {
							gameIds.add(score.getGameId());
						}	
					}
					if (gameIds.size() > 0) {						
						businessService.computeAndSetUserScore(gameIds);
					}
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
	
}
