package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import foodcenter.client.handlers.RedrawablePannel;

public class UsersPannel extends FlexTable implements RedrawablePannel
{

    private static final int COLUMN_EMAIL = 0;
    private static final int COLUMN_NEW_BUTTON = 1;
    private static final int COLUMN_ADD_BUTTON = 1;
    private static final int COLUMN_CANCEL_BUTTON = 2;

    private final List<String> users;

    private final boolean isEditMode;

    private final Button newUserButton;

    public UsersPannel(List<String> users,
                       boolean isEditMode)
    {
        super();
        this.users = users;
        this.isEditMode = isEditMode;

        // Adds an empty row to fill
        newUserButton = new Button("New");
        OnClickNewButton onClickNewButton = new OnClickNewButton();
        newUserButton.addClickHandler(onClickNewButton);
        redraw();
    }

    @Override
    public void redraw()
    {
        // Clear all the rows of this table
        removeAllRows();

        // Print the header row of this table
        printTableHeader();

        if (null == users)
        {
            return;
        }

        // Print all the categories if exits
        int row = 1;
        for (String user : users)
        {
            printUserRow(user, row);
            ++row;
        }
    }

    private void printTableHeader()
    {
        int row = getRowCount();

        // add the widgets to the table
        setText(row, COLUMN_EMAIL, "user email");

        if (isEditMode)
        {
            // This button starts a new row to type in
            setWidget(row, COLUMN_NEW_BUTTON, newUserButton);
        }
    }

    public void printNewUserEmptyRow()
    {

        int row = getRowCount();

        // Email text box
        TextBox userEmail = new TextBox();
        setWidget(row, COLUMN_EMAIL, userEmail);

        // Click on it tries to add the user...
        Button addButton = new Button("Add");
        addButton.addClickHandler(new OnClickAddButton(userEmail));
        setWidget(row, COLUMN_ADD_BUTTON, addButton);

        // Click on it will cancel the raw and redraw!
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new OnClickCancelButton());
        setWidget(row, COLUMN_CANCEL_BUTTON, cancelButton);

    }

    public void printUserRow(String email, int row)
    {
        setText(row, COLUMN_EMAIL, email);

        if (isEditMode)
        {
            Button delete = new Button("Delete");
            delete.addClickHandler(new OnClickDeleteButton(row));            
            setWidget(row, COLUMN_ADD_BUTTON, delete);
        }
    }


    /* ********************************************************************* */
    /* ********************** private classes ****************************** */
    /* ********************************************************************* */

    private class OnClickCancelButton implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            redraw();
        }
    }

    private class OnClickNewButton implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            printNewUserEmptyRow();
            newUserButton.setEnabled(false);
        }
    }

    private class OnClickAddButton implements ClickHandler
    {
        private final TextBox text;

        public OnClickAddButton(TextBox text)
        {
            this.text = text;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            String email = text.getText();
            users.add(email);
            redraw();
        }

    }

    private class OnClickDeleteButton implements ClickHandler
    {
        private final int row;

        public OnClickDeleteButton(int row)
        {
            this.row = row;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            String email = getText(row, COLUMN_EMAIL);
            if (users.contains(email))
            {
                users.remove(email);
                redraw();
            }
            else
            {
                Window.alert("User doesn't exists: " + email);
            }
        }
    }
}