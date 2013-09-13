package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import foodcenter.client.WebClientUtils;
import foodcenter.client.callbacks.ImageUploadedCallback;
import foodcenter.client.callbacks.OnClickServiceCheckBox;
import foodcenter.client.panels.common.EditableImage;
import foodcenter.client.panels.common.FileUploadPanel;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantProfilePannel extends HorizontalPanel
{

    private static final Integer MAX_PROFILE_LEN = 150;
    
    private final RestaurantProxy rest;
    private EditableImage img;
    private final boolean isEditMode;

    // remove can be done directly on the restaurant

    public RestaurantProfilePannel(RestaurantProxy rest, boolean isEditMode)
    {
        super();
        this.rest = rest;
        this.isEditMode = isEditMode;
        
        img = new EditableImage(rest.getImageUrl());
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
        img.updateImage(rest.getImageUrl());
        add(img);

        // Add the information panel
        add(createProfileInfoPanel());
        
        add(createInfoPanel());
    }

    public VerticalPanel createProfileInfoPanel()
    {
        VerticalPanel res = new VerticalPanel();

        res.add(createNamePanel());
        res.add(createPhonePanel());
        res.add(createServicesPanel());

        return res;
    }

    public Widget createInfoPanel()
    {
        if (isEditMode)
        {
            VerticalPanel p = new VerticalPanel();
            
            Label counter = new Label();
            p.add(counter);
                
            RichTextArea area = new RichTextArea();
            WebClientUtils.setNotNullHtml(area, rest.getInfo());
            p.add(area);
            area.setSize("90%", "100px");
            area.addKeyUpHandler(new AreaKeyUpHandler(area, counter));

            counter.setWidth("90%");
            counter.setText("using characters: " + area.getHTML().length() + " out of " + MAX_PROFILE_LEN);

            Button b = new Button("Set");
            b.addClickHandler(new OnClickSetButton(area));
            b.setWidth("90%");
            p.add(b);

            return p;
        }
        InlineHTML res = new InlineHTML();
        WebClientUtils.setNotNullHtml(res, rest.getInfo());
        return res;
    }
    
    private Panel createNamePanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        res.add(new Label("Name: "));

        TextBox nameBox = new TextBox();
        WebClientUtils.setNotNullText(nameBox, rest.getName());
        nameBox.addKeyUpHandler(new NameKeyUpHandler(nameBox));
        nameBox.setReadOnly(!isEditMode);
        res.add(nameBox);

        return res;
    }

    private Panel createPhonePanel()
    {
        // creates the phone panel with restaurant phone
        HorizontalPanel res = new HorizontalPanel();

        res.add(new Label("Phone: "));
        TextBox phoneBox = new TextBox();
        WebClientUtils.setNotNullText(phoneBox, rest.getPhone());
        phoneBox.addKeyUpHandler(new PhoneKeyUpHandler(phoneBox));
        phoneBox.setReadOnly(!isEditMode);
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
        List<ServiceType> services = rest.getServices();

        ServiceType service = ServiceType.forName(name);
        boolean value = services.contains(service);
        res.setValue(value);

        if (isEditMode)
        {
            res.addClickHandler(new OnClickServiceCheckBox(rest.getServices()));
        }
        res.setEnabled(isEditMode);

        return res;
    }

    private class OnClickImage implements ClickHandler, ImageUploadedCallback
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null == rest.getId())
            {
                Window.alert("You must 1st save the restaurant");
            }
            else
            {
                new FileUploadPanel(this, rest.getId(), null);
            }
        }

        @Override
        public void updateImage(String url)
        {
            rest.setImageUrl(url);
            img.updateImage(url);
        }
        
        @Override
        public void updateImage(String url, String width, String height)
        {
            rest.setImageUrl(url);
            img.updateImage(url, width, height);
        }

    }

    
    private class OnClickSetButton implements ClickHandler
    {
        private final RichTextArea txtArea;

        public OnClickSetButton(RichTextArea titleBox)
        {
            this.txtArea = titleBox;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            String html = txtArea.getHTML();
            if (html.length() > MAX_PROFILE_LEN)
            {
                Window.alert("Up to " + MAX_PROFILE_LEN + " characters. " + html);
                if (html.endsWith("<br>"))
                {
                    html.substring(0, html.length() - "<br>".length());
                }
                else
                {
                    html = html.substring(0, MAX_PROFILE_LEN-1);
                }
                WebClientUtils.setNotNullHtml(txtArea, html);
                return;
            }
            rest.setInfo(html);
    
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
    
    private class AreaKeyUpHandler implements KeyUpHandler
    {
        private final RichTextArea area;
        private final Label counter;
        
        public AreaKeyUpHandler(RichTextArea area, Label counter)
        {
            this.area = area;
            this.counter = counter;
        }
        
        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            counter.setText("using characters: " + area.getHTML().length() + " out of " + MAX_PROFILE_LEN);
        }
    }

}
