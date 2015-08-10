package competition.web.rss;

import java.util.Date;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.jsoup.Jsoup;

import com.sun.syndication.feed.synd.SyndEntry;

import competition.domain.entity.Competition;

/**
 * General purpose RSS Feed Panel.  Provide the RSS Feed URL and the max
 * number of items from the feed to display.
 *
 */
public class RssFeedPanel extends Panel {
	private static final long serialVersionUID = 1L;		

	public RssFeedPanel (String wicketId, Competition competition, int maxItems) {
		super(wicketId);
				
		String title = competition.getName() + " - Stiri";	
		add(new Label("fullLabel", title));
		
		List<? extends SyndEntry> entryList = (List<? extends SyndEntry>)new SyndEntryListModel(competition.getRss()).getObject();
		entryList = entryList.subList(0, Math.min(entryList.size(), maxItems));
		add(new ListView<SyndEntry> ("item", entryList) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<SyndEntry> item) {
				SyndEntry entry = item.getModelObject();
				ExternalLink link = new ExternalLink("link", entry.getLink());
				item.add(link);
				link.add(new Label("title", entry.getTitle()));
				item.add(DateLabel.forDatePattern("date", new PropertyModel<Date>(entry, "publishedDate"), "dd/MM/yyyy"));	
				item.add(new Label("description", Jsoup.parse(entry.getDescription().getValue()).text()));
			}
			
		});						
		
	}
}