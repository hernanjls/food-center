package foodcenter.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

public class FileManager
{
    public static BlobKey saveFile(InputStream is, String mimeType, String fileName) throws IOException
    {
        FileService fileService = FileServiceFactory.getFileService();

        // Create a new Blob file with mime-type
        AppEngineFile file = fileService.createNewBlobFile(mimeType, fileName);

        // Open a channel to write to it, lock because we intend to finalize
        boolean lock = true;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

        // Different standard Java ways of writing to the channel
        // are possible. Here we use a OutputStream:
        OutputStream os = Channels.newOutputStream(writeChannel);
        IOUtils.copy(is, os);

        // Now finalize
        writeChannel.closeFinally();

        return fileService.getBlobKey(file);
    }

    
    public static AppEngineFile getFile(String blobKey)
    {
        if (null == blobKey)
        {
            return null;
        }
        return getFile(new BlobKey(blobKey));
    }
    
    public static AppEngineFile getFile(BlobKey blobKey)
    {
        if (null == blobKey)
        {
            return null;
        }
        
        FileService fileService = FileServiceFactory.getFileService();
        return fileService.getBlobFile(blobKey);
    }
    
    public static void deleteFile(String blobKey) throws IOException
    {
        if (null == blobKey)
        {
            return;
        }
        deleteFile(new BlobKey(blobKey));
    }
    
    public static void deleteFile(BlobKey blobKey) throws IOException
    {
        if (null == blobKey)
        {
            return;
        }
        
        FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile file = fileService.getBlobFile(blobKey);
        
        if (null == file)
        {
            return;
        }
        fileService.delete(file);
    }

}
