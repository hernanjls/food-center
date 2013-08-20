package foodcenter.server.service.blobstore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.FileManager;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbRestaurant;

/**
 * This is the google way (using request factory is extremely slow)
 */
public class BlobUrlServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 8763734670789018659L;

    public final static String BLOB_UPLOAD_FILE_PARAM = "myFile";
    public final static String BLOB_UPLOAD_REST_ID_PARAM = "restId";
    public final static String BLOB_UPLOAD_COMP_ID_PARAM = "compId";

    public static final String BLOB_SERVE_KEY = "blob-key";

    private static UserService userService = UserServiceFactory.getUserService();
    private final static Logger logger = LoggerFactory.getLogger(BlobUrlServlet.class);

    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    private Map<String, String> fields = new TreeMap<String, String>();
    private FileItem fileItem = null; // File to deal with

    /**
     * for getting an image
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        blobstoreService.serve(blobKey, res);
    }

    /**
     * for uploading a new image
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
                                                                       IOException
    {

        res.setHeader("Content-Type", "text/html");

        if (false == processRequest(req, this.getServletContext()))
        {
            return;
        }

        String restId = fields.get(BLOB_UPLOAD_REST_ID_PARAM);
        String compId = fields.get(BLOB_UPLOAD_COMP_ID_PARAM);

        DbRestaurant rest = null;
        DbCompany comp = null;

        if (null != restId)
        {
            rest = DbHandler.find(DbRestaurant.class, restId);
        }
        else if (null != compId && null == rest)
        {
            comp = DbHandler.find(DbCompany.class, compId);
        }

        if (null == rest && null == comp)
        {
            logger.error("rest and comp are null");
            return;
        }

        else if (null != rest && !rest.isEditable())
        {
            logger.info("user: " + userService.getCurrentUser().getEmail()
                        + " not allowed to add image to restaurant: "
                        + restId);
            return;
        }
        else if (null != comp && !comp.isEditable())
        {
            logger.info("user: " + userService.getCurrentUser().getEmail()
                        + " not allowed to add image to company: "
                        + compId);
            return;
        }

        // Save the file
        BlobKey blobKey = FileManager.saveFile(fileItem.getInputStream(), //
                                               fileItem.getContentType());

        if (null != rest)
        {
            rest.deleteImage();
            rest.setImageKey(blobKey.getKeyString());
            rest.jdoPostLoad(); // Set image URL according to the Blob-Key
            res.getWriter().write(rest.getImageUrl());
        }
        else if (null != comp)
        {
            // TODO set comp image on blob upload
            // comp.setImageKey;
            // comp.jdoPostLoad();
            // res.getWriter().write(comp.getImageUrl());
        }
    }

    private boolean processRequest(HttpServletRequest req, ServletContext servletContext)
    {
        List<FileItem> items = null;

        // Parse the items using Apache commons-fileupload
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        try
        {
            items = upload.parseRequest(req); // Parse the request
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return false;
        }

        return parseItems(items);
    }

    private boolean parseItems(List<FileItem> items)
    {
        if (null == items)
        {
            logger.error("null items");
            return false;
        }

        for (FileItem item : items)
        {
            if (item.isFormField())
            {
                String fieldName = item.getFieldName();
                String value = item.getString();
                fields.put(fieldName, value);
            }
            else
            {
                fileItem = item;
            }
        }
        return (fileItem != null);
    }

}
