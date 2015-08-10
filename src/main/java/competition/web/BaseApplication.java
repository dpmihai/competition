package competition.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.devutils.DevUtilsPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.session.HttpSessionStore;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.IProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import competition.domain.entity.Login;
import competition.domain.entity.UserCookie;
import competition.service.AuditService;
import competition.service.CookieService;
import competition.web.common.CompetitionResourceGuard;
import competition.web.common.ClasspathFolderContentResourceReference;
import competition.web.competition.CreateCompetitionPage;
import competition.web.competition.CreateCompetitionsPage;
import competition.web.competition.CreateQuestionsPage;
import competition.web.competition.FullCompetitionPage;
import competition.web.game.CreateGamesPage;
import competition.web.security.LoginPage;
import competition.web.site.Page500;
import competition.web.stage.CreateStagesPage;
import competition.web.team.CreateTeamsPage;
import competition.web.user.CreateAccountPage;
import competition.web.user.CreateUsersPage;
import competition.web.user.HallOfFameUsersPage;
//import competition.web.user.CreateAccountPage;
import competition.web.user.LoginUsersPage;

public class BaseApplication extends AuthenticatedWebApplication {
	
	private static Logger LOG = LoggerFactory.getLogger(BaseApplication.class);
	
	private WebApplicationContext applicationContext;
	
	private int loggedUsers = 0;
	
	private int width = 1024;
	
	private List<String> loggedSessionIds = new ArrayList<String>();	
	
	public static final int PHONE_WIDTH = 480;
	
	@Override
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	@Override
	public void init() {
		super.init();

		// add spring
		getComponentInstantiationListeners().add(createSpringComponentInjector());

		// set locale
		Locale.setDefault(new Locale("ro", "RO"));

		// markup settings
		getMarkupSettings().setStripWicketTags(true);
		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
		//getMarkupSettings().setStripXmlDeclarationFromOutput(false);

		// exception settings
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		// Wicket uses SecurePackageResourceGuard by default
		// to allow acces to some files like report, chart we use our ResourceGuard
		getResourceSettings().setPackageResourceGuard(new CompetitionResourceGuard()); 

		// application settings
		// getApplicationSettings().setPageExpiredErrorPage(Page500.class);
		getApplicationSettings().setPageExpiredErrorPage(getHomePage());
		getApplicationSettings().setInternalErrorPage(Page500.class);
		// getApplicationSettings().setAccessDeniedPage(LoginPage.class);
				
		//getRequestCycleSettings().setGatherExtendedBrowserInfo(true);		 				

		// page settings
		//getPageSettings().setAutomaticMultiWindowSupport(false);
		getPageSettings().setVersionPagesByDefault(false);

		// mounts
		// mountBookmarkablePage("/404", Page404.class);
		mountPage("/error", Page500.class);
		mountPage("/login", LoginPage.class);
		
		mountPage("/admin/createAccount", CreateAccountPage.class);
		mountPage("/admin/createAccounts", CreateUsersPage.class);
		mountPage("/admin/createCompetition", CreateCompetitionPage.class);
		mountPage("/admin/createCompetitions", CreateCompetitionsPage.class);
		mountPage("/admin/createQuestions", CreateQuestionsPage.class);
		mountPage("/admin/createStages", CreateStagesPage.class);
		mountPage("/admin/createTeams", CreateTeamsPage.class);
		mountPage("/admin/createGames", CreateGamesPage.class);
		mountPage("main", FullCompetitionPage.class);		
		mountPage("hallOfFame", HallOfFameUsersPage.class);
		mountPage("lastLogin", LoginUsersPage.class);
		mountPage("guide", GuideLinesPage.class);	
		mountPage("notifier", NotifierServicePage.class);	
		
        mountResource("/audio/${name}", new ClasspathFolderContentResourceReference());
		
		//mount(new MixedParamUrlCodingStrategy("/user", UserDetailsPage.class, new String[] { "username" }));
		//mount(new QueryStringUrlCodingStrategy("/search", PostSearchPage.class));
		//mount(new QueryStringUrlCodingStrategy("/activation", ActivationPage.class));
		mountPage("/debug", DevUtilsPage.class);		

		// activate some options only in "DEVELOPMENT" mode
		if (isDevelopment()) {
			// enable request logger
			getRequestLoggerSettings().setRequestLoggerEnabled(true);
			getRequestLoggerSettings().setRequestsWindowSize(3000);

			// locate where wicket markup comes from your browser’s source
			// view
			getDebugSettings().setOutputMarkupContainerClassName(true);
		} else if (isDeployment()) {
			// TODO remove this after some debug
			getDebugSettings().setDevelopmentUtilitiesEnabled(true);
		}
		
		setSessionStoreProvider( new IProvider<ISessionStore>() {
			@Override
			public ISessionStore get() {
				return new HttpSessionStore() {
					@Override
					protected void onUnbind(String sessionId) {
						super.onUnbind(sessionId);
						synchronized (loggedSessionIds) {
							if (loggedSessionIds.contains(sessionId)) {
								removeLoggedUser(sessionId);
							}
						}
					}
				};
			}					       
		});
				
		ServletContext servletContext = super.getServletContext();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

//	@Override
//	protected ISessionStore newSessionStore() {
//		return new HttpSessionStore(this) {
//			@Override
//			protected void onUnbind(String sessionId) {
//				super.onUnbind(sessionId);
//				synchronized (loggedSessionIds) {
//					if (loggedSessionIds.contains(sessionId)) {
//						removeLoggedUser(sessionId);
//					}
//				}
//			}
//		};
//	}
	
	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return BaseAppSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	/*
	 * For easy web unit tests. See HomePageTest for more informations.
	 */
	protected SpringComponentInjector createSpringComponentInjector() {
		return new SpringComponentInjector(this);
	}

	public static BaseApplication get() {
		return (BaseApplication) WebApplication.get();
	}

	public boolean isDevelopment() {
		return RuntimeConfigurationType.DEVELOPMENT == getConfigurationType();
	}

	public boolean isDeployment() {
		return RuntimeConfigurationType.DEPLOYMENT == getConfigurationType();
	}
		
	// use this method to read if a rememberme cookie is present to auto login user after session expired
	@Override				
	public Session newSession(Request request, Response response) {		
		Session session = super.newSession(request, response);
		HttpServletRequest servletRequest = ((ServletWebRequest) request).getContainerRequest();
		CookieService cookieService = (CookieService)applicationContext.getBean("cookieService");
		UserCookie userCookie = cookieService.getUserCookie(servletRequest); 		
		if (!((BaseAppSession) session).isSignedIn() && (userCookie != null) && userCookie.isRememberme()) {											
			boolean ok = ((BaseAppSession) session).signIn(userCookie.getUsername(), userCookie.getPassword());
			if (ok) {
				//session id is not yet set
				session.bind();
				addLoggedUser(userCookie.getUsername());
				LOG.info("User " + userCookie.getUsername() + " logged at " + new Date() + " using cookie.");		
			} else {
				LOG.error("User " + userCookie.getUsername() + " could not log using cookie.");
			}
		}		
		return session;
	}	
	
	
	
	public void addLoggedUser(String username) {		
		synchronized (loggedSessionIds) {
			AuditService auditService = (AuditService)applicationContext.getBean("auditService");
			loggedUsers++;			
			loggedSessionIds.add(BaseAppSession.get().getId());
			Login lastLogin = new Login();
			lastLogin.setUsername(username);
			lastLogin.setLoginDate(new Date());
			auditService.createLogin(lastLogin);
		}
	}
	
	public void removeLoggedUser(String sessionId) {		
		synchronized (loggedSessionIds) {
			if (loggedUsers > 0) {
				loggedUsers--;
			}			
			loggedSessionIds.remove(sessionId);
		}
	}
	
	public int getLoggedUsers() {
		return loggedUsers;
	}
	
	// if we use getRequestCycleSettings().setGatherExtendedBrowserInfo(true);		
	public int getWidth() {
		WebClientInfo w = WebSession.get().getClientInfo();
		ClientProperties cp = w.getProperties();
		int sWidth = cp.getScreenWidth();
		int bWidth = cp.getBrowserWidth();
		width = Math.min(sWidth,  bWidth);		
		return width;
	}
	

}
