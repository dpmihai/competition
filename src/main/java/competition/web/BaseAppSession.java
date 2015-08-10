package competition.web;

import java.util.Locale;

import org.apache.wicket.request.Request;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import competition.domain.entity.User;
import competition.service.CookieService;
import competition.web.security.SecurityUtil;

public final class BaseAppSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(BaseAppSession.class);

	public static final Locale LOCALE_RO = new Locale("ro", "RO");		

	@SpringBean(name = "authenticationManager")
	private AuthenticationManager authenticationManager;		
	
	@SpringBean 
	private CookieService cookieService;

	public BaseAppSession(Request request) {
		super(request);

		Injector.get().inject(this);
	}

	public static BaseAppSession get() {
		return (BaseAppSession) Session.get();
	}

	public boolean isAdmin() {
		User user = getUser();
		if (user == null) {
			return false;
		}

		return user.isAdmin();
	}

	public User getUser() {
		if (!isSignedIn()) {
			return null;
		}

		return SecurityUtil.getLoggedUser();
	}

	public String getUsername() {
		User user = getUser();
		return (user == null) ? null : user.getUsername();
	}

	@Override
	public boolean authenticate(String username, String password) {				
		boolean authenticated = false;				
		try {						
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,
					password));
			setAuthentication(authentication);
			authenticated = authentication.isAuthenticated();			
		} catch (AuthenticationException e) {
			logger.warn(String.format("User '%s' failed to login. Reason: %s", username, e.getMessage()));
			authenticated = false;
		}

		return authenticated;
	}
		
	@Override
	public Roles getRoles() {
		Roles roles = new Roles();
		if (isSignedIn()) {
			roles.add(Roles.USER);
			if (isAdmin()) {
				roles.add(Roles.ADMIN);
			}
		}

		return roles;
	}

	@Override
	public void signOut() {
		super.signOut(); 
		setAuthentication(null);					
		cookieService.removeCookies();
	}

	private void setAuthentication(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}	
	
	@Override
	protected WebPage newBrowserInfoPage() {
		return new CustomBrowserInfoPage();
	}

}
