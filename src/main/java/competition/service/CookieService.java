package competition.service;

import javax.servlet.http.HttpServletRequest;

import competition.domain.entity.UserCookie;

public interface CookieService {
	
	public static final String USER_COOKIE_NAME = "competition_userid";
	public static final String REMEMBER_COOKIE_NAME = "competition_rememberme";
	
	public void addUserCookie(String username);
	
	public void addRememberCookie(boolean rememberme);
	
	public UserCookie getUserCookie(HttpServletRequest servletRequest);
	
	public void removeCookies();

}
