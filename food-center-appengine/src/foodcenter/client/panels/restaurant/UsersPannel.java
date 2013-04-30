package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.service.RequestUtils;
import foodcenter.service.UserCommonServiceProxy;
import foodcenter.service.proxies.UserProxy;

public class UsersPannel extends FlexTable
{

    
    private final Boolean isAdmin;
    private final List<String> users;
    
    private final Button newUserButton; 

    public UsersPannel(List<String> users, Boolean isAdmin)
    {
        super();
        this.isAdmin = isAdmin;
        this.users = users;
        newUserButton = new Button("New");
        newUserButton.addClickHandler(new OnClickNewUser());
        
        redraw();
        
    }

    
    public void redraw()
    {
        // Clear all the rows of this table
        removeAllRows();
        
        // Print the header row of this table
        printTableHeader();   
        
        // Print all the categories if exits
        int idx = 0;
        if (null != users)
        {
	        for (String up : users)
	        {
	            printUserRow(up, idx);
	            ++idx;
	        }
        }
    }
    
    private void printTableHeader()
    {        
        
        int row = getRowCount();
        
        newUserButton.setEnabled(isAdmin);

        // add the widgets to the table
        setText(row, 0, "user email");
        setWidget(row, 1, newUserButton);
    }

    public void printAddNewUserRow()
    {
        
        int row = getRowCount();
        
        TextBox userEmail = new TextBox();
        
        Button addButton = new Button("Add");
        addButton.addClickHandler(new OnClickAddUser(userEmail));
        
        setWidget(row, 0, userEmail);
        setWidget(row, 1, addButton);
        
    }


    public void printUserRow(String email, int idx)
    {
        int row = getRowCount();
        
        Button delete = new Button("delete");
        delete.addClickHandler(new DeleteUserClickHandler(idx));
        delete.setEnabled(isAdmin);
        
        setText(row, 0, email);
        setWidget(row, 1, delete);

    }

    class OnClickNewUser implements ClickHandler
    { 
        @Override
        public void onClick(ClickEvent event)
        {
            printAddNewUserRow();
            newUserButton.setEnabled(false);
        }
    }
    
    class OnClickAddUser implements ClickHandler
    {
        private final TextBox emailTextBox;
        
        
        public OnClickAddUser(TextBox emailTextBox)
        {
            this.emailTextBox  = emailTextBox;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            UserCommonServiceProxy service = RequestUtils.getRequestFactory().getUserCommonService(); 
            service.getDbUser(emailTextBox.getText()).fire(new AddUserReceiver());
        }
    }

    class AddUserReceiver extends Receiver<UserProxy>
    {

        @Override
        public void onSuccess(UserProxy response)
        {
            if (null != response)
            {
                users.add(response.getEmail());
                redraw();
            }
            else
            {
                Window.alert("email doesn't exists");
            }
            
        }
        
        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert(error.getMessage());
        }
    }
    
    
    class DeleteUserClickHandler implements ClickHandler
    {

        private final int idx;

        public DeleteUserClickHandler(int idx)
        {
            this.idx = idx;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            users.remove(idx);
            redraw();
        }
    }
}