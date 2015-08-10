package competition.web.user.chart;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;

import competition.web.common.panel.GenericPanel;

/**
 * http://cwiki.apache.org/WICKET/open-flash-chart-and-wicket.html
 */
public class OpenFlashChart extends GenericPanel<String> implements IResourceListener {
	
private static final long serialVersionUID = 1L;
	
	private String width;
	private String height;
	private SWFObject swf;	
		
	public OpenFlashChart(String id, String width, String height, IModel<String> jsonModel) {
		super(id, jsonModel);		
		this.width = width;
		this.height = height;			
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);		
		response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(OpenFlashChart.class, "saveChartImage.js")));
	}

	@Override
	public void onResourceRequested() {		
		IResource jsonResource = createJsonResource();
		IRequestHandler requestHandler = new ResourceRequestHandler(jsonResource, null);
		requestHandler.respond(getRequestCycle());
	}
	
	@Override
	public boolean isVisible() {
		return getModelObject() != null;
	}
	
	@Override
	protected boolean getStatelessHint() {
		return false;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();		
		String swfURL = toAbsolutePath(urlFor(new PackageResourceReference(OpenFlashChart.class, "open-flash-chart.swf"), null).toString());
		
		// see http://ofc2dz.com/OFC2/downloads/ofc2Downloads.html
		// http://ofc2dz.com/OFC2/examples/MiscellaneousPatches.html (Passing the Char Parameter "ID" when saving images (23-Feb-2009))
		// for embedded charts in html pages we also put some randomness at the end (if we have charts from different dashboards id is chart1 for all)
		swfURL = swfURL.concat("?id=").concat(getMarkupId()).concat("&nocache=").concat(UUID.randomUUID().toString());;
		swf = new SWFObject(swfURL, width, height, "9.0.0");
		add(swf);
	}

	@Override
	protected void onBeforeRender() {		
		String jsonUrl = getUrlForJson();		
		swf.addParameter("data-file", jsonUrl);
        swf.addParameter("wmode", "transparent");        
        super.onBeforeRender();
	}
	
	private IResource createJsonResource() {
		
		String jsonData = getJsonData();
		IResource jsonResource = new ByteArrayResource("text/plain", jsonData.getBytes()) {

			private static final long serialVersionUID = 1L;			
						
			// These headers are needed for IE 
			//
			// Pragma & Cache-Control are needed for https (otherwise a #2032 Error will be thrown)
			// see http://dwairi.wordpress.com/2009/01/15/open-flash-chart-ie-and-ssl/
			//
			// Use no-store for Cache-Control & Expires to force IE to not cache flash (otherwise refresh actions &
			// drill-down are not working)
			// see http://www.cfcoffee.co.uk/index.cfm/2010/1/24/IE-and-XML-issue-over-SSL
			//
			@Override
			protected void setResponseHeaders(ResourceResponse data, Attributes attributes) {
				// TODO wicket 1.5
				//data.getHeaders().addHeader("Pragma", "public"); 
				//data.getHeaders().addHeader("Cache-Control", "no-store, must-revalidate");
				//data.getHeaders().addHeader("Expires", "-1");	        
				data.disableCaching();
				super.setResponseHeaders(data, attributes);
			}
			
		};
		
		return jsonResource;
	}

	private String getUrlForJson() { 
		CharSequence dataPath = urlFor(IResourceListener.INTERFACE, getPage().getPageParameters());
		try {
			dataPath = URLEncoder.encode(dataPath.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error encoding dataPath for Chart Json data file.", e);
		}						
		return toAbsolutePath(dataPath.toString());				
	}
	
	private String getJsonData() {
		return getModelObject();
	}
	
	private String toAbsolutePath(String path) {	
		HttpServletRequest req = (HttpServletRequest)((WebRequest)RequestCycle.get().getRequest()).getContainerRequest();
		return RequestUtils.toAbsolutePath(req.getRequestURL().toString(), path);		
		// server behind Apache, firewall etc
	    //return "http://localhost:8084/competition/" + path;		
	}
			
}
