package foodcenter.client.panels.restaurant;

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
import foodcenter.client.handlers.ImageUploadedHandler;
import foodcenter.client.handlers.RedrawablePannel;
import foodcenter.client.panels.FileUploadPanel;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantProfilePannel extends HorizontalPanel implements RedrawablePannel
{

    private final RestaurantProxy rest;
    private String imgUrl;
    private final Boolean isEditMode;

    // remove can be done directly on the restaurant

    public RestaurantProfilePannel(RestaurantProxy rest, Boolean isEditMode)
    {
        super();
        this.rest = rest;
        this.isEditMode = isEditMode;
        
        imgUrl = rest.getImageUrl();

        redraw();
    }

    @Override
    public final void redraw()
    {
        // remove all the widgets in this panel
        clear();

        // Add the image
        Image image = new Image(imgUrl);
        if (null != image)
        {
            add(image);
        }
        if (isEditMode)
        {
            image.addClickHandler(new OnClickImage());
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

        boolean value = services.contains(service);
        res.setValue(value);

        if (isEditMode)
        {
            res.addClickHandler(new ServiceClickHandler(service));
        }
        res.setEnabled(isEditMode);

        return res;
    }

    
    
    private class OnClickImage implements ClickHandler, ImageUploadedHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            new FileUploadPanel(this, rest.getId(), null);
        }
        
        @Override
        public void updateImage(String url)
        {
            imgUrl = url;
            redraw();
        }

        
    }
    private class NameKeyUpHandler implements KeyUpHandler
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

    private class PhoneKeyUpHandler implements KeyUpHandler
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

            boolean isChecked = ((CheckBox) event.getSource()).getValue();

            if (isChecked && !services.contains(service))
            {
                services.add(service);
            }
            else if (!isChecked && services.contains(service))
            {
                services.remove(service);
            }
        }
    }

}
