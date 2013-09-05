package foodcenter.client.callbacks;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

import foodcenter.service.enums.ServiceType;

public class OnClickServiceCheckBox implements ClickHandler
{
    private final List<ServiceType> services;
    
    public OnClickServiceCheckBox(List<ServiceType> services)
    {
        this.services = services;
    }
    
    @Override
    public void onClick(ClickEvent event)
    {

        CheckBox cb = ((CheckBox) event.getSource());
        ServiceType service = ServiceType.forName(cb.getText());
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

