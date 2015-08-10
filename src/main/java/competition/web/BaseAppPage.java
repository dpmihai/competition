package competition.web;

import java.util.Locale;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAppPage extends WebPage {

	protected static Logger logger = LoggerFactory.getLogger(BaseAppPage.class);

	public BaseAppPage() {
		super();
	}

	public BaseAppPage(PageParameters pageParameters) {
		super(pageParameters);
	}

	public BaseAppSession getAppSession() {
		return BaseAppSession.get();
	}

	public String getClientIp() {
		return getWebClientInfo().getRemoteAddress();
	}

	protected ClientProperties getWebClientInfo() {
		return getAppSession().getClientInfo().getProperties();		
	}
	
	@Override
	public Locale getLocale() {	
	    return new Locale("ro", "RO");
	}


}
