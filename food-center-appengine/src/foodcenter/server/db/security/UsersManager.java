package foodcenter.server.db.security;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.modules.DbUser;

public class UsersManager
{
    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    public static User getUser()
    {
        return userService.getCurrentUser();
    }
    
    public static boolean isAdmin()
    {
        return userService.isUserAdmin();
    }
    
    public static DbUser getDbUser()
    {
        String email = getUser().getEmail().toLowerCase();
        logger.info("Load user: " + email);
        
        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));

        return DbHandler.find(DbUser.class, query, params);
    }
}
