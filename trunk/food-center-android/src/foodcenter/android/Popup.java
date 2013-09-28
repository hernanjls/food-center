package foodcenter.android;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class Popup
{
    // default receptions for mail
    private final static String[] rec = new String[] {};

    

    
    /**
     * send this msg by mail
     * 
     * @param o is the owner activity
     * @param m is the msg
     * @param s is the subject
     */
    public static void sendMail(Activity o, String s, String m)
    {
        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, rec);
        i.putExtra(Intent.EXTRA_SUBJECT, s);
        i.putExtra(Intent.EXTRA_TEXT, m);
        try
        {
            o.startActivity(Intent.createChooser(i, s + " Send mail..."));
        }
        catch (final android.content.ActivityNotFoundException ex)
        {
            m = "If you had a mail client, you could have send this msg by mail: " + m;
            Toast.makeText(o, m, Toast.LENGTH_LONG).show();
        }
    }
}
