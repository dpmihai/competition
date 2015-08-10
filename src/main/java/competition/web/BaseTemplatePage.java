package competition.web;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;

public abstract class BaseTemplatePage extends BaseAppPage {

	protected HeaderPanel headerPanel;
		
	protected AdminPanel adminPanel;
	protected FooterPanel footerPanel;
	protected ModalWindow dialog;

	public BaseTemplatePage() {
		super();
		createComponents();
	}

	public BaseTemplatePage(PageParameters pageParameters) {
		super(pageParameters);
		createComponents();
	}

	public HeaderPanel getHeaderPanel() {
		return headerPanel;
	}
	
	public AdminPanel getAdminPanel() {
		return adminPanel;
	}

	public ModalWindow getDialog() {
		return dialog;
	}

	private void createComponents() {
		setStatelessHint(true);

		// add jquery.js from wiquery
		//add(new HeaderContributor(new CoreJavaScriptHeaderContributor()));

//		// add jquery
//		add(JavascriptPackageResource.getHeaderContribution("js/jquery-1.7.2.min.js"));
//		
//		// add tipTip (tool tip)
//		add(JavascriptPackageResource.getHeaderContribution("js/jquery.tipTip.minified.js"));								
//		
//		// add li-scroller
//		add(JavascriptPackageResource.getHeaderContribution("js/jquery.li-scroller.1.0.js"));
//
//		// add myagora.js
//		add(JavascriptPackageResource.getHeaderContribution("js/competition.js"));

		add(new Label("page_title", new AbstractReadOnlyModel<String>() {

			@Override
			public String getObject() {
				return getTitle();
			}
		}));

		add(new WebComponent("google-analitics") {

			@Override
			public boolean isVisible() {
				return !BaseApplication.get().isDevelopment();
			}

		});

		headerPanel = new HeaderPanel("header");
		add(headerPanel);
		
		adminPanel = new AdminPanel("admin");
		add(adminPanel);

		footerPanel = new FooterPanel("footer");
		add(footerPanel);

		dialog = new ModalWindow("dialog");
		dialog.setUseInitialHeight(false);
		add(dialog);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		// to load jquery on all pages
		response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference())); 
		// to load js from webapp we use JavaScriptContentHeaderItem.forUrl
		// for load js from src (near a java class) we use response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(class, jsFile))
		response.render(JavaScriptContentHeaderItem.forUrl("js/jquery.tipTip.minified.js"));
		response.render(JavaScriptContentHeaderItem.forUrl("js/jquery.li-scroller.1.0.js"));		
		response.render(JavaScriptContentHeaderItem.forUrl("js/competition.js"));		
	}

	public String getTitle() {
		return "Competition";
	}

}
