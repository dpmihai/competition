package competition.web.competition;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import competition.domain.entity.Competition;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.common.panel.AbstractImageLinkPanel;
import competition.web.common.panel.GenericPanel;
import competition.web.user.TopUsersPanel;

public class CompetitionsPanel extends GenericPanel<Competition> {

	private static final long serialVersionUID = 1L;
	private WebMarkupContainer container;	

	public CompetitionsPanel(String id) {
		super(id);		
		
		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		container.add(createDataView( new CompetitionsDataProviderMain()));
	}

	private DataView<Competition> createDataView(IDataProvider<Competition> dataProvider) {

		DataView<Competition> dataView = new DataView<Competition>("competitions", dataProvider) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Competition> item) {

				AbstractImageLinkPanel<Competition, FullCompetitionPage> panel = new AbstractImageLinkPanel<Competition, FullCompetitionPage>(
						"competition", FullCompetitionPage.class, new Model<Competition>(item.getModelObject())) {

					private static final long serialVersionUID = 1L;										

					@Override
					public String getImageName() {
						return "img/" + getModelObject().getImageFile();
					}

					@Override
					public PageParameters createPageParameters() {
						PageParameters pp = new PageParameters();
						pp.add("competitionId", getModelObject().getId());
						pp.add("top", Boolean.FALSE);
						return pp;
					}
					
					@Override
					protected void addComponents() {
						add(new SimpleTooltipBehavior(getModelObject().getName()));						
					}
				};				

				panel.get("link:image").add(AttributeModifier.replace("class", "competition-image"));
				item.add(panel);
				
				item.add(new TopUsersPanel("top", item.getModelObject(), 3));
			}

		};
		
		dataView.setOutputMarkupId(true);

		return dataView;
	}
	
	public void setProvider(AjaxRequestTarget target, IDataProvider<Competition> dataProvider) {
		DataView<Competition> dataView = createDataView( dataProvider );
		container.replace(dataView);
		target.add(container);
	}
	
	@Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);        
        if (event.getPayload() instanceof SearchCompetitionEvent) {        	
        	SearchCompetitionEvent search = (SearchCompetitionEvent)event.getPayload();
            setProvider(search.getTarget(),  new ListDataProvider<Competition>(search.getCompetitions()));
        }
    }

}
