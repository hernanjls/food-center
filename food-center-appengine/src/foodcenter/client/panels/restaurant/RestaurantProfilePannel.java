package foodcenter.client.panels.restaurant;

import java.util.ArrayList;
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
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class RestaurantProfilePannel extends HorizontalPanel
{

    private RestaurantProxy rest;
    private Boolean isEditMode;
    private final RestaurantAdminServiceRequest requestService;

    // Add can't be done directly on the restaurant because of RF methodoligy
    private final List<ServiceType> addedServices;

    // remove can be done directly on the restaurant

    public RestaurantProfilePannel(RestaurantAdminServiceRequest requestService,
                                   RestaurantProxy rest,
                                   Boolean isEditMode)
    {
        super();
        this.requestService = requestService;
        this.isEditMode = isEditMode;
        this.rest = rest;

        this.addedServices = new ArrayList<ServiceType>();

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
        nameBox.setEnabled(isEditMode);
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
        phoneBox.setEnabled(isEditMode);
        res.add(phoneBox);

        return res;
    }

    private Panel createServicesPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        CheckBox deliveryCheckBox = createServiceCheckBox(ServiceType.DELIVERY, "delivery");
        res.add(deliveryCheckBox);

        CheckBox takeAwayCheckBox = createServiceCheckBox(ServiceType.TAKE_AWAY, "take away");
        res.add(takeAwayCheckBox);

        CheckBox tableCheckBox = createServiceCheckBox(ServiceType.TABLE, "table");
        res.add(tableCheckBox);

        return res;
    }

    private CheckBox createServiceCheckBox(ServiceType service, String name)
    {
        CheckBox res = new CheckBox(name);
        List<ServiceType> services = rest.getServices();

        boolean value = addedServices.contains(service) || services.contains(service);
        res.setValue(value);

        if (isEditMode)
        {
            res.addClickHandler(new ServiceClickHandler(service));
        }
        res.setEnabled(isEditMode);

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
        private final ServiceType service;

        public ServiceClickHandler(ServiceType service)
        {
            this.service = service;

        }

        @Override
        public void onClick(ClickEvent event)
        {
            List<ServiceType> services = rest.getServices();

            boolean isChecked = ((CheckBox) event.getSource()).isEnabled();

            if (isChecked)
            {
                if (!services.contains(service) && !addedServices.contains(service))
                {
                    addedServices.add(service);
                    requestService.addRestaurantServiceType(rest, service);
                }
            }
            else
            {
                if (services.contains(service))
                {
                    requestService.removeRestaurantServiceType(rest, service);
                    services.remove(service);
                }
                else if (addedServices.contains(service))
                {
                    requestService.removeRestaurantServiceType(rest, service);
                    addedServices.remove(service);
                }
            }
        }
    }

}
