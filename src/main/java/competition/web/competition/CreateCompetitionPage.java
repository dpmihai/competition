package competition.web.competition;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.service.UserService;
import competition.web.BaseTemplatePage;
import competition.web.common.form.FormPanel;

public class CreateCompetitionPage extends BaseTemplatePage {

	private Competition competition = new Competition();

	private WebMarkupContainer container;

	@SpringBean
	private GeneralService generalService;

	public CreateCompetitionPage() {
		super();

		IModel<Competition> model = new CompoundPropertyModel<Competition>(competition);
		CompetitionPanel competitionPanel = new CompetitionPanel(FormPanel.CONTENT_ID, model) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onOk(AjaxRequestTarget target) {
				super.onOk(target);
				createCompetition(target);
			}

		};

		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);

		FormPanel<Competition> formPanel = new FormPanel<Competition>("formPanel", competitionPanel);
		formPanel.getCancelButton().setVisible(false);
		container.add(formPanel);
	}

	private void createCompetition(AjaxRequestTarget target) {
		generalService.merge(competition);

		Label messageLabel = new Label(
				"formPanel",
				"<div class=\"success\">Competitia a fost creata cu succes.</div>");
		messageLabel.setEscapeModelStrings(false);
		container.replace(messageLabel);
		target.add(container);
	}

}
