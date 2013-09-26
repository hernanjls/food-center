package foodcenter.client.panels.common;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class LabeledDatePicker extends HorizontalPanel
{
    private Date date;
    private final TextBox dateTxt;
    private final Label text;
    private final DatePicker datePicker;
    private final PopupPanel popup;

    private static DateTimeFormat dateFormater = DateTimeFormat.getFormat("dd MMM yyyy");
    
    public LabeledDatePicker(String txt)
    {
        this.date = new Date();
        this.text = new Label(txt + " ");
        
        dateTxt = new TextBox();
        dateTxt.setReadOnly(true);
        datePicker = new DatePicker();
        popup = new PopupPanel(true);

        add(this.text);
        add(dateTxt);

        init();
    }

    public Date getDate()
    {
        return date;
    }
    
    private void init()
    {
        datePicker.setValue(date, true); // Set the default value
        dateTxt.setText(dateFormater.format(date));

        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>()
        {
            public void onValueChange(ValueChangeEvent<Date> event)
            {
                date = event.getValue();
                String dateString = dateFormater.format(date);
                dateTxt.setText(dateString);
            }
        });

        popup.add(datePicker);

        dateTxt.addClickHandler(new ClickHandler()
        {

            @Override
            public void onClick(ClickEvent event)
            {
                popup.showRelativeTo(dateTxt);
            }
        });
    }

}
