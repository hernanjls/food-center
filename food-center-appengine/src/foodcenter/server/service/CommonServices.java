package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.modules.AbstractDbOrder;
import foodcenter.server.db.modules.DbChannelToken;
import foodcenter.server.db.modules.DbUser;
import foodcenter.service.autobean.AutoBeanHelper;
import foodcenter.service.autobean.OrderBroadcast;
import foodcenter.service.autobean.OrderBroadcastAutoBeanFactory;
import foodcenter.service.autobean.OrderBroadcastType;

public class CommonServices
{

    private static Logger logger = LoggerFactory.getLogger(CommonServices.class);
    
    private static OrderBroadcastAutoBeanFactory factory = AutoBeanFactorySource.create(OrderBroadcastAutoBeanFactory.class);
    
    public static void broadcastToUsers(String msg, String... emails)
    {
        if (null == msg || null == emails || 0 == emails.length)
        {
            return;
        }
        List<DbUser> users = findUsers(emails);
        broadcastMsgToUsers(msg, users);
    }

    public static void broadcastToRestaurant(AbstractDbOrder order, OrderBroadcastType type)
    {

        List<DbChannelToken> tokens = getChannelTokens(order.getRestBranchId());
        if ((null == tokens) || tokens.isEmpty())
        {
            return;
        }

        OrderBroadcast msgBean = factory.create(OrderBroadcast.class).as();
        
        msgBean.setType(type);
        msgBean.setId(order.getId());
        
        String msg = AutoBeanHelper.serializeToJson(msgBean);
        
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        
        for (DbChannelToken t : tokens)
        {
            channelService.sendMessage(new ChannelMessage(t.getKey(), msg));
        }
    }


    protected static List<DbChannelToken> getChannelTokens(String restBranchId)
    {
        String query = "branchId == branchIdP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("branchIdP", restBranchId));

        List<DbChannelToken> tokens = DbHandler.find(DbChannelToken.class,
                                                     query,
                                                     params,
                                                     null,
                                                     Integer.MAX_VALUE);
        return tokens;
    }

    /**
     * @param emails list of emails to search the users
     * @return a list of all the users with the emails, empty list on input error or not found
     */
    private static List<DbUser> findUsers(String... emails)
    {
        logger.info("findUsers");
        if (null == emails || 0 == emails.length)
        {
            logger.debug("findUsers input is null or empty");
            return new ArrayList<DbUser>();
        }
        StringBuilder query = new StringBuilder();
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        int i = 0;
        int n = emails.length - 1;
        for (i=0; i < n; ++i)
        {
            query.append("email == emailP" + i);
            query.append(" || ");   
            params.add(new DeclaredParameter("emailP" + i, emails[i]));
        }
        query.append("email == emailP" + i);
        params.add(new DeclaredParameter("emailP" + i, emails[i]));
        
        return DbHandler.find(DbUser.class, query.toString(), params, null, Integer.MAX_VALUE);
    }
    
    private static void broadcastMsgToUsers(String msg, List<DbUser> users)
    {
        List<String> gcmKeys = new ArrayList<String>();
        for (DbUser u : users)
        {
            String gcmKey = u.getGcmKey();
            if (null != gcmKey)
            {
                gcmKeys.add(gcmKey);
            }
        }
        if (!gcmKeys.isEmpty())
        {
            GCMSender.send(msg, gcmKeys, 5);
        }
    }
    
}
