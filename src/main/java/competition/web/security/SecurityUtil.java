package competition.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import competition.domain.entity.User;

public class SecurityUtil {

	public static User getLoggedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}

		return (User) authentication.getPrincipal();
	}

	public static String getLoggedUsername() {
		User user = getLoggedUser();
		return (user != null) ? user.getUsername() : null;
	}

}
