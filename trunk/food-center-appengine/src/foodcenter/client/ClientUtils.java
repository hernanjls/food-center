package foodcenter.client;

import com.google.gwt.user.client.ui.ValueBoxBase;

public class ClientUtils
{
    public final static String GOOGLE_API_MAPS_KEY = "AIzaSyB3NaTrroFHrGCks8cojJ_OmDPoWWtGoiQ";    //AIzaSyB3NaTrroFHrGCks8cojJ_OmDPoWWtGoiQ
    public final static String GOOGLE_API_MAPS_VER = "2";
    
    public final static double GOOGLE_API_DEFAULT_LAT = 32.7775;
    public final static double GOOGLE_API_DEFAULT_LNG = 35.021667;
    public final static String GOOGLE_API_DEFAULT_ADDR = "Israeli Institute of Technology, Viazman 87, Kesalsaba, Israel";
    
    public static void setNotNullText(ValueBoxBase<String> w, String s)
    {
        if (null != s)
        {
            w.setText(s);
        }
    }

}
