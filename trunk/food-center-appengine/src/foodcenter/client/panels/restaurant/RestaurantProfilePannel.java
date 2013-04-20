package foodcenter.client.panels.restaurant;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.ClientUtils;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantProfilePannel extends HorizontalPanel
{

    private RestaurantProxy rest;
    private Boolean isAdmin;

    public RestaurantProfilePannel(RestaurantProxy rest, Boolean isAdmin)
    {
        super();
        this.isAdmin = isAdmin;
        this.rest = rest;

        redraw();
    }

    public final void redraw()
    {
        // remove all the widgets in this panel
        clear();

        // Add the image
        Image image = RequestUtils.getImage(rest.getIconBytes()); // TODO get image bytes;
        if (null != image)
        {
            add(image);
        }

        // Add the information panel
        add(createProfileInfoPanel());
    }

    public VerticalPanel createProfileInfoPanel()
    {
        VerticalPanel res = new VerticalPanel();

        res.add(createNamePanel());
        res.add(createPhonePanel());
        res.add(createServicesPanel());

        return res;
    }

    private Panel createNamePanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        res.add(new Label("Name: "));

        TextBox nameBox = new TextBox();
        ClientUtils.setNotNullText(nameBox, rest.getName());
        nameBox.addKeyUpHandler(new NameKeyUpHandler(nameBox));

        res.add(nameBox);

        return res;
    }

    private Panel createPhonePanel()
    {
        // creates the phone panel with restaurant phone
        HorizontalPanel res = new HorizontalPanel();

        res.add(new Label("Phone: "));
        TextBox phoneBox = new TextBox();
        ClientUtils.setNotNullText(phoneBox, rest.getPhone());
        phoneBox.addKeyUpHandler(new PhoneKeyUpHandler(phoneBox));
        res.add(phoneBox);

        return res;
    }

    private Panel createServicesPanel()
    {
        HorizontalPanel res = new HorizontalPanel();
        
        CheckBox deliveryCheckBox = createServiceCheckBox(ServiceType.DELIVERY, "delivery", rest);
        CheckBox takeAwayCheckBox = createServiceCheckBox(ServiceType.TAKE_AWAY, "take away", rest);
        CheckBox tableCheckBox = createServiceCheckBox(ServiceType.TABLE, "table", rest);

        res.add(deliveryCheckBox);
        res.add(takeAwayCheckBox);
        res.add(tableCheckBox);

        return res;
    }
    
    private CheckBox createServiceCheckBox(ServiceType service, String name, RestaurantProxy rest)
    {
        CheckBox res = new CheckBox(name);
        List<ServiceType> services = rest.getServices();
        
        boolean value = (null == services) ? false : services.contains(service);
        res.setValue(value);
        res.addClickHandler(new ServiceClickHandler(service, rest));
        res.setEnabled(isAdmin);
        
        return res;
    }

    class NameKeyUpHandler implements KeyUpHandler
    {
        private final TextBox titleBox;
        

        public NameKeyUpHandler(TextBox titleBox)
        {
            this.titleBox = titleBox;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            rest.setName(titleBox.getText());
        }

    }

    class PhoneKeyUpHandler implements KeyUpHandler
    {
        private final TextBox titleBox;

        public PhoneKeyUpHandler(TextBox titleBox)
        {
            this.titleBox = titleBox;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            rest.setPhone(titleBox.getText());
        }

    }

    private class ServiceClickHandler implements ClickHandler
    {
        private ServiceType service;
        private RestaurantProxy rest;
        
        public ServiceClickHandler(ServiceType service, RestaurantProxy rest)
        {
            this.service = service;
            this.rest= rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            List<ServiceType> services = rest.getServices();
            
            if (null == services)
            {
                // TODO maybe move this validation from here
                services = new LinkedList<ServiceType>();
                rest.setServices(services);
            }
            
            boolean isChecked = ((CheckBox) event.getSource()).isEnabled();
            
            if (isChecked)
            {
                if (!services.contains(service))
                {
                    services.add(service);
                }
            }
            else
            {
                if (services.contains(service))
                {
                    services.remove(service);
                }
            }

        }

    }

}
