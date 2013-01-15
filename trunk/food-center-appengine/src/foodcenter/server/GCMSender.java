package foodcenter.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class GCMSender
{
    private static String API_KEY = "AIzaSyC8N5hTFxhi6IYnY1NVC6JYl1mVS1EHbOs";

    public static void send(String msg, List<String> devs, int numRetries)
    {
        try
        {
            Sender sender = new Sender(API_KEY);
            Message message = new Message.Builder().addData("msg", msg).build();
            sender.send(message, devs, numRetries);
        }
        catch (IOException e)
        {
            Logger.getLogger(GCMSender.class.getName()).log(Level.WARNING, e.getMessage());
        }
    }
}
