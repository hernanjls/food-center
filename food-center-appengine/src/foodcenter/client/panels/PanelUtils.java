package foodcenter.client.panels;

import com.google.gwt.user.client.ui.ValueBoxBase;

public class PanelUtils
{

    public static void setNotNullText(ValueBoxBase<String> w, String s)
    {
        if (null != s)
        {
            w.setText(s);
        }
    }

}
