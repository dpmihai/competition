package competition.web.common;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathFolderContentResource implements IResource {
	
	private static Logger LOG = LoggerFactory.getLogger(ClasspathFolderContentResource.class);
	

	public ClasspathFolderContentResource() {		
	}

	public void respond(Attributes attributes) {
		PageParameters parameters = attributes.getParameters();	
		String fileName = parameters.get("name").toString();		
		LOG.info("*** fileName=" + fileName);
		URL url = getClass().getResource(fileName);		
		LOG.info("*** URL=" + url);
		if (url !=  null) {
			File file;
			try {
				file = new File(url.toURI());
				LOG.info("*** exits="+file.exists());
				//File file = new File(getClass().getResource(fileName).getFile());
				FileResourceStream fileResourceStream = new FileResourceStream(file);
				ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
				resource.respond(attributes);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}