package competition.web.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Search;

import competition.domain.entity.Competition;
import competition.domain.entity.Question;
import competition.domain.entity.User;
import competition.domain.entity.UserResponse;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.panel.AbstractImagePanel;
import competition.web.common.table.BaseTable;
import competition.web.common.table.LinkPropertyColumn;
import competition.web.security.SecurityUtil;
import competition.web.util.DateUtil;

public class ViewUserQuestionPanel extends Panel {
		
	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private BusinessService businessService;
	
	private ModalWindow dialog;	
	private Competition competition;
	private UserResponseDataProvider provider;
	private DataTable<UserResponse, String> table;		
	private String username;
	private WebMarkupContainer tableContainer;
	
	private String quizPoints = "";
	
	public ViewUserQuestionPanel(String id, Competition competition, String username) {
		super(id);
		this.competition = competition;
		this.username = username;
		initResponses(username);
		init();
	}
	
	private void init() {	
		
		Form<String> form = new Form<String>("userForm");
		
		dialog = new ModalWindow("dialog");
	    form.add(dialog);
	    
	    tableContainer = new WebMarkupContainer("tableContainer");
	    tableContainer.setOutputMarkupId(true);										
		
		ArrayList<String> names = new ArrayList<String>();
		try {
			List<User> users = businessService.getRegisteredUsers(competition.getId());
			for (User user : users) {
				names.add(user.getUsername());
			}
		} catch (Exception e) {

		}
		
		int points = businessService.getUserQuizPoints(competition.getId(), username);
		quizPoints = "Puncte: " + points;
		final Label bonus = new Label("quizPoints", new PropertyModel(this, "quizPoints"));
		bonus.setOutputMarkupId(true);
		form.add(bonus);
	    			    			        
        DropDownChoice<String> userChoice = new DropDownChoice<String>("users",	new PropertyModel<String>(this, "username"), names) {
			@Override
			public boolean isVisible() {
				User user = SecurityUtil.getLoggedUser();
				if (user == null) {
					return false;
				}	
				return user.isAdmin();					
			}					
		};
		userChoice.setOutputMarkupPlaceholderTag(true);				
		userChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {					
				initResponses(username);
				provider =  new UserResponseDataProvider(competition.getId(), username);
				table = new BaseTable<UserResponse, String>("table", getColumns(), provider, 300);    
				int points = businessService.getUserQuizPoints(competition.getId(), username);	
				quizPoints = "Puncte: " + points;		
				tableContainer.replace(table);
		        target.add(tableContainer);
		        target.add(bonus);
			}
		});    
		
		
		form.add(userChoice);
		
		
		
		add(form);
								
		provider =  new UserResponseDataProvider(competition.getId(), username);
        table = new BaseTable<UserResponse, String>("table", getColumns(), provider, 300);                
        tableContainer.add(table);        
        form.add(tableContainer);
	}
	
	private List<IColumn<UserResponse, String>> getColumns() {
        List<IColumn<UserResponse, String>> columns = new ArrayList<IColumn<UserResponse, String>>();
        
        columns.add(new AbstractColumn<UserResponse, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserResponse>> item, String componentId,
                                     final IModel<UserResponse> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });
        
        columns.add(new AbstractColumn<UserResponse, String>(new Model<String>("Intrebare")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserResponse>> item, String componentId,
                                     final IModel<UserResponse> rowModel) {
                final UserResponse response = rowModel.getObject();       
                Question question = generalService.find(Question.class, response.getQuestionId());
                item.add(new Label(componentId, new Model<String>(question.getQuestion())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<UserResponse, String>(new Model<String>("Oficial")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserResponse>> item, String componentId,
                                     final IModel<UserResponse> rowModel) {
                final UserResponse response = rowModel.getObject();       
                Question question = generalService.find(Question.class, response.getQuestionId());
                item.add(new Label(componentId, new Model<String>(question.getResponse())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<UserResponse, String>(new Model<String>("Personal")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserResponse>> item, String componentId,
                                     final IModel<UserResponse> rowModel) {
                final UserResponse response = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(response.getResponse())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });                   
        
        columns.add(new AbstractColumn<UserResponse, String>(new Model<String>("Ok")) {

            @Override
            public String getCssClass() {
                return "boolean";
            }

            public void populateItem(Item<ICellPopulator<UserResponse>> item, String componentId,
                                     final IModel<UserResponse> rowModel) {
                final UserResponse response = rowModel.getObject();     
                final Question question = generalService.find(Question.class, response.getQuestionId());
                item.add(new AbstractImagePanel(componentId) {

                    @Override
                    public String getImageName() {
                        if ((question.getResponse() != null) && (question.getResponse().equalsIgnoreCase(response.getResponse()))) {                	             	
                            return "img/tick.png";
                        } else {
                            return "img/delete.gif";
                        }
                    }

                });
            }                       
        });   
        
        Date quizDate = competition.getQuizDate();        
        if (quizDate != null) {
			int compare = DateUtil.compare(new Date(),quizDate);
			if ((compare <= 0) || SecurityUtil.getLoggedUser().isAdmin()) {
				columns.add(new LinkPropertyColumn<UserResponse>(new Model<String>(""), new Model<String>("Raspunde")) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {
						UserResponse response = (UserResponse) model.getObject();
						edit("Editeaza raspuns", target, response);
					}
				});
			}
		}

        return columns;
    }
	
	private void edit(String title, AjaxRequestTarget target, UserResponse response) {
		dialog.setTitle(title);                
        dialog.setAutoSize(true);        
        dialog.setContent(new EditUserResponsePanel(dialog.getContentId(), response) {

            @Override
            public void onEditUserResponse(AjaxRequestTarget target, Form form, UserResponse response) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    generalService.merge(response);
                    target.add(tableContainer);
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
	
	private void initResponses(String username) {
		try {
			Search searchQ = new Search(Question.class);
			searchQ.addFilterEqual("competitionId", competition.getId());		
			List<Question> questions = generalService.search(searchQ);
			
			Search search = new Search(UserResponse.class);
			search.addFilterEqual("competitionId", competition.getId());
			search.addFilterEqual("username", username);
			List<UserResponse> responses = generalService.search(search);
									
			// first entrance: must create responses from existing questions			
			if (questions.size() > responses.size()) {			
				for (Question question : questions) {
					UserResponse r = findUserResponse(responses, question.getId());
					if (r == null) {
						r = new UserResponse();
						r.setCompetitionId(competition.getId());
						r.setQuestionId(question.getId());
						r.setUsername(username);
						generalService.merge(r);
						responses.add(r);
					}
				}
			}						
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private UserResponse findUserResponse(List<UserResponse> responses, int questionId) {
		for (UserResponse response : responses) {
			if (response.getQuestionId().intValue() == questionId) {
				return response;
			}
		}
		return null;
	}

}
