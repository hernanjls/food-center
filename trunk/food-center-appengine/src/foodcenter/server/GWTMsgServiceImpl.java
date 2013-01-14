package foodcenter.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import foodcenter.server.db.Datastore;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.shared.GWTMsgService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GWTMsgServiceImpl extends RemoteServiceServlet implements GWTMsgService
{

    DbHandler db = new DbHandlerImp();
    private static String API_KEY = "AIzaSyC8N5hTFxhi6IYnY1NVC6JYl1mVS1EHbOs";

    public List<String> getMsgs() throws IllegalArgumentException
    {
        return db.getMsgs();

    }

    public void addMsg(String msg) throws IllegalArgumentException
    {
        db.saveMsg(msg);

        Sender sender = new Sender(API_KEY);
        Message message = new Message.Builder().addData("msg", msg).build();
        List<String> dev = Datastore.getDevices();
        if (!dev.isEmpty())
        {
            try
            {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "gcm: " + dev.size());
                sender.send(message, dev, 5);
            }
            catch (IOException e)
            {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, e.getMessage());
            }
        }
        else
        {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "no devices to broadcast");
        }
    }

    public void removeMsg(String msg) throws IllegalArgumentException
    {
        long res = db.deleteMsg(msg);
        if (0 == res)
        {
            throw new IllegalArgumentException("msg doesnt exists: " + msg);
        }

    }
}
