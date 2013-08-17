package foodcenter.client;

import com.google.gwt.user.client.ui.ValueBoxBase;

public class ClientUtils
{
    public final static String GOOGLE_API_MAPS_KEY = "AIzaSyB3NaTrroFHrGCks8cojJ_OmDPoWWtGoiQ";    //AIzaSyB3NaTrroFHrGCks8cojJ_OmDPoWWtGoiQ
    public final static String GOOGLE_API_MAPS_VER = "2";
        
    public static void setNotNullText(ValueBoxBase<String> w, String s)
    {
        if (null != s)
        {
            w.setText(s);
        }
    }

}
