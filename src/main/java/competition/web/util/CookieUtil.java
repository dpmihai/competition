package competition.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import competition.domain.entity.UserCookie;

public class CookieUtil {
	
	public static final String USER_COOKIE_NAME = "competition_userid";
	public static final String REMEMBER_COOKIE_NAME = "competition_rememberme";
	public static final String SEPARATOR = "#";
	
	public static void addUserCookie(String username, String password) {
		WebResponse servletResponse = (WebResponse)RequestCycle.get().getResponse();
		Cookie c = new Cookie(USER_COOKIE_NAME, encodeString(username + SEPARATOR + password));
		c.setMaxAge(365 * 24 * 60 * 60); // one year
		servletResponse.addCookie(c);	
	}
	
	public static void addRememberCookie(boolean rememberme) {
		WebResponse servletResponse = (WebResponse)RequestCycle.get().getResponse();
		Cookie c = new Cookie(REMEMBER_COOKIE_NAME, encodeString(String.valueOf(rememberme)));
		c.setMaxAge(365 * 24 * 60 * 60); // one year
		servletResponse.addCookie(c);		
	}
	
	public static UserCookie getUserCookie(HttpServletRequest servletRequest) {
		Cookie[] cookies = servletRequest.getCookies();
		if (cookies != null) {
			String value = null;
			boolean rememberme = false;
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				if (c.getName().equals(USER_COOKIE_NAME)) {
					value = decodeString(c.getValue());
				} else if (c.getName().equals(REMEMBER_COOKIE_NAME)) {
					rememberme = Boolean.valueOf(decodeString(c.getValue()));
				}
			}
			if (value != null) {
				int index = value.indexOf(SEPARATOR);
				if (index != -1) {
					String username = value.substring(0, index);
					String password = value.substring(index + 1);
					return new UserCookie(username, password, rememberme);
				}
			}
		}
		return null;
	}
	
	public static void removeCookies() {
		WebRequest servletRequest = (WebRequest)RequestCycle.get().getRequest();		
		WebResponse servletResponse = (WebResponse)RequestCycle.get().getResponse();
		List<Cookie> cookies = servletRequest.getCookies();				
		for (Cookie c : cookies) {			
			if (c.getName().equals(USER_COOKIE_NAME) || c.getName().equals(REMEMBER_COOKIE_NAME)) {
				c.setMaxAge(0);
				c.setValue(null);
				servletResponse.addCookie(c);	
			} 
		}
	}
	
	private static String encodeString(String s) {
	    String encodedString = s;
	    try{
	        encodedString = URLEncoder.encode(s, "UTF-8");
	    } catch (UnsupportedEncodingException e) {}

	    return encodedString;
	}
	
	private static String decodeString(String s) {
	    String decodedString = s;
	    try{
	        decodedString = URLDecoder.decode(s, "UTF-8");
	    } catch (UnsupportedEncodingException e) {}
	    return decodedString;
	}


}
