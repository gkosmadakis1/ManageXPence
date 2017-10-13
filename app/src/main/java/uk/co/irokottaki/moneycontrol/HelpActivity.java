package uk.co.irokottaki.moneycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HelpActivity extends AppCompatActivity {
    RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES =5 ;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    public static ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    public static PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        setTitle("Help");


        layout = (RelativeLayout) findViewById(R.id.helpActivityLayout);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")){
            layout.setBackgroundResource(R.drawable.backgroundimg);//need to call it somewhere to get the wood style displayed
        }

        //the case where the user has selected for a background on image from the device gallery
        else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")){

            SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            //retrieve the file path from preferences
            String filePath = prefers.getString("GalleryImage", "#00000000");
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            ImageView imageView = (ImageView)this.findViewById(R.id.ImageView);
            imageView.setImageBitmap(bitmap);
            /*final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            int scale = 1;
            while(options.outWidth / scale / 2 >= 70 &&
                    options.outHeight / scale / 2 >= 70) {
                scale *= 2;
            }
            options.inSampleSize = 4;*/
            //adjust the width and height to the layout
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float widthInPercentage=0f;
                float heightInPercentage=0f;
                int deviceWidth = metrics.widthPixels;
                int deviceHeight = metrics.heightPixels;

                widthInPercentage = ((float) 625 / 600) * 100;
                heightInPercentage = ((float) 3300 / 1024) * 100;//was 800

                //ONLY IN LANDSCAPE VIEW this is for big tablets 7',8',9',10'
                 int orientation = getResources().getConfiguration().orientation;
                 if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());

                         marginParams.setMargins(-170, -130, -90, 0);
                         imageView.setLayoutParams(marginParams);
                         heightInPercentage = ((float) 3300 / 800) * 100;
                         layout.setBackground(imageView.getDrawable());

                 }
                int mLayoutWidth = (int) ((widthInPercentage * deviceWidth) / 100);
                int mLayoutHeight = (int) ((heightInPercentage * deviceHeight) / 100);
                imageView.getLayoutParams().height = mLayoutHeight;
                imageView.getLayoutParams().width = mLayoutWidth;
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    layout.setBackgroundDrawable(imageView.getDrawable());
                } else {
                    layout.setBackground(imageView.getDrawable());

                }

        }
        else {
            layout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));
        }

    }//end of onCreate

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position) {
                case 0:
                    fragment =  new ScreenSlidePageFragment();
                    break;
                case 1:
                    fragment =  new ScreenSlidePageFragment2();
                    break;
                case 2:
                    fragment = new ScreenSlidePageFragment3();
                    break;
                case 3:
                    fragment = new ScreenSlidePageFragment4();
                    break;
                case 4:
                    fragment = new ScreenSlidePageFragment5();
                    break;
            }
            return fragment;

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }*/

   /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;

        }
                return super.onOptionsItemSelected(item);
      }


}
