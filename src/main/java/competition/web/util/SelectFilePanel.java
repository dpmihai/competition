package competition.web.util;

import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.web.common.form.FormContentPanel;

public class SelectFilePanel extends FormContentPanel<Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SelectFilePanel.class);

	protected FileUploadField uploadField;	

	public SelectFilePanel() {		
		 uploadField = new FileUploadField("fileUpload");
         uploadField.setRequired(true);         
         uploadField.setLabel(new Model<String>("Selecteaza fisier"));        
         add(uploadField);
	}

}
