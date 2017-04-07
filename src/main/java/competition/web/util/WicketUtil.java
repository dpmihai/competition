package competition.web.util;

import java.util.Date;

import org.apache.wicket.Session;
import org.apache.wicket.core.request.ClientInfo;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
	
	public static DateLabel getDateLabel(String componentId, IModel<Date> model, String datePattern) {
		
		// this has issues with timezone
		//return DateLabel.forDatePattern(componentId, model, datePattern);
		
		return new DateLabel(componentId, model, new PatternDateConverter(datePattern, false));
	}

}
