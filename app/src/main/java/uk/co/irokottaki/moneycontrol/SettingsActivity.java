package uk.co.irokottaki.moneycontrol;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
  // after this point Is MY CODE
public class SettingsActivity extends PreferenceActivity   {

    private static final int PICK_IMAGE =1 ;
    View view;
    RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    Bitmap bitmap;
    ListPreference list = null;
    private static ContentResolver cr = null;
    private static Context context;
    private static ListView lv;
    public static SharedPreferences prefs;
    private static SettingsActivity activity;
    private static String PACKAGE_NAME;
    private FragmentActivity myContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        SettingsActivity.context = getApplicationContext();
        PACKAGE_NAME= getApplicationContext().getPackageName();
        activity = SettingsActivity.this;
        lv= getListView();
        cr = getContentResolver();
        setContentView(R.layout.activity_settings);

        layout= (RelativeLayout) findViewById(R.id.settingsView);

        prefs= getSharedPreferences("Preferences", SettingsActivity.MODE_PRIVATE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);//initialize preference manager

        // get the color from preferences and apply it to the activity
        if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")) {

           layout.setBackgroundResource(R.drawable.backgroundimg);//need to call it somewhere to get the wood style displayed
        }

        else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")){

            SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            String filePath = prefers.getString("GalleryImage", "#00000000");
            final BitmapFactory.Options options = new BitmapFactory.Options();

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            Drawable dr = new BitmapDrawable(getResources(), bitmap);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(dr);
            } else {
                     layout.setBackground(dr);
                 }
        } else {
            layout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));
        }


    }//end of onCreate


    public static Context getAppContext() {
        return SettingsActivity.context;
    }

    public  static SettingsActivity getSettingsActivity() {
        return activity;
    }

    public static ListView getTheListView () {
        return SettingsActivity.lv;
    }

    public static SharedPreferences getTheSharedPrefs() {
        return SettingsActivity.prefs;
    }

    public static ContentResolver getTheContentResolver() {
        return cr;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
      /*  Intent colorIntent = new Intent(SettingsActivity.this, MainActivity.class);
        colorIntent.putExtra("colorSelected",colorChoosed);
        startActivity(colorIntent);//send the progress bar value to main activity*/
        return true;
    }

    public  void askPermissions () {

        if (ContextCompat.checkSelfPermission(SettingsActivity.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getSettingsActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            ActivityCompat.requestPermissions(getSettingsActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    public static void run (ServiceConnection serviceConn) {
        // Binding to IInAppBillingService
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getAppContext().bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        MyPreferenceFragment help= new MyPreferenceFragment();
        IabHelper mHelper= help.getmHelper();
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.i("IN APP BILLING", "onActivityResult handled by IABUtil.");
        }

    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        ListPreference list = null;
        static Bitmap bitmap;
        private static Drawable bitmapDrawable;
        boolean userIsPro;

        IInAppBillingService mService;
        Bundle querySkus;
        String sku= "pro_upgrade_2016.";
        public static IabHelper mHelper;
        String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqcYpXYA3pWCTMjOYJNNC70rNhXmbwxI5i4sGCtmZWN+eVFvrvtBtlwm8Wxwab8wf4CyLUxthccmgSd2Wmb6lHYVHG9/F7VSn+u3f9tnu8x+Oh30fyiSr4Wdesz0yfTwflVipA4wNwcEjxJoO0t8CCEyswQZcAzLAMzkodlMVwcdWx0kJ39qJxxuT8LWFlqwDpUSlLm6sPr+XmbD/vhfmd1h+qNQTteVte2Q5vVLSAk1/hCsqLCzrDp0BJ30w4f0nzEBn3g/7KIn3KQQp+6JE+xJanavahcvAU//PTDmy8t/bYxiFtn8kquBCL9xcHa/2Nw8PTEhzeWx3hCRUAugruwIDAQAB";
        private final String TAG="In App blling";
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;


        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                Log.e("Service In app:", "Service Connected");
            }
        };

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences sharedPrefs= SettingsActivity.getTheSharedPrefs();
            userIsPro = sharedPrefs.getBoolean("userIsPro", false);//retrieve the boolean for the pro user

            mHelper = new IabHelper(getSettingsActivity(), base64EncodedPublicKey);
            mHelper.enableDebugLogging(true, TAG);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d("In app Billing", "Problem setting up In-app Billing: " + result);
                        return;
                    }
                    // Hooray, IAB is fully set up!
                    if (mHelper == null)
                        return;
                    Log.d(TAG, "Setup successful. Querying inventory.");
                }
            });

            SettingsActivity callService = new SettingsActivity();
            callService.run(mServiceConn);

            //Query a purchase
            final ArrayList<String> skuList=new ArrayList<String>();
            skuList.add(sku);
            querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

            final IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (result.isFailure()) {
                        Log.d(TAG, "Querying Inventory Failed: " + result);
                        return;
                    }
                    Log.d(TAG, "Title: " + inv.getSkuDetails(sku).getTitle());
                    Log.d(TAG, "Description: " + inv.getSkuDetails(sku).getDescription());
                    Log.d(TAG, "Price = " + inv.getSkuDetails(sku).getPrice());
                }
            };

            list = (ListPreference) findPreference("background_color");
            list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = list.findIndexOfValue(newValue.toString());
                    if (index == 3) {

                        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

                                if (result.isFailure()) {
                                    Toast.makeText(getAppContext(), "Purchase:Error occured during purchase " +result, Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (purchase.getSku().equals(sku)) {
                                    consumeItem();

                                }
                            }
                        };

                        try {
                            Bundle skuDetails = mService.getSkuDetails(3, SettingsActivity.PACKAGE_NAME, "inapp", querySkus);
                            int response = skuDetails.getInt("RESPONSE_CODE");
                            if (response == 0) {
                                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                                for (String thisResponse : responseList) {
                                    JSONObject object = null;
                                    try {
                                        object = new JSONObject(thisResponse);
                                        String sku = object.getString("productId");
                                        String price = object.getString("price");
                                        if (sku.equals("premiumUpgrade")) {
                                            String mPremiumUpgradePrice = price;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        //check if the user has purchased the PRO version
                        mHelper.queryInventoryAsync(true, skuList, mQueryFinishedListener);
                        //check if user is Pro by retrieving it from the Shared Preferences
                        if (userIsPro) {
                            //the user has PRO version so proceed to change color
                            SettingsActivity permit = new SettingsActivity();
                            permit.askPermissions();
                            try {
                                //Intent intent = new Intent();
                                //intent.setType("image/*");//if i want to change the directory that it looks for images
                                //intent.setAction(Intent.ACTION_GET_CONTENT);
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                            } catch (Exception e) {

                                Log.e(e.getClass().getName(), e.getMessage(), e);
                            }
                        }
                        // if the userIsPro is false the user has not purchased the PRO version
                        else {
                            //so prompt to buy PRO version
                            if (mHelper != null) mHelper.flagEndAsync();
                            {
                                mHelper.launchPurchaseFlow(getSettingsActivity(), sku, 10001, mPurchaseFinishedListener);
                            }
                        }
                    }
                    return true;
                }
            });

        }//end of onCreate


        public static IabHelper getmHelper () {
            return mHelper;
        }

        public void consumeItem (){

            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        }

        IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                if (result.isFailure()) {
                    Toast.makeText(getAppContext(), "Error occured during purchase "+result, Toast.LENGTH_SHORT).show();
                } else {
                    mHelper.consumeAsync(inventory.getPurchase(sku), mConsumeFinishedListener);
                }
            }
        };

        IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {

                if (result.isSuccess()) {
                    Toast.makeText(getAppContext(), "Congratulations you upgraded to PRO", Toast.LENGTH_SHORT).show();
                    SharedPreferences sp = SettingsActivity.getTheSharedPrefs();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("userIsPro", true);//store the boolean userIsPro in Preferences, true since the user just purchased PRO
                    editor.putBoolean("adsDisabled", true);//store the boolean for the ads in Preferences, true since the user is PRO
                    editor.commit();
                } else {
                    Toast.makeText(getAppContext(), "Error occured during purchase "+ result, Toast.LENGTH_SHORT).show();
                }
            }
        };

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
            else {
                Log.i(TAG, "onActivityResult handled by IABUtil.");
            }

            if (requestCode == 1001) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getAppContext(), AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("Information")
                                .setMessage("You have bought the " + sku + ". Excellent choice, adventurer!");
                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getAppContext(), AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("Information")
                                .setMessage("Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }
            }

            if (resultCode == RESULT_OK){

                    Bitmap bitmap;

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = SettingsActivity.getTheContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(filePath,options);

                    SharedPreferences sp = SettingsActivity.getTheSharedPrefs();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("GalleryImage", filePath);
                    editor.commit();

            }

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mHelper != null)
            {
                mHelper.dispose();
                mHelper = null;
            }
            if (mService != null) {
                getAppContext().unbindService(mServiceConn);
            }
        }


        public static Drawable getBitmapDrawable() {
            return MyPreferenceFragment.bitmapDrawable;
        }

    }// end of static Fragment

    // Old way of storing the images picked from a user gallery. This goes to change background method
    //String encoded= prefers.getString("GalleryImage", "#00000000");
    //convert Base64 back to bitmap object
    //byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
    //Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length,options);
    // And this goes on the onActivityResult method.
    /*bitmap = BitmapFactory.decodeStream(SettingsActivity.getAppContext().getContentResolver().openInputStream(targetUri));

                    bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    SettingsActivity view = new SettingsActivity();
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        SettingsActivity.getTheListView().setBackgroundDrawable(bitmapDrawable);
                    }
                    else {
                        SettingsActivity.getTheListView().setBackground(bitmapDrawable);
                    }*/
    //store bitmap in SharedPreferences as Base64 string
                   /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                    //put the string to preferences
                    SharedPreferences sp = SettingsActivity.getTheSharedPrefs();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("GalleryImage", encoded);
                    editor.commit();*/
    // END OF MY CODE

    /**
     * {@inheritDoc}
     */
   // @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    /*@Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)*/
    /*public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }*/

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }


    }
