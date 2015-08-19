package competition.web.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.Team;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.table.BaseTable;
import competition.web.common.table.LinkPropertyColumn;
import competition.web.competition.AverageStatisticsPanel;
import competition.web.competition.CompetitionChoiceRenderer;
import competition.web.stage.StageChoiceRenderer;
import competition.web.util.DateUtil;


public class GamesPanel extends Panel {

	private Competition competition;
	private Stage stage;
	private DataTable<Game, String> table;
	private GamesDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;	
	private DropDownChoice<Stage> stageChoice;	
	
	@SpringBean
	private GeneralService generalService;		
	
	@SpringBean
	private BusinessService businessService;		

	public GamesPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {
		
        add(new Label("title", "Administrare Meciuri"));                   	
       	
       	List<Competition> competitions = generalService.search(
       			new Search(Competition.class).
       			addFilterEqual("active", true).
       			addSort(new Sort("name", true)));
       	if (competitions.size() > 0) {
       		competition = competitions.get(0);
       	}
		DropDownChoice<Competition> competitionChoice = new DropDownChoice<Competition>("competition", 
				new PropertyModel<Competition>(this, "competition"), competitions, new CompetitionChoiceRenderer());
		competitionChoice.setOutputMarkupPlaceholderTag(true);
		add(competitionChoice);
		
		final StagesModel stagesModel = new StagesModel();
		stage = stagesModel.getCurrentStage();
		
		competitionChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				provider.setStageId(-1);		
				stagesModel.reset();
				stage = stagesModel.getCurrentStage();
				provider.setStageId(getStageId());
				target.add(stageChoice);
				target.add(table);		
				target.add(addLink);
			}
		});    
						
		stageChoice = new DropDownChoice<Stage>("stage", new PropertyModel<Stage>(this, "stage"), stagesModel, new StageChoiceRenderer());		
		stageChoice.setOutputMarkupPlaceholderTag(true);
		add(stageChoice);
		stageChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				provider.setStageId(getStageId());
				target.add(table);		
				target.add(addLink);
			}
		});    
        
        dialog = new ModalWindow("dialog");
        add(dialog);

        add(addLink = new AjaxLink("addGame") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga meci nou", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
	}
	
	private void addTable() {
        List<IColumn<Game, String>> columns = new ArrayList<IColumn<Game, String>>();
        
        columns.add(new AbstractColumn<Game, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col hideForPhone";
            }

            public void populateItem(Item<ICellPopulator<Game>> item, String componentId,
                                     final IModel<Game> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });
        
        columns.add(new AbstractColumn<Game, String>(new Model<String>("Gazde")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Game>> item, String componentId,
                                     final IModel<Game> rowModel) {
                final Game game = rowModel.getObject();     
                String hostsName = ((Team)generalService.find(Team.class, game.getHostsId())).getName();
                item.add(new Label(componentId, new Model<String>(hostsName)));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<Game, String>(new Model<String>("Oaspeti")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Game>> item, String componentId,
                                     final IModel<Game> rowModel) {
                final Game game = rowModel.getObject();     
                String guestsName = ((Team)generalService.find(Team.class, game.getGuestsId())).getName();
                item.add(new Label(componentId, new Model<String>(guestsName)));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<Game, String>(new Model<String>("Scor gazde")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Game>> item, String componentId,
                                     final IModel<Game> rowModel) {
                final Game game = rowModel.getObject();          
                Integer hostsScore = game.getHostsScore();
                String model = hostsScore == null ? "-" : String.valueOf(hostsScore);
                item.add(new Label(componentId, new Model<String>(model)));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<Game, String>(new Model<String>("Scor oaspeti")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Game>> item, String componentId,
                                     final IModel<Game> rowModel) {
                final Game game = rowModel.getObject();              
                Integer guestsScore = game.getGuestsScore();
                String model = guestsScore == null ? "-" : String.valueOf(guestsScore);
                item.add(new Label(componentId, new Model<String>(model)));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<Game, String>(new Model<String>("Data")) {

            @Override
            public String getCssClass() {
                return "name-col hideForPhone";
            }

            public void populateItem(Item<ICellPopulator<Game>> item, String componentId,
                                     final IModel<Game> rowModel) {
                final Game game = rowModel.getObject();                                
                item.add(DateLabel.forDatePattern(componentId, new Model<Date>(game.getFixtureDate()), "dd/MM/yyyy"));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new LinkPropertyColumn<Game>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Game game = (Game) model.getObject();																																
				addEdit("Editeaza meci", target, game);     
			}
		});
        
        columns.add(new LinkPropertyColumn<Game>(new Model<String>("Sterge"), new Model<String>("Sterge"),  new Model<String>("Stergeti meciul?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Game game = (Game) model.getObject();																																
				generalService.remove(game);
                target.add(table);     
			}
		});
        
        provider =  new GamesDataProvider(getStageId());
        table = new BaseTable<Game, String>("table", columns, provider, 300);
        table.setOutputMarkupId(true);
        add(table);
    }
	
	private int getStageId() {
		int stageId = -1;
		if (stage != null) {
			stageId = stage.getId();
			addLink.setEnabled(true);
		} else {
			addLink.setEnabled(false);
		}
		return stageId;
	}
	
	private void addEdit(String title, AjaxRequestTarget target, Game game) {
		dialog.setTitle(title);                
        dialog.setAutoSize(true);               
        dialog.setContent(new AddEditGamePanel(dialog.getContentId(), stage, game) {

            @Override
            public void onAddGame(AjaxRequestTarget target, Form form, Game game) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    game = generalService.merge(game);
                    businessService.computeAndSetUserScore(game);
                    // reset average statistics to be computed again
                    AverageStatisticsPanel.resetAverageStatistics(competition.getName());
                    target.add(table);                    
                } catch (Exception e) {                            
                    e.printStackTrace();
                    form.error(e.getMessage());
                }
            }

            @Override
            public void onCancel(AjaxRequestTarget target) {
                ModalWindow.closeCurrent(target);
            }

        });
        dialog.show(target);
	}
	
    class StagesModel extends LoadableDetachableModel<List<Stage>> {
    	
		private static final long serialVersionUID = 1L;
		private List<Stage> stages = new ArrayList<Stage>();
		
		@SuppressWarnings("unchecked")
		@Override
		protected List<Stage> load() {
			return getStages();
		}
		
		private List<Stage> getStages() {
			try {
				if (competition == null) {
					return new ArrayList<Stage>();
				} else {
					if (stages.isEmpty()) {
						stages = generalService.search(new Search(Stage.class).
								addFilter(Filter.equal("competitionId", competition.getId())).
								addSort(new Sort("id", false)));
					}
					return stages;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Stage>();
			}
		}
		
		public Stage getCurrentStage() {			
			Date current = new Date();
			List<Stage> stages = getStages();
			int size = stages.size();
			int pos = -1;
			if (size > 0) {
				pos = size;
			}
			for (int i = 0; i<size; i++) {
				Stage stage = stages.get(i);
				if ( DateUtil.compare(stage.getFixtureDate(), current) > 0) {
					pos = i;
					break;
				}
			}
			return (pos > 0) ? stages.get(pos-1) : (pos == -1) ? null : stages.get(0); 
		}
		
		public void reset() {
			stages.clear();
		}

    }

}
