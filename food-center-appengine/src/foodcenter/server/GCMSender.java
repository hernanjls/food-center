package foodcenter.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class GCMSender
{
    private static String SERVER_API_KEY_GCM = "AIzaSyBRTi4GJOeB0aYwxRjr-hbe3FZDvOQzQ3w";

    public static void send(String msg, String dev, int numRetries)
    {
        try
        {
            Sender sender = new Sender(SERVER_API_KEY_GCM);
            Message message = new Message.Builder().addData("msg", msg).build();
            sender.send(message, dev, numRetries);
        }
        catch (IOException e)
        {
            Logger.getLogger(GCMSender.class.getName()).log(Level.WARNING, e.getMessage());
        }

    }

    public static void send(String msg, List<String> devs, int numRetries)
    {
        try
        {
            Sender sender = new Sender(SERVER_API_KEY_GCM);
            Message message = new Message.Builder().addData("msg", msg).build();
            sender.send(message, devs, numRetries);
        }
        catch (IOException e)
        {
            Logger.getLogger(GCMSender.class.getName()).log(Level.WARNING, e.getMessage());
        }
    }
}
