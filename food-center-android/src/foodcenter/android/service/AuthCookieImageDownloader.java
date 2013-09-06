package foodcenter.android.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class AuthCookieImageDownloader extends BaseImageDownloader
{
    public AuthCookieImageDownloader(Context context)
    {
        super(context);
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException
    {
        HttpURLConnection conn = createConnection(imageUri);

        if (null != extra && Map.class.isInstance(extra))
        {
            @SuppressWarnings("unchecked")
            Map<String, String> reqProps = (Map<String, String>) extra;
            for (String k : reqProps.keySet())
            {
                conn.setRequestProperty(k, reqProps.get(k));    
            }
        }

        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT)
        {
            conn = createConnection(conn.getHeaderField("Location"));
            redirectCount++;
        }

        return new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);
    }

}
