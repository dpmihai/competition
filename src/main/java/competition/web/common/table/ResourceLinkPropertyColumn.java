package competition.web.common.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;

public class ResourceLinkPropertyColumn<T> extends PropertyColumn<T, String> {

	private static final long serialVersionUID = 1L;
	private IModel labelModel;
	private ByteArrayResource resource;

	@SuppressWarnings("unchecked")
	public ResourceLinkPropertyColumn(IModel displayModel, IModel labelModel, ByteArrayResource resource) {
		super(displayModel, null);
		this.labelModel = labelModel;
		this.resource = resource;
	}

	@SuppressWarnings("unchecked")
	public ResourceLinkPropertyColumn(IModel displayModel, String sortProperty, String propertyExpression,  ByteArrayResource resource) {
		super(displayModel, sortProperty, propertyExpression);
		this.resource = resource;
	}

	@SuppressWarnings("unchecked")
	public ResourceLinkPropertyColumn(IModel displayModel, String propertyExpressions,   ByteArrayResource resource) {
		super(displayModel, propertyExpressions);
		this.resource = resource;
	}

	@Override
	public void populateItem(Item item, String componentId, IModel model) {
		item.add(new LinkPanel(item, componentId, model));
	}
	
	protected void onDone(IModel<T> rowModel) {		
	}
	

	public ByteArrayResource getResource() {
		return resource;
	}
	
	public class LinkPanel extends Panel {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public LinkPanel(final Item item, final String componentId, final IModel model) {
			super(componentId);
			
			ResourceLink link = new DynamicResourceLink("link", resource, model);
			add(link);

			IModel tmpLabelModel = labelModel;
			if (labelModel == null) {
				tmpLabelModel = getDataModel(model);
			}

			link.add(new Label("label", tmpLabelModel));
		}
	}
	
	public class DynamicResourceLink extends ResourceLink {
		
		private IModel model;
		
		public DynamicResourceLink(final String id, final IResource resource, final IModel model) {
			super(id, resource);
			this.model = model;			
		}
		
		@Override
		public void beforeResourceRequested() {
			onDone(model);
		}
		
		protected void onComponentTag(ComponentTag componentTag) {					
            super.onComponentTag(componentTag);
            componentTag.put("target", "_blank");
        }	
	}
}