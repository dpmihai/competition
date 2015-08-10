package competition.web;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import competition.domain.entity.MailData;
import competition.job.MailNotifierJob;
import competition.service.BusinessService;
import competition.service.GeneralService;

public class NotifierServicePage extends WebPage {
	
	@SpringBean
	private BusinessService businessService;
	
	@SpringBean
	private GeneralService generalService;
 
    public NotifierServicePage(PageParameters pageParameters) {
    	
    	String json = createJson();    	   
//    	System.out.println("*** json="+json);
    	
//    	ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.ALL, Visibility.ANY);
//    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    	try {
//    		System.out.println("----------  read");
//			List<MailData> list = mapper.readValue(json, new TypeReference<List<MailData>>(){});			
//			System.out.println(list);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
    	
    	TextRequestHandler textRequestHandler = new TextRequestHandler("application/json", "UTF-8", json); 
        getRequestCycle().scheduleRequestHandlerAfterCurrent(textRequestHandler);
    }
    
    public String createJson() {
    	MailNotifierJob job = new MailNotifierJob();
    	job.setBusinessService(businessService);
    	job.setGeneralService(generalService);
    	List<MailData> mailData = job.createMails();
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("[");
    	for (int i=0, size=mailData.size(); i<size; i++) {
    		MailData md = mailData.get(i);
    		sb.append(md.toJson());
    		if (i < size-1) {
    			sb.append(",");
    		}
    	}
    	sb.append("]");
    	return sb.toString();
    }
    
   
}