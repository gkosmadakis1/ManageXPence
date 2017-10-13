package uk.co.irokottaki.moneycontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ScreenSlidePageFragment4 extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page4, container, false);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_next).setEnabled(HelpActivity.mPager.getCurrentItem() < HelpActivity.mPagerAdapter.getCount()-1);

        // Add a "next" and "previous" button to the action bar
        MenuItem item = menu.add(Menu.NONE, R.id.action_previous, Menu.NONE, R.string.action_previous);
        MenuItem item2 = menu.add(Menu.NONE, R.id.action_next, Menu.NONE, R.string.action_next);

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    }

}
