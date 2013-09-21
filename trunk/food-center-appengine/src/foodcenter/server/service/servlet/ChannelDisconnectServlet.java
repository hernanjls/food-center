package foodcenter.server.service.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.modules.DbChannelToken;

public class ChannelDisconnectServlet extends HttpServlet
{

    private static final long serialVersionUID = -146434946135472791L;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
                                                                       IOException
    {

        ChannelService channelService = ChannelServiceFactory.getChannelService();
        
        // get the channel key!
        ChannelPresence presence = channelService.parsePresence(req);
        
        String query = "key == keyP";
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("keyP", presence));
        
        DbHandler.delete(DbChannelToken.class, query, params);
    }
}
