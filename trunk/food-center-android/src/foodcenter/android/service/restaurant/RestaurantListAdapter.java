package foodcenter.android.service.restaurant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import foodcenter.android.service.DownloadImageTask;
import foodcenter.android.service.Setup;
import foodcenter.service.proxies.RestaurantProxy;

public class RestaurantListAdapter extends BaseAdapter
{
    private Context context;

    // references to our restaurants
    private RestaurantProxy[] rests;
    
    public RestaurantListAdapter(Context context, //
                                 RestaurantProxy[] rests)
    {
        super();

        this.context = context;
        this.rests = rests;
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
//        ImageView imageView;
//        if (convertView == null)
//        { // if it's not recycled, initialize some attributes
//            imageView = new ImageView(context);
//            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
//        }
//        else
//        {
//            imageView = (ImageView) convertView;
//        }
//        new DownloadImageTask(imageView).execute(Setup.PROD_URL + getItem(position).getImageUrl());
//        return imageView;
        
        
      TextView imageView;
      if (convertView == null)
      { // if it's not recycled, initialize some attributes
          imageView = new TextView(context);
//          imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//          imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
          imageView.setPadding(8, 8, 8, 8);
      }
      else
      {
          imageView = (TextView) convertView;
      }
//      new DownloadImageTask(imageView).execute(Setup.PROD_URL + getItem(position).getImageUrl());
      imageView.setText(getItem(position).getName() + " pos " + position);
      return imageView;
      
      

        
    }

}