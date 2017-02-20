package competition.web.util;

import org.apache.wicket.Session;
import org.apache.wicket.core.request.ClientInfo;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.request.WebClientInfo;

public class WicketUtil {
	
	public static boolean isSmallScreen() {
		boolean smallScreen = false;
		int width = -1;
		ClientInfo info = Session.get().getClientInfo();
		if (info instanceof WebClientInfo) {
		   	ClientProperties cp = ((WebClientInfo) info).getProperties();
		   	width = cp.getScreenWidth();
		}
			
		// tablet & phone
		if ((width > 0) && (width <= 800)) {
			smallScreen = true;
		}
		return smallScreen;
	}

}
