package foodcenter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ManageRestaurant implements EntryPoint
{
	private static final String GWT_CONTINER = "gwtContainer";

	private VerticalPanel mainVerticalPanel = new VerticalPanel();
	
	@Override
    public void onModuleLoad()
    {
		
		
		RootPanel.get(ManageRestaurant.GWT_CONTINER).add(mainVerticalPanel);
    }

}
