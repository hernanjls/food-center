package foodcenter.android.activities.coworkers;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import foodcenter.android.R;
import foodcenter.android.service.AndroidRequestUtils;

@SuppressLint("DefaultLocale")
public class CoworkersListAdapter extends BaseAdapter
{
    private static final int PROFILE_IMG = R.drawable.ic_person;
    private static final int COWORKER_IMG = R.drawable.ic_group;

    private final String[] coworkers;
    private final String me;

    private final CoworkersActivity activity;
    
    private final  boolean enabled;
    
    public CoworkersListAdapter(CoworkersActivity activity, String[] coworkers, boolean isEnabled)
    {
        super();
        this.activity = activity;
        this.coworkers = coworkers;
        this.enabled = isEnabled;

        me = AndroidRequestUtils.getSharedPreferences(activity.getApplicationContext())
            .getString(AndroidRequestUtils.PREF_ACCOUNT_NAME, "Unknown Account")
            .toLowerCase();

    }

    @Override
    public int getCount()
    {
        if (null == coworkers)
        {
            return 0;
        }
        return coworkers.length;
    }

    @Override
    public String getItem(int position)
    {
        if (position > getCount())
        {
            return null;
        }
        return coworkers[position];
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position)
    {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = activity.getLayoutInflater().inflate(R.layout.coworkers_view_list_item,
                                                        parent,
                                                        false);

            CoworkerViewHolder holder = new CoworkerViewHolder();
            view.setTag(holder);

            holder.img = (ImageView) view.findViewById(R.id.coworkers_view_list_item_img);
            holder.txt = (TextView) view.findViewById(R.id.coworkers_view_list_item_txt);

        }

        String email = getItem(position);

        int img = ((null != email) && email.equals(me)) ? PROFILE_IMG : COWORKER_IMG;

        CoworkerViewHolder holder = (CoworkerViewHolder) view.getTag();
        holder.txt.setText(email);
        holder.img.setImageResource(img);

        return view;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return enabled;
    }
    
    private class CoworkerViewHolder
    {
        private ImageView img;
        private TextView txt;
    }
}
