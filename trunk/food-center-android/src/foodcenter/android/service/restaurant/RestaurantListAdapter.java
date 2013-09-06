package foodcenter.android.service.restaurant;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantListAdapter extends BaseAdapter
{
    private Activity activity;

    // references to our restaurants
    private RestaurantProxy[] rests;
    private DisplayImageOptions options;

    public RestaurantListAdapter(Activity activity,
                                 DisplayImageOptions options,
                                 RestaurantProxy[] rests)
    {
        super();

        this.activity = activity;
        this.rests = rests;
        this.options = options;
        if (null == this.options)
        {
            this.options = RequestUtils.getDefaultDisplayImageOptions(activity);
        }
    }

    @Override
    public int getCount()
    {
        return rests.length;
    }

    @Override
    public RestaurantProxy getItem(int position)
    {
        return rests[position];
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position)
    {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final RelativeLayout layout;
        if (convertView == null)
        {
            layout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.rest_grid_item,
                                                                           parent,
                                                                           false);
        }
        else
        {
            layout = (RelativeLayout) convertView;
        }

        RestaurantProxy r = getItem(position);

        TextView textView = (TextView)layout.getChildAt(1);
        textView.setText(r.getName());

        ImageView imageView = (ImageView)layout.getChildAt(0);
        String url = Setup.PROD_URL + r.getImageUrl();
        ImageLoader.getInstance().displayImage(url, imageView, options);
        
        return layout;
    }

}
