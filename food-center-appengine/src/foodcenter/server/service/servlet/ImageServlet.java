package foodcenter.server.service.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.FileManager;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbRestaurant;

/**
 * This is the google way (using request factory is extremely slow)
 */
public class ImageServlet extends HttpServlet
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
    private final static Logger logger = LoggerFactory.getLogger(ImageServlet.class);

    private Map<String, String> fields = new TreeMap<String, String>();
    private String blobSaved = null; // File to deal with

    /**
     * for getting an image
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        BlobKey blobKey = new BlobKey(req.getParameter(BLOB_SERVE_KEY));
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        
        Image oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
        if (null != oldImage)
        {
            Transform resize = ImagesServiceFactory.makeResize(50, 50);
            Image newImage = imagesService.applyTransform(resize, oldImage);
            IOUtils.write(newImage.getImageData(), res.getOutputStream());
        }
    }

    /**
     * for uploading a new image
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
                                                                       IOException
    {
        res.setHeader("Content-Type", "text/html");

        processRequest(req);

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
            FileManager.deleteFile(blobSaved);
            return;
        }

        else if (null != rest && !rest.isEditable())
        {
            logger.info("user: " + userService.getCurrentUser().getEmail()
                        + " not allowed to add image to restaurant: "
                        + restId);
            FileManager.deleteFile(blobSaved);
            return;
        }
        else if (null != comp && !comp.isEditable())
        {
            logger.info("user: " + userService.getCurrentUser().getEmail()
                        + " not allowed to add image to company: "
                        + compId);
            FileManager.deleteFile(blobSaved);
            return;
        }

        // Save the file

        if (null != rest)
        {
            rest.deleteImage();
            rest.setImageKey(blobSaved);
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
        else
        {
            FileManager.deleteFile(blobSaved);
        }
        
    }

    private void processRequest(HttpServletRequest req) throws IOException
    {
        try
        {
            ServletFileUpload upload = new ServletFileUpload();

            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext())
            {
                FileItemStream item = iterator.next();
                InputStream is = item.openStream();
                
                try
                {
                    if (item.isFormField())
                    {
                        String fieldName = item.getFieldName();
                        String value = null;
                            value = IOUtils.toString(is);
    
                        fields.put(fieldName, value);
                    }
                    else
                    {
                        blobSaved = FileManager.saveFile(is, item.getContentType()).getKeyString();
                    }
                }
                finally
                {
                    IOUtils.closeQuietly(is);
                }

            }
        }
        catch (FileUploadException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
}
