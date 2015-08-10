package competition.web;

import java.io.InputStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.job.UserRankingJob;
import competition.job.UserTotalScoreJob;
import competition.job.build.ImportCompetitionJob;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.AjaxConfirmLink;
import competition.web.common.form.FormPanel;
import competition.web.competition.CreateCompetitionsPage;
import competition.web.competition.CreateQuestionsPage;
import competition.web.game.CreateGamesPage;
import competition.web.stage.CreateStagesPage;
import competition.web.team.CreateTeamsPage;
import competition.web.user.CreateUsersPage;
import competition.web.user.LoginUsersPage;
import competition.web.util.SelectFilePanel;

public class AdminPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(AdminPanel.class);
    
    @SpringBean
    protected GeneralService generalService;
    
    @SpringBean
    protected BusinessService businessService;

	public AdminPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		add(new CreateAccountLink("createAccount"));
		add(new CreateCompetitionLink("createCompetition"));
		add(new CreateQuestionsLink("createQuestions"));
		add(new CreateTeamsLink("createTeams"));
		add(new CreateStagesLink("createStages"));		
		add(new CreateGamesLink("createGames"));
		add(new RunJobLink("runJob"));
		add(new ResetRankingLink("resetRanking"));
		add(new ImportCompetitionLink("import"));
		add(new LoginHistoryLink("loginHistory"));
	}
		
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class CreateAccountLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public CreateAccountLink(String id) {
			super(id, CreateUsersPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class CreateCompetitionLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public CreateCompetitionLink(String id) {
			super(id, CreateCompetitionsPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class CreateQuestionsLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public CreateQuestionsLink(String id) {
			super(id, CreateQuestionsPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class CreateTeamsLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public CreateTeamsLink(String id) {
			super(id, CreateTeamsPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class CreateStagesLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public CreateStagesLink(String id) {
			super(id, CreateStagesPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class CreateGamesLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public CreateGamesLink(String id) {
			super(id, CreateGamesPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class RunJobLink extends AjaxConfirmLink<Void> {

		private static final long serialVersionUID = 1L;

		public RunJobLink(String id) {
			super(id,"Vrfeti sa rulati procesul de actualizare puncte?");	
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

		@Override
		public void onClick(AjaxRequestTarget target) {
			UserTotalScoreJob job = new UserTotalScoreJob();
			job.setGeneralService(generalService);
			job.setBusinessService(businessService);
			job.run();
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class ResetRankingLink extends AjaxConfirmLink<Void> {

		private static final long serialVersionUID = 1L;

		public ResetRankingLink(String id) {
			super(id, "Vreti sa resetati ranking-urile?");					
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			UserRankingJob job = new UserRankingJob();
			job.setGeneralService(generalService);
			job.setBusinessService(businessService);
			job.run();			
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class ImportCompetitionLink extends AjaxLink<Void> {

		private static final long serialVersionUID = 1L;

		public ImportCompetitionLink(String id) {
			super(id);					
		}		

		@Override
		public void onClick(AjaxRequestTarget target) {
			ModalWindow dialog = findParent(BaseTemplatePage.class).getDialog();
			dialog.setTitle("Importa competitie");
			dialog.setAutoSize(true);

			dialog.setContent(new FormPanel<Void>(dialog.getContentId(), new SelectFilePanel() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onOk(AjaxRequestTarget target) {					
					try {
						final FileUpload uploadedFile = uploadField.getFileUpload();
						if (uploadedFile != null) {
							InputStream is = uploadedFile.getInputStream();
							ImportCompetitionJob job = new ImportCompetitionJob(is);
							job.setGeneralService(generalService);			
							job.run();
							LOG.info("Succesfully imported competition.");
						}
						ModalWindow.closeCurrent(target);
					} catch (Exception e) {
						LOG.error(e.toString(), e);
						error("Nu am putut importa competitia");
					}
				}

			}));
			dialog.show(target);							
		}

	}
	
	@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
	private final class LoginHistoryLink extends BookmarkablePageLink<Void> {

		private static final long serialVersionUID = 1L;

		public LoginHistoryLink(String id) {
			super(id, LoginUsersPage.class);		
			//add(new SimpleAttributeModifier("class", "right-border"));
		}

	}


}
