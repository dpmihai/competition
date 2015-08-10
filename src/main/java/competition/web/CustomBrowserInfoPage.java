package competition.web;

import org.apache.wicket.markup.html.pages.BrowserInfoPage;

public class CustomBrowserInfoPage extends BrowserInfoPage {

	// When we use in Application getRequestCycleSettings().setGatherExtendedBrowserInfo(true);		
	// to take browser information
	//
	// Override HTML to have no message like :
	// "If you see this, it means that both javascript and meta-refresh are not support 
	// by your browser configuration. Please click this link to continue to the original destination." 
	public CustomBrowserInfoPage() {
		super();
	}
} 
