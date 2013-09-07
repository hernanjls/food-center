package foodcenter.android.actionbar;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import foodcenter.android.R;

public class ActionBarDrawer
{
    private final Activity activity;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle = "Select Option";
    private CharSequence mTitle = "Food Center";

    public ActionBarDrawer(Activity activity, ListView.OnItemClickListener onItemClick)
    {
        this.activity = activity;
        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) activity.findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ActionBarListAdapter(activity, null));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(onItemClick);
        mDrawerToggle = new FoodCenterActionBarDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    public void onPostCreate(Bundle savedInstanceState)
    {
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    public void onConfigurationChanged(Configuration newConfig)
    {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public void closeDrawer()
    {
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    public Object getItemAtPosition(int position)
    {
        return mDrawerList.getItemAtPosition(position);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return mDrawerToggle.onOptionsItemSelected(item);
    }
    
    public void setTitle(CharSequence title)
    {
        mTitle = title;
    }
    
    public CharSequence getTitle()
    {
        return mTitle;
    }
        
    private class FoodCenterActionBarDrawerToggle extends ActionBarDrawerToggle
    {
        public FoodCenterActionBarDrawerToggle()
        {
            super(activity,
                  mDrawerLayout,
                  R.drawable.ic_drawer,
                  R.string.drawer_open,
                  R.string.drawer_close);
        }

        /** Called when a drawer has settled in a completely closed state. */
        public void onDrawerClosed(View view)
        {
            activity.getActionBar().setTitle(mTitle);
            activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        /** Called when a drawer has settled in a completely open state. */
        public void onDrawerOpened(View drawerView)
        {
            activity.getActionBar().setTitle(mDrawerTitle);
            activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    };

}
