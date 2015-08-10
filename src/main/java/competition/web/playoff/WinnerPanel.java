package competition.web.playoff;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.service.GeneralService;

public class WinnerPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService service;
	
	@SpringBean
	private BusinessService businessService;
	
	public WinnerPanel(String id, IModel<String> winner) {
		super(id, winner);

		add(new ContextImage("trophy", "img/playoff_cup.png"));
		
		User user = service.find(User.class, winner.getObject());
							
		add(new ContextImage("winnerAvatar", "img/" + user.getAvatarFile()));
		add(new Label("winner", user.getTeam() + " (" + winner.getObject() + ")"));
		
	}


}
