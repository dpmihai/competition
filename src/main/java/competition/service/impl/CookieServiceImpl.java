package competition.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GeneralDAO;

import competition.domain.entity.PersistentLogin;
import competition.domain.entity.User;
import competition.domain.entity.UserCookie;
import competition.service.CookieService;

// defined in xml because must be used in BaseApplication
@Transactional
public class CookieServiceImpl implements CookieService {
	
	@Autowired
	private GeneralDAO generalDao;

	@Transactional
	public void addUserCookie(String username) {
		String uuid = UUID.randomUUID().toString();
		PersistentLogin pl = new PersistentLogin();
		pl.setUuid(uuid);
		pl.setUsername(username);
		generalDao.merge(pl);
		WebResponse servletResponse = (WebResponse)RequestCycle.get().getResponse();
		Cookie c = new Cookie(USER_COOKIE_NAME, encodeString(uuid));
		c.setMaxAge(365 * 24 * 60 * 60); // one year
		servletResponse.addCookie(c);			
	}

	@Transactional
	public void addRememberCookie(boolean rememberme) {
		WebResponse servletResponse = (WebResponse)RequestCycle.get().getResponse();
		Cookie c = new Cookie(REMEMBER_COOKIE_NAME, encodeString(String.valueOf(rememberme)));
		c.setMaxAge(365 * 24 * 60 * 60); // one year
		servletResponse.addCookie(c);			
	}

	@Transactional(readOnly = true)
	public UserCookie getUserCookie(HttpServletRequest servletRequest) {
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
				PersistentLogin pl = generalDao.find(PersistentLogin.class, value);
				if (pl != null) {
					User user = generalDao.find(User.class, pl.getUsername());
					return new UserCookie(user.getUsername(), user.getPassword(), rememberme);
				}	
			}
		}
		return null;
	}

	@Transactional
	public void removeCookies() {
		WebRequest servletRequest = (WebRequest)RequestCycle.get().getRequest();
		WebResponse servletResponse = (WebResponse)RequestCycle.get().getResponse();
		List<Cookie> cookies = servletRequest.getCookies();				
		for (Cookie c : cookies) {					
			if (c.getName().equals(USER_COOKIE_NAME)) {
				String uuid = c.getValue();
				generalDao.removeById(PersistentLogin.class, uuid);
			}
			if (c.getName().equals(USER_COOKIE_NAME) || c.getName().equals(REMEMBER_COOKIE_NAME)) {
				c.setMaxAge(0);
				c.setValue(null);
				servletResponse.addCookie(c);	
			} 
		}		
	}
	
	private String encodeString(String s) {
	    String encodedString = s;
	    try{
	        encodedString = URLEncoder.encode(s, "UTF-8");
	    } catch (UnsupportedEncodingException e) {}

	    return encodedString;
	}
	
	private String decodeString(String s) {
	    String decodedString = s;
	    try{
	        decodedString = URLDecoder.decode(s, "UTF-8");
	    } catch (UnsupportedEncodingException e) {}
	    return decodedString;
	}

}
