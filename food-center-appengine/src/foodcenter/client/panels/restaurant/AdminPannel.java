package foodcenter.client.panels.restaurant;
	
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.client.panels.PanelUtils;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.proxies.RestaurantProxy;


public class AdminPannel  extends HorizontalPanel
{

	private final RequestContext requestContext;
	private final UserProxy admin;
	private final Boolean isAdmin;
	private final RestaurantProxy rest;
    private Panel createNamePanel()
    {
         HorizontalPanel res = new HorizontalPanel();
         TextBox nameBox = new TextBox();
         PanelUtils.setNotNullText(nameBox, admin.getUsername());
         nameBox.addKeyUpHandler(new AdminNameKeyUpHandler(nameBox, admin));
         res.add(nameBox);
         return res;
     }
	public AdminPannel(RequestContext requestContext, UserProxy admin,RestaurantProxy rest, Boolean isAdmin)
	{
		super();
        this.requestContext = requestContext;
        this.admin = admin;
        this.isAdmin = isAdmin;
        this.rest = rest;
       
        createHeader();
        List<UserProxy> admins = rest.getAdmins();
        if (null == admins)
        {
        	admins = new LinkedList<UserProxy>();
        }

        for (UserProxy ap : admins)
        {
        	printAdmineRow(ap);
        }
	}

    private void createHeader()
    {
    	HorizontalPanel res = new HorizontalPanel();
        Button addAdminButton = new Button("Add");
        addAdminButton.addClickHandler(new AddAdminHandler());
        this.setCellHorizontalAlignment(addAdminButton, ALIGN_RIGHT);
        res.add(createNamePanel());
    }

    public void addAdmin()
    {
        UserProxy AdminProxy = requestContext.create(UserProxy.class);
        rest.getAdmins().add(AdminProxy);
        printAdmineRow(AdminProxy);
    }

    public void deleteAdmin(int row)
    {
        List<UserProxy> admins= rest.getAdmins();
        admins.remove(row - 1);
        remove(row);
    }

    public void printAdmineRow(UserProxy adminProxy)
    {
        int row = getWidgetCount();

        TextBox name = new TextBox();
        name.addKeyUpHandler(new AdminNameKeyUpHandler(name, adminProxy));
        Button delete = new Button("delete");
        delete.addClickHandler(new DeleteAdminClickHandler(row));
        delete.setEnabled(isAdmin);

    }

   	class AddAdminHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            addAdmin();
        }
    }

    class DeleteAdminClickHandler implements ClickHandler
    {

        private final int row;

        public DeleteAdminClickHandler(int row)
        {
            this.row = row;
        }

        @Override
        public void onClick(ClickEvent event)
        {
        	deleteAdmin(row);
        }
    }
    class AdminNameKeyUpHandler implements KeyUpHandler
    {
        private final TextBox nameBox;
        private final UserProxy admin;
       
        public AdminNameKeyUpHandler(TextBox nameBox, UserProxy admin)
        {
            this.nameBox = nameBox;
            this.admin = admin;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            admin.stableId();
        }
    }
}