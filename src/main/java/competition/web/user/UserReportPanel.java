package competition.web.user;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.nextreports.engine.FluentReportRunner;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import competition.domain.entity.BestStagePerformer;
import competition.domain.entity.Competition;
import competition.domain.entity.User;
import competition.domain.entity.UserChampion;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.CompetitionConfiguration;
import competition.web.common.panel.AbstractImageLabelPanel;
import competition.web.common.table.BaseTable;
import competition.web.common.table.ResourceLink;
import competition.web.common.table.ResourceLinkPropertyColumn;
import competition.web.competition.AverageStatisticsPanel;
import competition.web.competition.LuckyTeamStatisticsPanel;
import competition.web.security.SecurityUtil;
import competition.web.util.ConnectionUtil;

public class UserReportPanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private BusinessService businessService;

	private static final Logger LOG = LoggerFactory.getLogger(UserReportPanel.class);

	private Competition competition;
	private String format = ReportRunner.HTML_FORMAT;
	private DataTable<User, String> table;		
	private UsersRegisteredDataProvider provider;
	private String username;
	private LuckyTeamStatisticsPanel luckyPanel;
	
	public UserReportPanel(String id, IModel<Competition> competition) {
		super(id);
		this.competition = competition.getObject();
		init();
	}

	private void init() {	
		
		username = SecurityUtil.getLoggedUsername();
				
		String title = competition.getName() + " - Rapoarte utilizatori";				
		add(new Label("fullTopLabel", title));						
		
		Search search = new Search(BestStagePerformer.class);
		search.addFilter(Filter.equal("competitionId", competition.getId()));
		List<BestStagePerformer> performers = generalService.search(search);
				
		String performerText = "NA";
		if (performers.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (BestStagePerformer bp : performers) {
				sb.append(bp.getTeam()).
				   append(" (").append(bp.getUsername()).append(") : ").
				   append(bp.getStagename()).
				   append("\n");
			}
			performerText = sb.toString();
		}		
		
		StringBuilder performerTitle = new StringBuilder("Cel mai bun punctaj pe etapa");
		if (performers.size() > 0) {
			performerTitle.append(" : ").append(performers.get(0).getPoints()).append(" puncte");
		}
		add(new Label("bestPerformerTitle", performerTitle.toString()));
		add(new MultiLineLabel("bestPerformer", performerText));
		
		List<String> formats = Arrays.asList(ReportRunner.HTML_FORMAT, ReportRunner.EXCEL_FORMAT, ReportRunner.PDF_FORMAT);
		DropDownChoice<String> formatChoice = new DropDownChoice<String>("format", new PropertyModel<String>(this, "format"), formats);		
		add(formatChoice);
		formatChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				target.add(table);				
			}
		});
		
		provider = new UsersRegisteredDataProvider(competition.getId());
		
		addTable();		
		
		add(new AverageStatisticsPanel("averageStatistics", Model.of(competition)));
		luckyPanel = new LuckyTeamStatisticsPanel("luckyStatistics", Model.of(competition), Model.of(SecurityUtil.getLoggedUsername()));
		luckyPanel.setOutputMarkupId(true);
		add(luckyPanel);
								
		ArrayList<String> names = new ArrayList<String>();
		try {
			List<User> users = businessService.getRegisteredUsers(competition.getId());
			for (User user : users) {
				names.add(user.getUsername());
			}
		} catch (Exception e) {

		}
		
		DropDownChoice<String> userChoice = new DropDownChoice<String>("users",	new PropertyModel<String>(this, "username"), names);
		userChoice.setOutputMarkupPlaceholderTag(true);				
		userChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {									
				  luckyPanel.setUser(username); 
				  target.add(luckyPanel);
			}
		});    					
		
		add(userChoice);
								
		add(new CompetitionLinkPanel("globalLink", new CompetitionReportResource()));
	}
		
	private String getCompetitionName() {
		String competitionName = "";
		if (competition != null) {
			competitionName = competition.getName();			
		} 
		return competitionName;
	}	

	class ReportResource extends ByteArrayResource {
				
		private String userName;

		public ReportResource(String contentType) {
			super(contentType);			
		}				

		@Override
		protected byte[] getData(final Attributes attributes) {
			return generateReport(userName);
		}
		
		@Override
		protected void configureResponse(ResourceResponse response, Attributes attributes) {
			response.setCacheDuration(Duration.NONE); 
		}				
				
		public void setUserName(String userName) {
			this.userName = userName;
		}	
		
		@Override		
		protected ResourceResponse newResourceResponse(final Attributes attributes) {
			ResourceResponse rr = super.newResourceResponse(attributes);
			rr.setContentType(getReportContentType());
			return rr;
		}				
						
		private byte[] generateReport(String userName) {					
			HashMap<String, Object> pValues = new HashMap<String, Object>();
			pValues.put("User", userName);
			pValues.put("Competition", getCompetitionName());
			ByteArrayOutputStream output;
			Connection connection = null;				
			Report report = CompetitionConfiguration.get().getReport();				
			try {
				output = new ByteArrayOutputStream();
				connection = ConnectionUtil.createConnection();
				FluentReportRunner.report(report).
					connectTo(connection).
					withQueryTimeout(60).
					withParameterValues(pValues).
				    //withChartImagePath("./reports").
					formatAs(format).						
					run(output);
				return output.toByteArray();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new byte[0];
			} finally {
				ConnectionUtil.closeConnection(connection);
			}
		}		

	}
		
	// cache it for one hour
	class CompetitionReportResource extends ByteArrayResource {
				
		public CompetitionReportResource() {
			super(getReportContentType());			
		}				

		@Override
		protected byte[] getData(final Attributes attributes) {
			return generateReport();
		}
		
		@Override
		protected void configureResponse(ResourceResponse response, Attributes attributes) {
			response.setCacheDuration(Duration.NONE); 
		}		
		
		@Override		
		protected ResourceResponse newResourceResponse(final Attributes attributes) {
			ResourceResponse rr = super.newResourceResponse(attributes);
			rr.setContentType(getReportContentType());
			return rr;
		}	
		
		private byte[] generateReport() {					
			HashMap<String, Object> pValues = new HashMap<String, Object>();		
			pValues.put("Competition", getCompetitionName());
			ByteArrayOutputStream output;
			Connection connection = null;				
			Report report = CompetitionConfiguration.get().getCompetitionReport();				
			try {
				output = new ByteArrayOutputStream();
				connection = ConnectionUtil.createConnection();
				FluentReportRunner.report(report).
					connectTo(connection).
					withQueryTimeout(60).
					withParameterValues(pValues).
				    //withChartImagePath("./reports").
					formatAs(format).						
					run(output);
				return output.toByteArray();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new byte[0];
			} finally {
				ConnectionUtil.closeConnection(connection);
			}
		}		

	}
	
	public class CompetitionResourceLink extends ResourceLink {
						
		public CompetitionResourceLink(final String id, final IResource resource) {
			super(id, resource);					
		}				
		
		protected void onComponentTag(ComponentTag componentTag) {					
            super.onComponentTag(componentTag);
            componentTag.put("target", "_blank");
        }	
	}
	
	public class CompetitionLinkPanel extends Panel {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public CompetitionLinkPanel(final String componentId, IResource resource) {
			super(componentId);
			
			ResourceLink link = new CompetitionResourceLink("link", resource);
			add(link);
			
			link.add(new Label("label", Model.of("Genereaza")));
		}
	}
	
	public void onSelect(AjaxRequestTarget target, Form form, User user) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }	   
    
    private void addTable() {
        List<IColumn<User, String>> columns = new ArrayList<IColumn<User, String>>();
        
        columns.add(new AbstractColumn<User, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<User>> item, String componentId,
                                     final IModel<User> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
            }
        });
        
        columns.add(new AbstractColumn<User, String>(new Model<String>("Nume")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<User>> item, String componentId,
                                     final IModel<User> rowModel) {
            	final User user = rowModel.getObject();      
            	int titles = generalService.count(new Search(UserChampion.class).addFilter(Filter.equal("username", user.getUsername())));
            	StringBuilder sb = new StringBuilder(user.getUsername()); 
            	if (titles > 0) {
            		sb.append("  ");
            		for (int i=0; i<titles; i++) {
            			sb.append("*");
            		}
            	}
                item.add(new Label(componentId, new Model<String>(sb.toString())));
            }
        });   
        
        columns.add(new AbstractColumn<User, String>(new Model<String>("Echipa")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<User>> item, String componentId,
                                     final IModel<User> rowModel) {
                final User user = rowModel.getObject();                
                //item.add(new Label(componentId, new Model<String>(user.getTeam())));   
                item.add(new AbstractImageLabelPanel(componentId, "img16") {

					@Override
					public String getDisplayString() {
						return user.getTeam();
					}

					@Override
					public String getImageName() {						
						return "img/" + user.getAvatarFile();
					}
                	
                });
            }
        });                  
        
        columns.add(new ResourceLinkPropertyColumn<User>(new Model<String>("Genereaza"), new Model<String>("Genereaza"),new ReportResource(getReportContentType())) {
        	protected void onDone(IModel<User> rowModel) {		        		
        		final User user = rowModel.getObject();          	        		
        		((ReportResource)getResource()).setUserName(user.getUsername());
        	}
        });        
                
        table = new BaseTable<User, String>("table", columns, provider, 20);   
        table.setOutputMarkupId(true);
        add(table);
    }
    
    private String getReportContentType() {
    	String contentType;
    	if (ReportRunner.PDF_FORMAT.endsWith(format)) {
			contentType = "application/pdf";
		} else if (ReportRunner.EXCEL_FORMAT.endsWith(format)) {
			contentType = "application/vnd.ms-excel";
		} else {
			contentType = "text/html";
		}
    	return contentType;
    }       

}
