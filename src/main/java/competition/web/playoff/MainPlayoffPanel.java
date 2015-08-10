package competition.web.playoff;

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

import competition.domain.entity.Competition;
import competition.domain.entity.Stage;
import competition.domain.entity.StagePlayoff;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.common.panel.InfoPanel;

public class MainPlayoffPanel extends Panel {
	
	@SpringBean
	private BusinessService businessService;
	
	@SpringBean
	private GeneralService generalService;

	private WebMarkupContainer container;
	private DropDownChoice<StagePlayoff> stageChoice;	
	private ModalWindow dialog;

	private StagePlayoff currentStage = null;
	private int competitionId = -1;
	
	public MainPlayoffPanel(String id, IModel<Competition> model) {
		super(id, model);
		setOutputMarkupId(true);	
		
		competitionId = model.getObject().getId();
		currentStage = businessService.getCurrentStagePlayoff(competitionId);
		
		Competition competition = generalService.find(Competition.class, competitionId);
		
		String title = competition.getName() + " - Etape Playoff";	
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

				
		stageChoice = new DropDownChoice<StagePlayoff>("stage", new PropertyModel<StagePlayoff>(this, "currentStage"), new StagesPlayoffModel(
				competitionId), new StagePlayoffChoiceRenderer());		
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
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu s-au creat inca etapele de playoff."));
			dialog.show(target);
			return;
		}
		StagePlayoff prev = businessService.getPreviousStagePlayoff(currentStage);
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
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu s-au creat inca etapele de playoff."));
			dialog.show(target);
			return;
		}
		StagePlayoff next = businessService.getNextStagePlayoff(currentStage);
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
			stagePanel = new EmptyPanel("stagePlayoffPanel");
		} else {
			stagePanel = new ViewStagePlayoffPanel("stagePlayoffPanel", new Model<StagePlayoff>(currentStage));
		}
		return stagePanel;
	}

}
