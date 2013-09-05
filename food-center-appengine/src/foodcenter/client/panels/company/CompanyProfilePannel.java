package foodcenter.client.panels.company;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.ClientUtils;
import foodcenter.client.callbacks.ImageUploadedCallback;
import foodcenter.client.callbacks.OnClickServiceCheckBox;
import foodcenter.client.panels.common.EditableImage;
import foodcenter.client.panels.common.FileUploadPanel;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CompanyProxy;

public class CompanyProfilePannel extends HorizontalPanel
{

    private final CompanyProxy comp;
    private EditableImage img;
    private final boolean isEditMode;

    // remove can be done directly on the compaurant

    public CompanyProfilePannel(CompanyProxy comp, boolean isEditMode)
    {
        super();
        this.comp = comp;
        this.isEditMode = isEditMode;
        
        img = new EditableImage(comp.getImageUrl());
        if (isEditMode)
        {
            img.setClickHandler(new OnClickImage());
        }

        redraw();
    }

    public final void redraw()
    {
        // remove all the widgets in this panel
        clear();

        // Add the image
        img.updateImage(comp.getImageUrl());
        add(img);

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
        ClientUtils.setNotNullText(nameBox, comp.getName());
        nameBox.addKeyUpHandler(new NameKeyUpHandler(nameBox));
        nameBox.setEnabled(isEditMode);
        res.add(nameBox);

        return res;
    }

    private Panel createPhonePanel()
    {
        // creates the phone panel with compaurant phone
        HorizontalPanel res = new HorizontalPanel();

        res.add(new Label("Phone: "));
        TextBox phoneBox = new TextBox();
        ClientUtils.setNotNullText(phoneBox, comp.getPhone());
        phoneBox.addKeyUpHandler(new PhoneKeyUpHandler(phoneBox));
        phoneBox.setEnabled(isEditMode);
        res.add(phoneBox);

        return res;
    }

    private Panel createServicesPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        CheckBox deliveryCheckBox = createServiceCheckBox(ServiceType.DELIVERY.getName());
        res.add(deliveryCheckBox);

        CheckBox takeAwayCheckBox = createServiceCheckBox(ServiceType.TAKE_AWAY.getName());
        res.add(takeAwayCheckBox);

        CheckBox tableCheckBox = createServiceCheckBox(ServiceType.TABLE.getName());
        res.add(tableCheckBox);

        return res;
    }

    private CheckBox createServiceCheckBox(String name)
    {
        CheckBox res = new CheckBox(name);
        List<ServiceType> services = comp.getServices();

        ServiceType service = ServiceType.forName(name);
        boolean value = services.contains(service);
        res.setValue(value);

        if (isEditMode)
        {
            res.addClickHandler(new OnClickServiceCheckBox(comp.getServices()));
        }
        res.setEnabled(isEditMode);

        return res;
    }

    private class OnClickImage implements ClickHandler, ImageUploadedCallback
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null == comp.getId())
            {
                Window.alert("You must 1st save the compaurant");
            }
            else
            {
                new FileUploadPanel(this, null, comp.getId());
            }
        }

        @Override
        public void updateImage(String url)
        {
            comp.setImageUrl(url);
            img.updateImage(url);
        }
        
        @Override
        public void updateImage(String url, String width, String height)
        {
            comp.setImageUrl(url);
            img.updateImage(url, width, height);
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
            comp.setName(titleBox.getText());
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
            comp.setPhone(titleBox.getText());
        }

    }

}
