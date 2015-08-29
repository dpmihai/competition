package competition.web;

import java.util.Date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.User;
import competition.service.GeneralService;
import competition.web.common.form.FormPanel;
import competition.web.security.GuardedLoginAjaxLink;
import competition.web.security.SecurityUtil;
import competition.web.user.ChangePasswordPanel;

/**
 * @author Decebal Suiu
 */
public class HeaderPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(HeaderPanel.class);
    
    @SpringBean
    protected GeneralService generalService;


	public HeaderPanel(String id) {
		super(id);

		setOutputMarkupId(true);

		add(DateLabel.forDatePattern("date", new Model<Date>(new Date()), "EEEE d MMMM yyyy"));		
				
		Label loggedLabel = new Label("loggedUsers", new LoadableDetachableModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				int count = BaseApplication.get().getLoggedUsers();
				if (count == 1) {
					return count + " utilizator online";
				} else {
					return count + " utilizatori online";
				}
			}
		});											
		loggedLabel.setOutputMarkupId(true);
		add(loggedLabel);
		
		Link<Void> homeLink = new BookmarkablePageLink<Void>("home", HomePage.class);
		ContextImage logoImage = new ContextImage("logoImage", "img/logo.png");
		logoImage.add(AttributeModifier.replace("alt", new ResourceModel("html_index_description").getObject()));
		homeLink.add(logoImage);		
		add(homeLink);

		add(new WelcomeContainer("welcome"));
		add(new LoginLink("login"));		
		add(new ChangePasswordLink("changePassword"));		
		//add(new ReportLink("generateReport"));
		// add(new BookmarkablePageLink<Void>("user-details",
		// UserDetailsPage.class) {
		//
		// @Override
		// public boolean isVisible() {
		// return ((AuthenticatedWebSession) getSession()).isSignedIn();
		// }
		//
		// });

		add(new LogoutLink("logout"));

	}

	@AuthorizeAction(action = Action.RENDER, roles = Roles.USER)
	private final class WelcomeContainer extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public WelcomeContainer(String id) {
			super(id);

			Label userLabel = new Label("user", new LoadableDetachableModel<String>() {

				private static final long serialVersionUID = 1L;

				@Override
				protected String load() {
					return BaseAppSession.get().getUsername();
				}

			});
			userLabel.setOutputMarkupId(true);
			add(userLabel);

			add(AttributeModifier.replace("class", "right-border"));
		}

	}

	@AuthorizeAction(action = Action.RENDER, deny = { Roles.USER, Roles.ADMIN })
	private final class LoginLink extends GuardedLoginAjaxLink<Void> {

		private static final long serialVersionUID = 1L;

		public LoginLink(String id) {
			super(id);			
		}

	}

	@AuthorizeAction(action = Action.RENDER, roles = Roles.USER)
	private final class LogoutLink extends Link<Void> {

		private static final long serialVersionUID = 1L;

		public LogoutLink(String id) {
			super(id);
		}

		@Override
		public void onClick() {
			if (SecurityUtil.getLoggedUser() != null) {
				LOG.info("User " + SecurityUtil.getLoggedUser().getUsername() + "  logged out at " + new Date());
			}
			BaseAppSession.get().signOut();
			BaseApplication.get().removeLoggedUser(BaseAppSession.get().getId());
			setResponsePage(getApplication().getHomePage());
		}

	}
			
	@AuthorizeAction(action = Action.RENDER, roles = Roles.USER)
	private class ChangePasswordLink extends AjaxLink<Void> {
		
		private static final long serialVersionUID = 1L;

		public ChangePasswordLink(String id) {
			super(id);
			add(AttributeModifier.replace("class", "right-border"));
		}

		@Override
		public void onClick(AjaxRequestTarget target) {
			ModalWindow dialog = findParent(BaseTemplatePage.class).getDialog();
			dialog.setTitle("Schimba parola utilizator");
			dialog.setAutoSize(true);

			dialog.setContent(new FormPanel<Void>(dialog.getContentId(), new ChangePasswordPanel() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onOk(AjaxRequestTarget target) {
					User user = SecurityUtil.getLoggedUser();
					try {
						user.setPassword(newPassword);

						generalService.merge(user);
						LOG.info("Succesfully changed password for user " + user);

						ModalWindow.closeCurrent(target);
					} catch (Exception e) {
						LOG.error(e.toString(), e);
						error("Nu am putut modifica parola");
					}
				}

			}));
			dialog.show(target);
		}
	};	
	
//	@AuthorizeAction(action = Action.RENDER, roles = Roles.USER)
//	private final class ReportLink extends BookmarkablePageLink<Void> {
//
//		private static final long serialVersionUID = 1L;
//
//		public ReportLink(String id) {
//			super(id, UserReportPage.class);		
//			add(new SimpleAttributeModifier("class", "right-border"));
//		}
//
//	}
	
	
}
