/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package foodcenter.android.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import foodcenter.android.R;
import foodcenter.android.activities.main.MainActivity;
import foodcenter.service.FoodCenterRequestFactory;

/**
 * Utility methods for getting the base URL for client-server communication and
 * retrieving shared preferences.
 */
public class AndroidRequestUtils
{

    public static final String SERVER_ERROR_COOKIE_AUTH = "Could not parse payload: payload[0] = <";
    
    /** The URL of the production service. */
    public static final String PROD_URL = "https://food-center.appspot.com";
    public static final String DEV_URL = "http://10.0.0.32:8888";
    

    /** Cookie name for authorization. */
    private static final String PROD_AUTH_COOKIE_NAME = "SACSID";
    private static final String DEV_AUTH_COOKIE_NAME = "dev_appserver_login";
    
    private static String SERVER_URL = null;
    

    /** Tag for logging. */
    private static final String TAG = AndroidRequestUtils.class.getSimpleName();

    // Shared constants

    /** Key for server/account name in shared preferences. */
    public static final String PREF_SERVER_URL = "PREF_SERVER_URL";
    public static final String PREF_ACCOUNT_NAME = "accountName";

    /** Key for auth cookie name in shared preferences. */
    public static final String AUTH_COOKIE = "authCookie";

    /** URL suffix for the RequestFactory servlet. */
    public static final String RF_METHOD = "/gwtRequest";

    /** An intent name for receiving registration/unregistration status. */
    public static final String UPDATE_UI_INTENT = getPackageName() + ".UPDATE_UI";

    // End shared constants

    /** Key for shared preferences. */
    private static final String SHARED_PREFS = "FOODCENTER_PREFS";

    /** Should be called on startup and after login */
    public static void setUpUrl(Context context)
    {
        SharedPreferences p = getSharedPreferences(context);
        SERVER_URL = p.getString(PREF_SERVER_URL, PROD_URL);
    }
    
    /** Returns the (debug or production) URL associated with the registration service. */
    public static String getBaseUrl()
    {
        return SERVER_URL;
    }

    public static boolean isDev()
    {
        return (!SERVER_URL.equals(PROD_URL));
    }

    /** Default behavior for non-production URL is development-server cookie */
    public static String getAuthCookieName()
    {
        return (isDev()) ?  DEV_AUTH_COOKIE_NAME : PROD_AUTH_COOKIE_NAME ;
    }
    
    /**
     * Creates and returns an initialized {@link FoodCenterRequestFactory} of the given
     * type, with authCookie added to the request. <br>
     * RequestUtils.getSharedPrefs will be used.
     */
    public static FoodCenterRequestFactory getFoodCenterRF(Context context)
    {
//        if (null == foodCenterRF)
//        {
//            foodCenterRF = getRequestFactory(context, FoodCenterRequestFactory.class);
//        }
//        return foodCenterRF;
        
        return getRequestFactory(context, FoodCenterRequestFactory.class);
    }

    /**
     * Creates and returns an initialized {@link RequestFactory} of the given
     * type, with authCookie added to the request. <br>
     * RequestUtils.getSharedPrefs will be used.
     */
    private static <T extends RequestFactory> T getRequestFactory(Context context,
                                                                  Class<T> factoryClass)
    {
        // request factory uses Thread.currentThread().getContextClassLoader() to load factory
        Thread.currentThread().setContextClassLoader(MainActivity.class.getClassLoader());
        T requestFactory = RequestFactorySource.create(factoryClass);

        SharedPreferences prefs = AndroidRequestUtils.getSharedPreferences(context);
        String authCookie = prefs.getString(AndroidRequestUtils.AUTH_COOKIE, null);

        String uriString = AndroidRequestUtils.getBaseUrl() + RF_METHOD;
        URI uri;
        try
        {
            uri = new URI(uriString);
        }
        catch (URISyntaxException e)
        {
            Log.w(TAG, "Bad URI: " + uriString, e);
            return null;
        }
        requestFactory.initialize(new SimpleEventBus(), //
                                  new AndroidRequestTransport(uri, authCookie, new DefaultHttpClient()));

        return requestFactory;
    }

    /**
     * Helper method to get a SharedPreferences instance.
     */
    public static SharedPreferences getSharedPreferences(Context context)
    {
        return context.getApplicationContext().getSharedPreferences(SHARED_PREFS, 0);
    }


    /**
     * 
     * @param context
     * @return the default display image options for Ultimate Image Loader
     */
    public static DisplayImageOptions getDefaultDisplayImageOptions(Context context)
    {
        Map<String, String> extra = new HashMap<String, String>();
        String cookie = getSharedPreferences(context).getString(AndroidRequestUtils.AUTH_COOKIE,
                                                                null);
        if (null != cookie)
        {
            extra.put("Cookie", cookie);
        }

        return new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_error)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .extraForDownloader(extra)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    }


    /**
     * Returns the package name of this class.
     */
    private static String getPackageName()
    {
        return AndroidRequestUtils.class.getPackage().getName();
    }
}
