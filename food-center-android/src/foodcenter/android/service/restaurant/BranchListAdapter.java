package foodcenter.android.service.restaurant;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.activities.rest.BranchActivity;
import foodcenter.service.proxies.RestaurantBranchProxy;

public class BranchListAdapter extends BaseAdapter implements OnClickListener
{

    private final Activity activity;
    private final RestaurantBranchProxy[] branches;
    private static final int layoutId = R.layout.rest_view_branch_list_item;

    public BranchListAdapter(Activity activity, RestaurantBranchProxy[] branches)
    {
        super();

        this.activity = activity;
        this.branches = branches;
    }

    @Override
    public int getCount()
    {
        return branches.length;
    }

    @Override
    public RestaurantBranchProxy getItem(int position)
    {
        if (position >= getCount())
        {
            return null;
        }
        return branches[position];
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
        final TextView layout;
        if (convertView == null)
        {
            layout = (TextView) activity.getLayoutInflater().inflate(layoutId, parent, false);
        }
        else
        {
            layout = (TextView) convertView;
        }

        RestaurantBranchProxy b = getItem(position);

        layout.setText(b.getAddress());

        layout.setTag(R.id.branch_id_tag, b);
        layout.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view)
    {
        // Get branch from tag and store it in ObjectStore
        RestaurantBranchProxy b = (RestaurantBranchProxy) view.getTag(R.id.branch_id_tag);
        ObjectStore.put(b.getId(), b);
        
        //Add branch id to the intent
        Intent intent = new Intent(activity, BranchActivity.class);
        intent.putExtra(BranchActivity.EXTRA_BRANCH_ID, b.getId());
        
        // Activity gets the id from intent and branch from ObjectStore
        activity.startActivity(intent);
    }

}
