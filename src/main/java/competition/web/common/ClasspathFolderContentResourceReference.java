package competition.web.common;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class ClasspathFolderContentResourceReference extends ResourceReference {
	
	public ClasspathFolderContentResourceReference() {	   
	    super(ClasspathFolderContentResourceReference.class, "folderRef");
	}

	@Override
	public IResource getResource() {
	    return new ClasspathFolderContentResource();
	}

}
