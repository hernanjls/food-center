package foodcenter.android.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import foodcenter.android.R;
import foodcenter.android.activities.rest.RestaurantActivity;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantListAdapter extends BaseAdapter implements OnClickListener
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
            this.options = AndroidRequestUtils.getDefaultDisplayImageOptions(activity);
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
        if (position >= getCount())
        {
            return null;
        }
        return rests[position];
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (null == convertView)
        {
            convertView = activity.getLayoutInflater().inflate(R.layout.main_view_rest_grid_item,
                                                                 parent,
                                                                 false);
        }

        RestaurantProxy r = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.main_view_rest_grid_item_txt);
        textView.setText(r.getName());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.main_view_rest_grid_item_img);
        String url = AndroidRequestUtils.getBaseUrl(activity) + r.getImageUrl();
        ImageLoader.getInstance().displayImage(url, imageView, options);

        // Restaurant is not fully loaded => don't add it to obj cache
        convertView.setTag(R.id.adapter_id_tag, r.getId());
        convertView.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View view)
    {
        String restId = (String) view.getTag(R.id.adapter_id_tag);
        Intent intent = new Intent(activity, RestaurantActivity.class);
        intent.putExtra(RestaurantActivity.EXTRA_REST_ID, restId);
        activity.startActivity(intent);

    }

}
