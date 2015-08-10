package competition.web.game;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.TitleValue;
import competition.service.BusinessService;

public class TickerPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private BusinessService businessService;

	public TickerPanel(String id, int competitionId) {
		super(id);
		
		List<TitleValue> games = businessService.getGamesTicker(competitionId); 
		ListView<TitleValue> listView = new ListView<TitleValue>("games", games) {

			@Override
			protected void populateItem(ListItem<TitleValue> item) {
				item.add(new Label("title", item.getModelObject().getTitle()));			
				item.add(new Label("game", item.getModelObject().getValue()));
			}
			
		};	
		add(listView);
	}		

}
