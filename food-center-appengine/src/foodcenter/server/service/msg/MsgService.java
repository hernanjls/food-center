package foodcenter.server.service.msg;

import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;

public class MsgService
{

    static DbHandler db = new DbHandlerImp();

    public static void createMsg(String msg)
    {
        db.saveMsg(msg);
    }

    public static void deleteMsg(String msg)
    {

        db.deleteMsg(msg);
    }

    public static List<String> getMsgs()
    {
        return db.getMsgs();
    }

}
