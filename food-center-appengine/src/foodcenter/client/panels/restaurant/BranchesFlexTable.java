package foodcenter.client.panels.restaurant;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.client.panels.RestaurantBranchPanel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class BranchesFlexTable extends FlexTable
{

	private final RequestContext requestContext;
	private final Boolean isAdmin;
	private final RestaurantProxy rest;
	private List<RestaurantBranchProxy> branches;

	public BranchesFlexTable(RequestContext requestContext, RestaurantProxy rest, List<RestaurantBranchProxy> branches, Boolean isAdmin)
	{
		super();
		this.requestContext = requestContext;
		this.rest = rest;

		this.isAdmin = isAdmin;
		this.branches = branches;
		redraw();
	}

	public final void redraw()
	{
		// Clear all the rows of this table
		removeAllRows();

		// Print the header row of this table
		printTableHeader();

		// Print all the branches if exits
		if (null == branches)
		{
			return;
		}
		
		int row = getRowCount();
		for (RestaurantBranchProxy rbp : branches)
		{
			printRestaurntBranchTableRow(rbp, row);
			row++;
		}

	}

	/**
	 * Prints (or overrides) the 1st row of the table
	 * [0] = "categories", [1] = button("add category")
	 */
	private void printTableHeader()
	{
		// set column 0
		setText(0, 0, "Address");

		// set column 1
		Button addBranchButton = new Button("Add Branch");
		addBranchButton.addClickHandler(new AddBranchClickHandler());
		addBranchButton.setEnabled(isAdmin);
		setWidget(0, 1, addBranchButton);
	}

	/**
	 * print the branch to the table row
	 * 
	 * @param row is the row to set
	 * @param branch is the category to print as row
	 */
	private void printRestaurntBranchTableRow(RestaurantBranchProxy branch, int row)
	{

		setText(row, 0, branch.getAddress());

		Button editBranchButton = new Button("edit");
		editBranchButton.addClickHandler(new EditRestaurantBranchClickHandler(branch, row));
		editBranchButton.setEnabled(isAdmin);
		setWidget(row, 1, editBranchButton);

		Button deleteBranchButton = new Button("X");
		deleteBranchButton.addClickHandler(new DeleteRestaurantBranchClickHandler(branch));
		deleteBranchButton.setEnabled(isAdmin);
		setWidget(row, 2, deleteBranchButton);

	}

	/**
	 * Handles add category button click
	 */
	private class AddBranchClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent event)
		{

			// construct a new branch, it will be edited by the rest branch panel
			RestaurantBranchProxy branch = RequestUtils.createRestaurantBranchProxy(requestContext);

			// get the next row of the table
			int row = getRowCount();

			// construct a popup to show the rest branch panel
			PopupPanel popup = new PopupPanel(false); // dont close on outside click

			// construct on close runnable
			OnEditBranchPopupClose onClose = new OnEditBranchPopupClose(branch, popup, true, row);

			// construct the panel and add it to the popup
			RestaurantBranchPanel branchPanel = new RestaurantBranchPanel(requestContext, branch, isAdmin, onClose);
			popup.add(branchPanel);
			popup.setTitle("Add Branch");
			popup.setPopupPosition(10, 80);

			// show the new popup content
			popup.show();

		}
	}

	private class EditRestaurantBranchClickHandler implements ClickHandler
	{
		private final RestaurantBranchProxy branch;
		private final int row;

		/**
		 * @param index - is the index on the list to delete
		 */
		public EditRestaurantBranchClickHandler(RestaurantBranchProxy branch, int row)
		{
			this.branch = branch;
			this.row = row;
		}

		@Override
		public void onClick(ClickEvent event)
		{
			PopupPanel popup = new PopupPanel(false); // dont close on outside click
			OnEditBranchPopupClose onClose = new OnEditBranchPopupClose(branch, popup, false, row);
			RestaurantBranchPanel branchPanel = new RestaurantBranchPanel(requestContext, branch, isAdmin, onClose);

			popup.add(branchPanel);
			popup.setTitle("Add Branch");
			popup.setPopupPosition(10, 80);
			popup.show();
			return;
		}
	}

	/**
	 * Handles delete category button click
	 */
	private class DeleteRestaurantBranchClickHandler implements ClickHandler
	{
		private final RestaurantBranchProxy branch;

		/**
		 * @param index - is the index on the list to delete
		 */
		public DeleteRestaurantBranchClickHandler(RestaurantBranchProxy branch)
		{
			this.branch = branch;
		}

		@Override
		public void onClick(ClickEvent event)
		{
			branches.remove(branch);
			redraw();
		}
	}

	private class OnEditBranchPopupClose implements Runnable
	{
		private final RestaurantBranchProxy branch;
		private final PopupPanel popup;
		private final boolean isNew;
		private int row;

		public OnEditBranchPopupClose(RestaurantBranchProxy branch, PopupPanel popup, boolean isNew, int row)
		{
			this.branch = branch;
			this.popup = popup;
			this.isNew = isNew;
			this.row = row;
		}

		@Override
		public void run()
		{
			popup.hide();
			if (isNew)
			{
				branches.add(branch);
//				branch.setRestaurant(rest);
			}
			printRestaurntBranchTableRow(branch, row);

		}

	}
}
