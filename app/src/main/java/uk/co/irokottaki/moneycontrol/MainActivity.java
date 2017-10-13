package uk.co.irokottaki.moneycontrol;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import android.preference.PreferenceManager;
import com.android.vending.billing.IInAppBillingService;
import com.dropbox.core.v2.users.FullAccount;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.millennialmedia.MMSDK;
import com.millennialmedia.InlineAd;
import com.millennialmedia.MMException;

import org.json.JSONException;
import org.json.JSONObject;
import static android.content.DialogInterface.*;

public class MainActivity extends AppCompatActivity  {

    Button saveToFile,addExpensesButton,reportButton,dateButton,editButton,chartButton,barChartButton,
            addDescrButton, budgetButton,annualChartButton, savingsButton, exportButton, importButton, circleButton;
    ImageButton infoDateRangeButton,backUpInfoButton;
    EditText expensesField,dateText;
    private Spinner descriptionsItem,addExpensesByDescription;
    static ArrayList<java.util.Date> dates = new ArrayList<>();
    ArrayList<Double> expenses = new ArrayList<Double>();
    private static String fileLine;
    private  int year_x, month_x, day_x, mLayoutHeight,mLayoutWidth, valueFromNumPicker1,valueFromNumPicker2;
    static final int DIALOG_ID= 0;
    private TextView incomeLabel, balanceLabel,numberPickerLabel1, numberPickerLabel2;
    ArrayAdapter<String> spinnerAdapter;
    private static ArrayList <String>  itemsAddedByUser;
    private String descriptionAddedByUser,ACCESS_TOKEN;
    EditText addedDescriptionField=null, incomeField;
    public ArrayList<String> allDescriptions ;//descriptions
    RelativeLayout layout;
    private static ArrayList<Float> arrayOfamountOct15, arrayOfamountNov15, arrayOfamountDec15,arrayOfamountJan16, arrayOfamountFeb16,
            arrayOfamountMar16, arrayOfamountApr16, arrayOfamountMay16, arrayOfamountJun16, arrayOfamountJul16, arrayOfamountAug16,
            arrayOfamountSep16, arrayOfamountOct16, arrayOfamountNov16, arrayOfamountDec16, arrayOfamountJan, arrayOfamountFeb,
            arrayOfamountMar, arrayOfamountApr, arrayOfamountMay, arrayOfamountJun, arrayOfamountJul, arrayOfamountAug, arrayOfamountSep,
            arrayOfamountOct, arrayOfamountNov, arrayOfamountDec;//all the amounts for the months
    protected PreferenceManager mPreferenceManager;
    private boolean budgetWarningEnabled,invalidToken;
    private LinkedHashSet<String> uniqueDescriptions;
    ArrayList <Float> uniqueAmounts;
    private float amountWithDuplicate;
    private double monthSum, balance, incomeDouble;
    static TabHost tabHost;
    private Handler handler = new Handler();
    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static String [] PERMISSIONS_STORAGE= {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ImageView imageView;
    private final String TAG="Millenial Media";
    private final String TAG2="In App Billing";
    private InlineAd inlineAd, inlineAd2, inlineAd3;
    IabHelper mHelper;
    String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqcYpXYA3pWCTMjOYJNNC70rNhXmbwxI5i4sGCtmZWN+eVFvrvtBtlwm8Wxwab8wf4CyLUxthccmgSd2Wmb6lHYVHG9/F7VSn+u3f9tnu8x+Oh30fyiSr4Wdesz0yfTwflVipA4wNwcEjxJoO0t8CCEyswQZcAzLAMzkodlMVwcdWx0kJ39qJxxuT8LWFlqwDpUSlLm6sPr+XmbD/vhfmd1h+qNQTteVte2Q5vVLSAk1/hCsqLCzrDp0BJ30w4f0nzEBn3g/7KIn3KQQp+6JE+xJanavahcvAU//PTDmy8t/bYxiFtn8kquBCL9xcHa/2Nw8PTEhzeWx3hCRUAugruwIDAQAB";
    public boolean adsDisabled;
    IInAppBillingService mService;
    Bundle querySkus;
    String sku= "pro_upgrade_2016.";
    public boolean userIsPro;;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    private HorizontalBarChart mChart;
    private FrameLayout stackedBarLayout;
    private static Float incomeForJan,incomeForFeb,incomeForMar,incomeForApr,incomeForMay,incomeForJun,incomeForJul,incomeForAug,
            incomeForSep,incomeForOct,incomeForNov,incomeForDec;
    private NumberPicker numberPicker1, numberPicker2;
    HashMap<String, Float> amountsRelatedToDays;
    private boolean isPaymentCircleSet;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar cal= Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        setContentView(R.layout.activity_main);
        setTitle("Home");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences sharedprefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        adsDisabled = sharedprefs.getBoolean("adsDisabled", false);//retrieve the boolean value for ads

        userIsPro = sharedprefs.getBoolean("userIsPro", false);//retrieve the boolean for the pro user
        //get the boolean value for payment circle
        isPaymentCircleSet = sharedprefs.getBoolean("isPaymentCircle", false);

        //get the int values from number pickers
        valueFromNumPicker1 = sharedprefs.getInt("valueFromNumPicker1", valueFromNumPicker1);
        valueFromNumPicker2 = sharedprefs.getInt("valueFromNumPicker2", valueFromNumPicker2);

        //onCoachMark();

        if (adsDisabled==false) {

            //this is for the ads Millenial Media
            MMSDK.initialize(this); // pass in current activity instance

            //Create the inline placement instance and set the listeners.
            try {
                // NOTE: The ad container argument passed to the createInstance call should be the
                // view container that the ad content will be injected into.
                FrameLayout adsLayout = new FrameLayout(this);
                adsLayout = (FrameLayout) findViewById(R.id.adView);
                FrameLayout adsLayout2 = (FrameLayout) findViewById(R.id.adView2);
                FrameLayout adsLayout3 = (FrameLayout) findViewById(R.id.adView3);
                inlineAd = InlineAd.createInstance("220118", (ViewGroup) adsLayout);
                inlineAd2 = InlineAd.createInstance("220118", (ViewGroup) adsLayout2);
                inlineAd3 = InlineAd.createInstance("220118", (ViewGroup) adsLayout3);
                inlineAd.setListener(new InlineAd.InlineListener() {
                    @Override
                    public void onRequestSucceeded(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad loaded.");
                    }

                    @Override
                    public void onRequestFailed(InlineAd inlineAd, InlineAd.InlineErrorStatus errorStatus) {

                        Log.i(TAG, errorStatus.toString());
                    }

                    @Override
                    public void onClicked(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad clicked.");
                    }

                    @Override
                    public void onResize(InlineAd inlineAd, int width, int height) {

                        Log.i(TAG, "Inline Ad starting resize.");
                    }

                    @Override
                    public void onResized(InlineAd inlineAd, int width, int height, boolean toOriginalSize) {

                        Log.i(TAG, "Inline Ad resized.");
                    }

                    @Override
                    public void onExpanded(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad expanded.");
                    }

                    @Override
                    public void onCollapsed(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad collapsed.");
                    }

                    @Override
                    public void onAdLeftApplication(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad left application.");
                    }
                });
                inlineAd2.setListener(new InlineAd.InlineListener() {
                    @Override
                    public void onRequestSucceeded(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad loaded.");
                    }

                    @Override
                    public void onRequestFailed(InlineAd inlineAd, InlineAd.InlineErrorStatus errorStatus) {

                        Log.i(TAG, errorStatus.toString());
                    }

                    @Override
                    public void onClicked(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad clicked.");
                    }

                    @Override
                    public void onResize(InlineAd inlineAd, int width, int height) {

                        Log.i(TAG, "Inline Ad starting resize.");
                    }

                    @Override
                    public void onResized(InlineAd inlineAd, int width, int height, boolean toOriginalSize) {

                        Log.i(TAG, "Inline Ad resized.");
                    }

                    @Override
                    public void onExpanded(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad expanded.");
                    }

                    @Override
                    public void onCollapsed(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad collapsed.");
                    }

                    @Override
                    public void onAdLeftApplication(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad left application.");
                    }
                });

                inlineAd3.setListener(new InlineAd.InlineListener() {
                    @Override
                    public void onRequestSucceeded(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad loaded.");
                    }

                    @Override
                    public void onRequestFailed(InlineAd inlineAd, InlineAd.InlineErrorStatus errorStatus) {

                        Log.i(TAG, errorStatus.toString());
                    }

                    @Override
                    public void onClicked(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad clicked.");
                    }

                    @Override
                    public void onResize(InlineAd inlineAd, int width, int height) {

                        Log.i(TAG, "Inline Ad starting resize.");
                    }

                    @Override
                    public void onResized(InlineAd inlineAd, int width, int height, boolean toOriginalSize) {

                        Log.i(TAG, "Inline Ad resized.");
                    }

                    @Override
                    public void onExpanded(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad expanded.");
                    }

                    @Override
                    public void onCollapsed(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad collapsed.");
                    }

                    @Override
                    public void onAdLeftApplication(InlineAd inlineAd) {

                        Log.i(TAG, "Inline Ad left application.");
                    }
                });

            } catch (MMException e) {
                Log.e(TAG, "Error creating inline ad", e);
                // abort loading ad
            }

            //Set the metadata and request the ad.
            if (inlineAd != null ||inlineAd2!=null || inlineAd3!=null) {
                // set a refresh rate of 30 seconds that will be applied after the first request
                inlineAd.setRefreshInterval(30000);
                inlineAd2.setRefreshInterval(30000);
                inlineAd3.setRefreshInterval(30000);
                // The InlineAdMetadata instance is used to pass additional metadata to the server to
                // improve ad selection
                final InlineAd.InlineAdMetadata inlineAdMetadata = new InlineAd.InlineAdMetadata().setAdSize(InlineAd.AdSize.BANNER);
                inlineAd.request(inlineAdMetadata);
                inlineAd2.request(inlineAdMetadata);
                inlineAd3.request(inlineAdMetadata);
            }
        }

        layout = (RelativeLayout) findViewById(R.id.mainActivityView);

        imageView = (ImageView)this.findViewById(R.id.ImageView);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")){
            imageView.setImageResource(android.R.color.transparent);//need to clear the background here because both gallery image and color are displayed
            layout.setBackgroundResource(R.drawable.backgroundimg);//need to call it somewhere to get the wood style displayed
        }
        //the case where the user has selected for a background on image from the device gallery
        else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")){

            SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            //retrieve the file path from preferences
            /*String filePath = prefers.getString("GalleryImage", "#00000000");
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            imageView.setImageBitmap(bitmap);

            final BitmapFactory.Options options = new BitmapFactory.Options();*/
            /*options.inJustDecodeBounds = true;
            int scale = 1;
            while(options.outWidth / scale / 2 >= 70 &&
                    options.outHeight / scale / 2 >= 70) {
                scale *= 2;
            }*/
            //  options.inSampleSize = 4;
            //retrieve the file path from preferences
            String filePath = prefers.getString("GalleryImage", "#00000000");
            final BitmapFactory.Options options = new BitmapFactory.Options();

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(bitmap);
            //adjust the width and height to the layout
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float widthInPercentage = 0f;
                float heightInPercentage = 0f;
                int deviceWidth = metrics.widthPixels;
                int deviceHeight = metrics.heightPixels;
                //this is the case for Nexus 5,6
                    if (deviceHeight > 1280) {
                        widthInPercentage = ((float) 505 / 480) * 100;
                        heightInPercentage = ((float) 800 / 800) * 100;
                    }
                //this is for big tablet 7', 10'
                     else {
                        widthInPercentage = ((float) 490 / 480) * 100;
                        heightInPercentage = ((float) 850 / 800) * 100;//was 800
                    }
                 //ONLY IN LANDSCAPE VIEW this is for big tablets 7',10'
                int orientation = getResources().getConfiguration().orientation;
                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());
                    //marginParams.setMargins(-125,-130,-90,0);
                    imageView.setLayoutParams(marginParams);
                    widthInPercentage = ((float) 495 / 480) * 100;
                    heightInPercentage = ((float) 1380 / 800) * 100;//height was 1200
                    layout.setBackground(imageView.getDrawable());
                }

                mLayoutWidth = (int) ((widthInPercentage * deviceWidth) / 100);
                mLayoutHeight = (int) ((heightInPercentage * deviceHeight) / 100);
                imageView.getLayoutParams().height = mLayoutHeight;
                imageView.getLayoutParams().width = mLayoutWidth;
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                     layout.setBackgroundDrawable(imageView.getDrawable());
                      }
                    else {
                        layout.setBackground(imageView.getDrawable());
                }
        }
        else
        {
            imageView.setImageResource(android.R.color.transparent);//need to clear the background here because both gallery image and color are displayed
            layout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));
        }

        //add the tabs
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.getTabWidget().setShowDividers(TabWidget.SHOW_DIVIDER_MIDDLE);
        //tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Add Expenses");
        tabSpec.setContent(R.id.addExpenses);
        tabSpec.setIndicator("Add Expenses");

        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Manage Expenses");
        tabSpec.setContent(R.id.ManageExpenses);
        tabSpec.setIndicator("Manage Expenses");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Chart Expenses");
        tabSpec.setContent(R.id.ChartExpenses);
        tabSpec.setIndicator("Chart Expenses");
        tabHost.addTab(tabSpec);
        TextView x = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        x.setTextSize(10);
        TextView y = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        y.setTextSize(10);
        TextView z = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        z.setTextSize(10);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //get the value from settings for the budget warnings
        budgetWarningEnabled = prefs.getBoolean("budgetWarnings", false);

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(MainActivity.this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

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
                Log.d(TAG2, "Setup successful. Querying inventory.");
            }
        });
        // Binding to IInAppBillingService
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //Query a purchase
        final ArrayList<String> skuList = new ArrayList<String>();
        skuList.add(sku);
        querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        // START OF FIRST TAB
        //Expenses Label
        TextView expensesLabel = new TextView(this);
        expensesLabel.setText("Expense Amount");

        //Expenses TextField
        expensesField = new EditText(this);
        expensesField = (EditText) findViewById(R.id.expenseText);

        //Description Label
        TextView descriptionLabel = new TextView(this);
        descriptionLabel.setText("Expense Description");

        //Description spinner
        descriptionsItem = (Spinner) findViewById(R.id.descriptionCombo);

        itemsAddedByUser = new ArrayList<String>();
        itemsAddedByUser.add("House Rent");
        itemsAddedByUser.add("Shopping");
        itemsAddedByUser.add("SuperMarket");
        itemsAddedByUser.add("Travel");
        itemsAddedByUser.add("Mortgage");
        itemsAddedByUser.add("Council Tax");
        itemsAddedByUser.add("House Bills");
        itemsAddedByUser.add("Entertainment");
        descriptionsItem = (Spinner) findViewById(R.id.descriptionCombo);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsAddedByUser);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        descriptionsItem.setAdapter(spinnerAdapter);

        descriptionsItem.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        spinnerAdapter,
                        R.layout.spinnernothingselected,
                        this));

        //Date Label
        TextView dateLabel = new TextView(this);
        dateLabel.setText("Date");

        //Button for the add description
        addDescrButton = new Button(this);
        addDescrButton.setText("Add");
        addDescrButton = (Button) findViewById(R.id.addDescButton);

        //Button for the date
        dateButton = new Button(this);
        dateButton.setText("Select Date");

        //Date textfield
        dateText = new EditText(this);
        dateText = (EditText) findViewById(R.id.dateText);

        //Save to File Button
        saveToFile = new Button(this);
        saveToFile.setText("Save To File");
        saveToFile.setBackgroundColor(Color.BLUE);
        saveToFile = (Button) findViewById(R.id.saveButton);

        //Income label, income field
        incomeLabel = new TextView(this);
        incomeLabel = (TextView) findViewById(R.id.incomeLabel);

        incomeField = new EditText(this);
        incomeField = (EditText) findViewById(R.id.incomeField);

        //get the stored income value from preferences and set it to the income field
        SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String incomeStored = prefers.getString("Income", "");
        incomeField.setText(incomeStored);

        //cicle Button
        circleButton = new Button (this);
        circleButton = (Button) findViewById(R.id.circleButton);

        //Balance Label
        balanceLabel = new TextView(this);
        balanceLabel = (TextView) findViewById(R.id.balanceView);

        //Edit Button
        editButton = new Button(this);
        editButton = (Button) findViewById(R.id.editButton);

        // END OF FIRST TAB

        //Add expenses button
        addExpensesButton = new Button(this);
        addExpensesButton = (Button) findViewById(R.id.addExpensesButton);

        //add expenses by description Spinner
        addExpensesByDescription = (Spinner) findViewById(R.id.addExpensesByDescSpinner);
        //set the adapter for that spinner so that it will take the values as added on the first spinner.
        addExpensesByDescription.setAdapter(spinnerAdapter);
        addExpensesByDescription.setAdapter(new NothingSelectedSpinnerAdapter(spinnerAdapter, R.layout.spinnernothingselected, this));

        //Information with example date range button
        infoDateRangeButton = new ImageButton(this);
        infoDateRangeButton = (ImageButton) findViewById(R.id.infoDateRange);

        //Button for the Budget Control
        budgetButton = new Button(this);
        budgetButton = (Button) findViewById(R.id.budgetButton);

        //Report button
        reportButton = new Button(this);
        reportButton = (Button) findViewById(R.id.reportButton);

        //Chart button
        chartButton = new Button(this);
        chartButton = (Button) findViewById(R.id.chartButton);

        //Bar Chart button
        barChartButton = new Button(this);
        barChartButton = (Button) findViewById(R.id.barChartbutton);

        //Annual Chart Button
        annualChartButton = new Button(this);
        annualChartButton = (Button) findViewById(R.id.annualChartButton);

        //Savings Chart Button
        savingsButton = new Button(this);
        savingsButton = (Button) findViewById(R.id.annualSavingsButton);

        // Back up Info button
        backUpInfoButton = new ImageButton(this);
        backUpInfoButton = (ImageButton) findViewById(R.id.backUpInfoButton);

        //Export Button
        exportButton = new Button(this);
        exportButton = (Button) findViewById(R.id.exportButton);

        //import button
        importButton = new Button(this);
        importButton = (Button) findViewById(R.id.importButton);

        showDialogOnButtonClick();
        try {
            readTheFile(0,0, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        readDescriptionsFile();


        saveToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WriteToFile();
            }
        });

        addDescrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInputDialog();
            }
        });

        circleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showNumberPickerDialogOnButtonClick();
            }
        });

        //call the methods to update balance and the graph
        if (isPaymentCircleSet) {
            processDateCircle();

        }
        else {
            incomeField.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    if (s.length() >= 2) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                processBalance();
                                showStackedBar();
                            }
                        }, 3000);//this is a delay of 3 seconds that starts when the user finished typing.
                    }
                }
            });
        }

        addExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddTheExpenses();
            }
        });

        infoDateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("Information")
                        .setMessage("You can enter a date range and the application will add the expenses you made for that period." +
                                "An example of a date range is 05/01/2016-15/01/2016." + "\n" + "Also if you select a description on the " +
                                "dropdown menu above the Add Expenses button, the application will sum all the expenses you made " +
                                "on that period only for the description you selected on the dropdown menu.");
                AlertDialog alert1;
                builder.setPositiveButton("Close",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert1 = builder.create();
                alert1.show();
            }
        });

        final IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isFailure()) {
                    Log.d(TAG2, "Querying Inventory Failed: " + result);
                    return;
                }
                Log.d(TAG2, "Title: " + inv.getSkuDetails(sku).getTitle());
                Log.d(TAG2, "Description: " + inv.getSkuDetails(sku).getDescription());
                Log.d(TAG2, "Price = " + inv.getSkuDetails(sku).getPrice());

            }
        };

        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        if (result.isFailure()) {
                            Toast.makeText(MainActivity.this, "Error occured during purchase " +result, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (purchase.getSku().equals(sku)) {
                            consumeItem();
                        }
                    }
                };
                try {
                     Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
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
                //check if user is Pro retrieve boolean from SharedPreferences
                if (userIsPro){
                    //the user has PRO version so start budget activity.
                    Intent intentBudget = new Intent(view.getContext(), BudgetActivity.class);
                    startActivity(intentBudget);

                }
                // if the userIsPro is false the user has not purchased the PRO version
                    else {
                         if (mHelper != null) mHelper.flagEndAsync();
                    {
                        //so prompt to buy PRO version
                        mHelper.launchPurchaseFlow(MainActivity.this, sku, 10001, mPurchaseFinishedListener);
                    }
                }
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(view.getContext(), ReportActivity.class);
                startActivity(intent1);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentEdit = new Intent(view.getContext(), EditActivity.class);
                startActivity(intentEdit);
            }
        });

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentEdit = new Intent(view.getContext(), ChartActivity.class);
                intentEdit.putExtra("Descriptions", allDescriptions);
                startActivity(intentEdit);
            }
        });

        barChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentEdit = new Intent(view.getContext(), HorizontalBarChartActivity.class);
                System.out.println("Bar Chart");
                startActivity(intentEdit);
            }
        });

        annualChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentAnnualChart = new Intent(view.getContext(), AnnualChartActivity.class);
                startActivity(intentAnnualChart);
            }
        });

        savingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentSavingsChart = new Intent(view.getContext(), AnnualSavingsActivity.class);
                startActivity(intentSavingsChart);
            }
        });

        backUpInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("Information")
                        .setMessage("You can save your expenses file to your device internal storage, edit it and then " +
                                "import it back in the application.\n 1. You can export it as a txt or as a pdf file. It " +
                                "is stored in your device Download folder. 2.You can edit the txt file but remember to \n" +
                                "keep the initial structure with the spaces. 3.When you finish editing save it, go back \n" +
                                "to the application and press Import. Import feature is only available on PRO version.");
                AlertDialog alert1;
                builder.setPositiveButton("Close",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert1 = builder.create();
                alert1.show();
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, exportButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.export_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        /*Toast.makeText( MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();*/
                        if (item.getTitle().equals("Export to DROPBOX")) {

                            ACCESS_TOKEN = retrieveAccessToken();
                            //checkValidToken(ACCESS_TOKEN);
                            getUserAccount();
                            if (!tokenExists() || invalidToken) {
                                //No token Back to LoginActivity
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            //Image URI received
                            File file = new File("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");//get the directory of the file stored
                            if (file != null && !invalidToken) {
                                //Initialize UploadTask
                                new UploadTask(DropboxClient.getClient(ACCESS_TOKEN), file, getApplicationContext()).execute();
                            }

                        }
                        if (item.getTitle().equals("Export TXT")) {
                            ExportExpensesFileToSdCard();
                            Toast.makeText(MainActivity.this, "You exported to Txt file", Toast.LENGTH_SHORT).show();
                        }
                        if (item.getTitle().equals("Export PDF")) {
                            ExportFileToPDF();
                            Toast.makeText(MainActivity.this, "You exported to Pdf file", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        if (result.isFailure()) {
                            Toast.makeText(MainActivity.this, "Error occured during purchase " + result, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (purchase.getSku().equals(sku)) {
                            consumeItem();
                        }
                    }
                };
                try {
                    Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
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
                //check if the user is Pro retrieve boolean from SharedPreferences
                 if (userIsPro) {
                    //the user has PRO version so make the import
                    readFileFromSdCard();
                }
                // if userIsPro is false the user has not purchased the PRO version
                else {
                    if (mHelper != null) mHelper.flagEndAsync();
                    {
                        //so prompt to buy PRO version
                        mHelper.launchPurchaseFlow(MainActivity.this, sku, 10001, mPurchaseFinishedListener);
                    }
                }
            }
        });

        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                float heightInPercentage = 0f;
                float widthInPercentage = 0f;
                DisplayMetrics metrics = getResources().getDisplayMetrics();

                int deviceWidth = metrics.widthPixels;
                //on Nexus 5 deviceHeight= 1776, on Nexus 10 deviceHeight=800, on nexus one 3.7' deviceHeight=480
                //on Nexus 6 deviceHeight= 2368, deviceWidth=1440
                int deviceHeight = metrics.heightPixels;
                // width:505/480, height: 889/800
                //first if is for devices like Nexus 5,6
                if ((deviceHeight > 1700 && deviceHeight < 1800) && (deviceWidth > 1050 && deviceWidth < 1100)
                        || (deviceHeight > 2300 && deviceHeight < 2400) && (deviceWidth > 1400 && deviceWidth < 1500)) {
                    widthInPercentage = ((float) 497 / 480) * 100;// 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
                    heightInPercentage = ((float) 800 / 800) * 100; // same procedure 300 is the height of the LinearLayout and i'm converting it into percentage
                }
                //this is the case of 7' table in portrait orientation deviceWidth=800 deviceHeight=1216
                else if ((deviceHeight > 1200 && deviceHeight < 1300) && (deviceWidth > 750 && deviceWidth < 850)) {
                    widthInPercentage = ((float) 490 / 480) * 100;
                    heightInPercentage = ((float) 850 / 800) * 100;
                }
                //this is for the case of 8' tablet. deviceHeight=1836, deviceWidth=1080
                else if ((deviceHeight > 1800 && deviceHeight < 1850) && (deviceWidth > 1050 && deviceWidth < 1100)) {
                    widthInPercentage = ((float) 490 / 480) * 100;
                    heightInPercentage = ((float) 800 / 800) * 100;
                }
                //this is the case of 9' tablet in vertical position. deviceWidth=1536, deviceHeight=1952
                else if ((deviceHeight > 1900 && deviceHeight < 2000) && (deviceWidth > 1500 && deviceWidth < 1550)) {
                    widthInPercentage = ((float) 488 / 480) * 100;
                    heightInPercentage = ((float) 800 / 800) * 100;
                } else {
                    widthInPercentage = ((float) 499 / 480) * 100;
                    heightInPercentage = ((float) 850 / 800) * 100;//was 800
                }

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    widthInPercentage = ((float) 490 / 480) * 100;
                    heightInPercentage = ((float) 1380 / 800) * 100;//height was 1200

                }
                mLayoutWidth = (int) ((widthInPercentage * deviceWidth) / 100);

                mLayoutHeight = (int) ((heightInPercentage * deviceHeight) / 100);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mLayoutWidth, mLayoutHeight);

                layout.setLayoutParams(layoutParams);
            }
        });
        // Get on the current tab when pressing the back button on the top of the activity
        final Intent intent = getIntent();

        if (intent.hasExtra("Chart")) {
            final int tab = intent.getExtras().getInt("Chart");
            switchToTab(tab); // switch to tab 3 from Chart Expenses
        } else if (intent.hasExtra("BarChart")) {
            final int tab = intent.getExtras().getInt("BarChart");
            switchToTab(tab); // switch to tab 3 from BarChart Expenses
        } else if (intent.hasExtra("AnnualChart")) {
            final int tab = intent.getExtras().getInt("AnnualChart");
            switchToTab(tab); // switch to tab 3 from Annual Chart Expenses
        } else if (intent.hasExtra("SavingsChart")) {
            final int tab = intent.getExtras().getInt("SavingsChart");
            switchToTab(tab);//switch to tab 3 savings chart
        } else if (intent.hasExtra("Budget")) {
            final int tab = intent.getExtras().getInt("Budget");
            switchToTab(tab); // switch to tab 2 from the Budget
        } else if (intent.hasExtra("Report")) {
            final int tab = intent.getExtras().getInt("Report");
            switchToTab(tab); // switch to tab 2 from the Report
        }

        //this is for the Notifications
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + AlarmManager.INTERVAL_DAY * 2, AlarmManager.INTERVAL_DAY * 2, broadcast);



    }// end of onCreate method


    private void switchToTab(int tab) {
        tabHost.setCurrentTab(tab);
    }

    public void consumeItem() {

        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                Toast.makeText(MainActivity.this, "Error occured during purchase " + result, Toast.LENGTH_SHORT).show();
            } else {
                mHelper.consumeAsync(inventory.getPurchase(sku), mConsumeFinishedListener);
            }
        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {

                    if (result.isSuccess()) {
                        Toast.makeText(MainActivity.this, "Congratulations you upgraded to PRO", Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("userIsPro", true);//store the boolean userIsPro in Preferences, true since the user just purchased PRO

                        adsDisabled =true;//disable ads
                        editor.putBoolean("adsDisabled", true);//store the boolean for the ads in Preferences, true since the user is PRO
                        editor.commit();
                    } else {
                        Toast.makeText(MainActivity.this, "Error occured during purchase "+ result, Toast.LENGTH_SHORT).show();
                    }
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = (ImageView) this.findViewById(R.id.ImageView);

        if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")) {
            imageView.setImageResource(android.R.color.transparent);//need to clear the background here because both gallery image and color are displayed
            layout.setBackgroundResource(R.drawable.backgroundimg);//need to call it somewhere to get the wood style displayed

        }
        //the case where the user has selected for a background on image from the device gallery
        else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")) {

            SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            //final BitmapFactory.Options options = new BitmapFactory.Options();
            /*options.inJustDecodeBounds = true;
            int scale = 1;
            while(options.outWidth / scale / 2 >= 70 &&
                    options.outHeight / scale / 2 >= 70) {
                scale *= 2;
            }*/
            //options.inSampleSize = 4;
            //retrieve the file path from preferences
            String filePath = prefers.getString("GalleryImage", "#00000000");
            final BitmapFactory.Options options = new BitmapFactory.Options();

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(bitmap);
            //adjust the width and height to the layout
            imageView.getLayoutParams().height = mLayoutHeight;
            imageView.getLayoutParams().width = mLayoutWidth;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(imageView.getDrawable());
            }
            else {
                layout.setBackground(imageView.getDrawable());
            }
        }

        else
        {
            imageView.setImageResource(android.R.color.transparent);//need to clear the background here because both gallery image and color are displayed
            layout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));

        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        budgetWarningEnabled = prefs.getBoolean("budgetWarnings", false);

        //get the stored income value from preferences and set it to the income field
        SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String incomeStored = prefers.getString("Income", "");
        incomeField.setText(incomeStored);

        SharedPreferences sharedprefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        adsDisabled = sharedprefs.getBoolean("adsDisabled", false);//retrieve the boolean value for ads

        userIsPro = sharedprefs.getBoolean("userIsPro", false);//retrieve the boolean for the pro user

        // Binding to IInAppBillingService
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("Information")
                            .setMessage("You have bought the " + sku + ". Excellent choice, adventurer!");
                } catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("Information")
                            .setMessage("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void readTheFile(int lastDayOfPreviousMonth, int lastDayOfCurrentMonth, String currentMonth) throws ParseException {
        fileLine = "";
        arrayOfamountOct15 = new ArrayList<Float>();
        arrayOfamountNov15 = new ArrayList<Float>();
        arrayOfamountDec15  = new ArrayList<Float>();

        arrayOfamountJan16 = new ArrayList<Float>();
        arrayOfamountFeb16 = new ArrayList<Float>();
        arrayOfamountMar16 = new ArrayList<Float>();
        arrayOfamountApr16 = new ArrayList<Float>();
        arrayOfamountMay16 = new ArrayList<Float>();
        arrayOfamountJun16 = new ArrayList<Float>();
        arrayOfamountJul16 = new ArrayList<Float>();
        arrayOfamountAug16 = new ArrayList<Float>();
        arrayOfamountSep16 = new ArrayList<Float>();
        arrayOfamountOct16 = new ArrayList<Float>();
        arrayOfamountNov16 = new ArrayList<Float>();
        arrayOfamountDec16 = new ArrayList<Float>();

        arrayOfamountJan = new ArrayList<Float>();
        arrayOfamountFeb = new ArrayList<Float>();
        arrayOfamountMar = new ArrayList<Float>();
        arrayOfamountApr = new ArrayList<Float>();
        arrayOfamountMay = new ArrayList<Float>();
        arrayOfamountJun = new ArrayList<Float>();
        arrayOfamountJul = new ArrayList<Float>();
        arrayOfamountAug = new ArrayList<Float>();
        arrayOfamountSep = new ArrayList<Float>();
        arrayOfamountOct = new ArrayList<Float>();
        arrayOfamountNov = new ArrayList<Float>();
        arrayOfamountDec = new ArrayList<Float>();
        amountsRelatedToDays = new HashMap<>();
        String amount = "";
        String date = "";

        try {
            InputStream inputStream = new FileInputStream("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            int lineIndex = 0;//this is to count the lines
            while ((line = br.readLine()) != null) {

                //fileLine +=line+"\n";// get the contents of the file after the header
                if (lineIndex == 0) {
                    fileLine = line + "\n" + "\n";// need to catch the space and write only one space after the header.
                }

                if (++lineIndex > 2 && !line.equals("") && !line.equals("r")) {
                    fileLine += line + "\n";
                    int index = line.lastIndexOf(" ");
                    amount = line.substring(0, line.indexOf(" "));
                    date = line.substring(index, line.length());
                    String extractDayFromDate = date.substring(0, date.indexOf("/"));
                    //convert String to int but first take the second character e.g. take 5 from 05
                    int extractDayFromDateInt = Integer.parseInt(extractDayFromDate.trim().replaceFirst("^0+(?!$)", ""));
                    String extractMonthFromDate = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
                    String extractYearFromDate = date.substring(date.lastIndexOf("/") + 1, date.length());

                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2015")) {
                        arrayOfamountOct15.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2015")) {
                        arrayOfamountNov15.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2015")) {
                        arrayOfamountDec15.add(Float.valueOf(amount));
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2016")) {
                        arrayOfamountJan16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2016")) {
                        arrayOfamountFeb16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2016")) {
                        arrayOfamountMar16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2016")) {
                        arrayOfamountApr16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2016")) {
                        arrayOfamountMay16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2016")) {
                        arrayOfamountJun16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2016")) {
                        arrayOfamountJul16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2016")) {
                        arrayOfamountAug16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2016")) {
                        arrayOfamountSep16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2016")) {
                        arrayOfamountOct16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2016")) {
                        arrayOfamountNov16.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2016")) {
                        arrayOfamountDec16.add(Float.valueOf(amount));
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2017")) {
                        arrayOfamountJan.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2017")) {
                        arrayOfamountFeb.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2017")) {
                        arrayOfamountMar.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2017")) {
                        arrayOfamountApr.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2017")) {
                        arrayOfamountMay.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2017")) {
                        arrayOfamountJun.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2017")) {
                        arrayOfamountJul.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2017")) {
                        arrayOfamountAug.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2017")) {
                        arrayOfamountSep.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2017")) {
                        arrayOfamountOct.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2017")) {
                        arrayOfamountNov.add(Float.valueOf(amount));
                    }
                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2017")) {
                        arrayOfamountDec.add(Float.valueOf(amount));
                    }

                    if (lastDayOfCurrentMonth!=0 && lastDayOfPreviousMonth!=0 && currentMonth!=null) {
                        final Calendar calendar = Calendar.getInstance();
                        java.util.Date currentDate = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(currentMonth);
                        calendar.setTime(currentDate);// here i convert the String month in an integer to be used on the switch-case
                        int monthInt = calendar.get(Calendar.MONTH)+1;
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);;
                       //the case of the current month
                        if (extractMonthFromDate.substring(1, 2).equals(String.valueOf(monthInt))&&
                                currentYear==Integer.parseInt(extractYearFromDate)&&
                                lastDayOfCurrentMonth > extractDayFromDateInt) {

                                if (amountsRelatedToDays.containsKey(extractDayFromDate)) {
                                    amountsRelatedToDays.put(extractDayFromDate, amountsRelatedToDays.get(extractDayFromDate)+Float.valueOf(amount));
                                } else {
                                    amountsRelatedToDays.put(extractDayFromDate, Float.valueOf(amount));
                                }
                        }
                        //the case of the previous month
                        else  if (extractMonthFromDate.substring(1, 2).equals(String.valueOf(monthInt-1)) &&
                                currentYear==Integer.parseInt(extractYearFromDate)&&
                                lastDayOfPreviousMonth <= extractDayFromDateInt) {

                                if (amountsRelatedToDays.containsKey(extractDayFromDate)) {
                                    amountsRelatedToDays.put(extractDayFromDate, amountsRelatedToDays.get(extractDayFromDate)+Float.valueOf(amount));
                                } else {
                                    amountsRelatedToDays.put(extractDayFromDate, Float.valueOf(amount));
                                }
                        }
                    }
                }
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void showInputDialog() {
        //this method handles the popup window to add/remove descriptions in the spinner.
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setView(promptView);

        addedDescriptionField = new EditText(this);
        addedDescriptionField = (EditText) promptView.findViewById(R.id.edittext);
        InputFilter[] FilterArray = new InputFilter[1];//this is to set a limit
        FilterArray[0] = new InputFilter.LengthFilter(14);//on characters entered by user
        addedDescriptionField.setFilters(FilterArray);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("ADD", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        descriptionAddedByUser = addedDescriptionField.getText().toString();
                        if (itemsAddedByUser.contains(descriptionAddedByUser)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                                    .setTitle("Description exists!")
                                    .setMessage("This description exists already on your list");
                            AlertDialog alert1;
                            builder.setPositiveButton("OK",
                                    new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alert1 = builder.create();
                            alert1.show();
                        } else if (!addedDescriptionField.getText().toString().equals("") && !addedDescriptionField.getText().toString().equals(" ")) {
                            spinnerAdapter.add(descriptionAddedByUser);
                            spinnerAdapter.notifyDataSetChanged();
                            writeDescriptionsToFile();//write new description in the file
                        }
                    }
                })
                .setNeutralButton("REMOVE",
                        new OnClickListener() {
                            String[] fixedDescriptions = new String[]{"House Rent", "Shopping", "SuperMarket", "Travel", "Mortgage", "Council Tax", "House Bills", "Entertainment"};

                            public void onClick(DialogInterface dialog, int id) {
                                if (Arrays.asList(fixedDescriptions).contains(addedDescriptionField.getText().toString())) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                                            .setTitle("Can not remove this Description!")
                                            .setMessage("This description is preinstalled in the " +
                                                    "application and for operating reasons is not recommended to delete it");
                                    AlertDialog alert1;
                                    builder.setPositiveButton("OK",
                                            new OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    alert1 = builder.create();
                                    alert1.show();
                                } else if (itemsAddedByUser.contains(addedDescriptionField.getText().toString())) {
                                    spinnerAdapter.remove(addedDescriptionField.getText().toString());
                                    deleteDescriptionFromFile();
                                }
                                //dialog.cancel();
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                                            .setTitle("Description does not exist!")
                                            .setMessage("This description does not exist on your list");
                                    AlertDialog alert1;
                                    builder.setPositiveButton("OK",
                                            new OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    alert1 = builder.create();
                                    alert1.show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ;
                            }
                        });
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void writeDescriptionsToFile() {
        try {
            PrintWriter out = new PrintWriter(openFileOutput("descriptions.txt", MODE_APPEND));
            String descriptionItem = descriptionAddedByUser;
            out.append(descriptionItem);
            out.write("\r\n");
            out.close();
            Toast.makeText(this, descriptionItem + " added in your list", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void readDescriptionsFile() {
        try {
            InputStream inputStream = openFileInput("descriptions.txt");
            Scanner in = new Scanner(inputStream);

            while (in.hasNextLine()) {
                String descriptionItem = in.nextLine();
                itemsAddedByUser.add(descriptionItem);
            }
            inputStream.close();
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    private void deleteDescriptionFromFile() {
        String checkedDescription = addedDescriptionField.getText().toString();
        try {

            FileInputStream fstream = this.openFileInput("descriptions.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            StringBuilder fileContent = new StringBuilder();
            while ((strLine = br.readLine()) != null) {
                if (!strLine.equals(checkedDescription)) {
                    fileContent.append(strLine);
                    fileContent.append("\r\n");
                }
            }
            PrintWriter out = new PrintWriter(openFileOutput("descriptions.txt", MODE_PRIVATE));
            out.write(fileContent.toString());
            out.close();
            Toast.makeText(this, checkedDescription + " removed from your list", Toast.LENGTH_LONG).show();
        } catch (Exception e) {            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void showDialogOnButtonClick() {
        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year_x, month_x, day_x);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = format.format(calendar.getTime());

            Toast.makeText(MainActivity.this, strDate, Toast.LENGTH_LONG).show();
            TextView dateTextField = (TextView) findViewById(R.id.dateText);
            dateTextField.setText(strDate);
        }
    };

    protected void showNumberPickerDialogOnButtonClick () {

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View npView = inflater.inflate(R.layout.number_picker_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        //number pickers
        numberPicker1 = (NumberPicker) npView.findViewById(R.id.numberPicker1);
        numberPicker1.setMaxValue(31);
        numberPicker1.setMinValue(1);
        numberPicker1.setWrapSelectorWheel(true);

        numberPicker2 = (NumberPicker) npView.findViewById(R.id.numberPicker2);
        numberPicker2.setMaxValue(31);
        numberPicker2.setMinValue(1);
        numberPicker2.setWrapSelectorWheel(true);
        //Labels before the number pickers
        numberPickerLabel1 = new TextView(this);
        numberPickerLabel1 = (TextView) npView.findViewById(R.id.numberPickerLabel1);
        numberPickerLabel2 = new TextView(this);
        numberPickerLabel2 = (TextView) npView.findViewById(R.id.numberPickerLabel2);

        if ((valueFromNumPicker1!=0 && valueFromNumPicker2!=0)) {
            numberPicker1.setValue(valueFromNumPicker1);
            numberPicker2.setValue(valueFromNumPicker2);
        }
        //Reset Button
        //resetCircleButton = (Button) npView.findViewById(R.id.resetPaymentCircleButton);

        //if the user has set number picker values then set them to appear when user presses the circle button

        alertDialogBuilder.setCancelable(false)
                .setTitle("Set your payment circle")
                .setMessage("Set your salary date" + "\n" +
                        "It is applied only in the Balance shown" +"\n"+
                        "Change back to calendar month with the Reset button")
                .setView(npView)
                .setPositiveButton("SET",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                valueFromNumPicker1 = numberPicker1.getValue();
                                valueFromNumPicker2 = numberPicker2.getValue();
                                processDateCircle ();

                            // store in preferences the boolean to set the circle and the values from the number pickers.
                                SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefers.edit();
                                editor.putBoolean("isPaymentCircle",true);
                                editor.putInt("valueFromNumPicker1", valueFromNumPicker1);
                                editor.putInt("valueFromNumPicker2", valueFromNumPicker2);
                                editor.commit();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            }
                        })
                .setNeutralButton("Reset",
                     new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                           isPaymentCircleSet = false;
                           valueFromNumPicker1 = 0;
                           valueFromNumPicker2 = 0;

                           //processDateCircle();
                           // process again the balance
                           processBalance();
                           // redraw the graph with the balance
                           showStackedBar();

                           // store in preferences the boolean to set the circle and the values from the number pickers.
                           SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor = prefers.edit();
                           editor.putBoolean("isPaymentCircle",false);
                           editor.putInt("valueFromNumPicker1", valueFromNumPicker1);
                           editor.putInt("valueFromNumPicker2", valueFromNumPicker2);
                           editor.commit();
                           dialog.cancel();
                       }
                });
        AlertDialog alertBox = alertDialogBuilder.create();
        alertDialogBuilder.show();

    }

    private void processDateCircle() {

        System.out.println("First Date: "+valueFromNumPicker1+ " Second Date: "+valueFromNumPicker2);

        final Calendar calendar = Calendar.getInstance();//this gets the current month
        String currentMonth = String.format(Locale.UK, "%tB", calendar);

        try {
            readTheFile(valueFromNumPicker1, valueFromNumPicker2, currentMonth);
            //the user has clicked SET on the dialog
            isPaymentCircleSet = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // process again the balance
        processBalance();
        // redraw the graph with the balance
        showStackedBar();
    }

    private void processBalance() {

        if (incomeField != null && incomeField.getText().toString().matches("\\d+")) {
            String incomeValue = incomeField.getText().toString();
            incomeForJan = Float.parseFloat(incomeValue);//i initialize the incomes for every month to avoid null values
            incomeForFeb = Float.parseFloat(incomeValue);// returned to AnnualSavingsActivity. So if the user will not
            incomeForMar = Float.parseFloat(incomeValue);//change the income then i use the current that has been entered
            incomeForApr = Float.parseFloat(incomeValue);// if on a month the user changes the income then this will be
            incomeForMay = Float.parseFloat(incomeValue);// changed above on the if statements.
            incomeForJun = Float.parseFloat(incomeValue);
            incomeForJul = Float.parseFloat(incomeValue);
            incomeForAug = Float.parseFloat(incomeValue);
            incomeForSep = Float.parseFloat(incomeValue);
            incomeForOct = Float.parseFloat(incomeValue);
            incomeForNov = Float.parseFloat(incomeValue);
            incomeForDec = Float.parseFloat(incomeValue);
            Calendar c = Calendar.getInstance();
            String currentMonth = String.format(Locale.UK, "%tB", c);//get the current month
            int year = c.getInstance().get(Calendar.YEAR);

            if (currentMonth.equals("January") && year == 2017) {
                incomeForJan = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountJan);
            }
            if (currentMonth.equals("February") && year == 2017) {
                incomeForFeb = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountFeb);
            }
            if (currentMonth.equals("March") && year == 2017) {
                incomeForMar = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountMar);
            }
            if (currentMonth.equals("April") && year == 2017) {
                incomeForApr = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountApr);
            }
            if (currentMonth.equals("May") && year == 2017) {
                incomeForMay = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountMay);
            }
            if (currentMonth.equals("June") && year == 2017) {
                incomeForJun = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountJun);
            }
            if (currentMonth.equals("July") && year == 2017) {
                incomeForJul = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountJul);
            }
            if (currentMonth.equals("August") && year == 2017) {
                incomeForAug = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountAug);
            }
            if (currentMonth.equals("September") && year == 2017) {
                incomeForSep = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountSep);
            }
            if (currentMonth.equals("October") && year == 2017) {
                incomeForOct = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountOct);
            }
            if (currentMonth.equals("November") && year == 2017) {
                incomeForNov = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountNov);
            }
            if (currentMonth.equals("December") && year == 2017) {
                incomeForDec = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountDec);
            }
            if (currentMonth.equals("December") && year == 2016) {
                incomeForDec = Float.parseFloat(incomeValue);
                SumExpensesForBalance(arrayOfamountDec16);
            }
            // this is to avoid invalid double thrown on initial state where income is not added yet by the user
            incomeDouble = Double.valueOf(incomeValue);
            balance = incomeDouble - monthSum;
            DecimalFormat df = new DecimalFormat("#.0");
            balanceLabel.setText("Balance: " + df.format(balance));

            //store it in preferences
            SharedPreferences sp = getSharedPreferences("Preferences", MainActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Income", incomeValue);
            editor.commit();
        }
    }

    private void showStackedBar() {
        //this is for the stacked bar appeared after the balance
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChart = new HorizontalBarChart(this);
        stackedBarLayout = (FrameLayout) findViewById(R.id.stackedBar);
        stackedBarLayout.addView(mChart);
        mChart.setDrawGridBackground(false);

        mChart.setDescription("");

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(false);

        mChart.getAxisLeft().setEnabled(false);//hide the values up from the bar
        mChart.getAxisRight().setEnabled(false);//hide the values above the bar
        mChart.getLegend().setEnabled(false);//hide the legend

        mChart.getAxisRight().setDrawGridLines(false);
        //this is to set the chart's position to always (positive and negative balance)start from the same point
        mChart.getAxisLeft().setAxisMinValue(0f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawAxisLine(false);

        // IMPORTANT: When using negative values in stacked bars, always make sure the negative values are in the array first
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        yValues.add(new BarEntry(new float[]{(float) balance, (float) (incomeDouble - balance) } , 0));

        BarDataSet set = new BarDataSet(yValues, "");
        set.setBarSpacePercent(40f);//the height of the bar
        set.setDrawValues(false);//hide the y values appeared inside the bar

        if (balance < 0) {

            set.setColors(new int[]{Color.rgb(205, 0, 0) });//, Color.rgb(91, 57, 198)
        } else {

            set.setColors(new int[]{Color.rgb(0, 131, 0), Color.rgb(205, 0, 0)});//the colors green and red
        }

        String[] xVals = new String[]{""};

        BarData data = new BarData(xVals, set);
        mChart.setData(data);
        mChart.invalidate();
    }

    private double SumExpensesForBalance(ArrayList<Float> arrayOfamount) {
        Float monthSumFloat = 0f;
        //the user has set a payment circle
        if (isPaymentCircleSet) {
            for (Float amountsFloat : amountsRelatedToDays.values()) {
                monthSumFloat += amountsFloat;

            }
        }
        // the user has not set a payment circle or has reset it.
        else if (!isPaymentCircleSet) {
            for (int i = 0; i < arrayOfamount.size(); i++) {
                monthSumFloat += arrayOfamount.get(i);
            }
        }
        monthSum = monthSumFloat;
        return monthSum;
    }

    public void WriteToFile() {
        EditText ET = (EditText) findViewById(R.id.expenseText);
        EditText date = (EditText) findViewById(R.id.dateText);
        String amountField = ET.getText().toString();
        String dateField = date.getText().toString();

        if (amountField.equals("") || amountField.equals(" ") || descriptionsItem.getSelectedItem() == null || dateField.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Empty Field ")
                    .setMessage("Some of the fields are empty, fill them all and try again");
            AlertDialog alert1;
            builder.setPositiveButton("OK",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert1 = builder.create();
            alert1.show();
        } else {
            try {
                PrintWriter out = new PrintWriter(openFileOutput("expenses.txt", MODE_APPEND));
                ET = (EditText) findViewById(R.id.expenseText);
                EditText amount = (EditText) findViewById(R.id.expenseText);
                Spinner dateSpinner = (Spinner) findViewById(R.id.descriptionCombo);
                date = (EditText) findViewById(R.id.dateText);
                String amountText = amount.getText().toString();
                String descriptionText = dateSpinner.getSelectedItem().toString();
                String dateText = date.getText().toString();
                int length = 22;
                String formatStr = "%-8s%-15s%-10s";
                if (!fileLine.contains("Amount")) {
                    fileLine = "Amount Description Date";
                    out.printf("%-" + length + "s %s%n", "Amount  Description", "Date");//write the header
                    out.write("\r\n");//write two new lines
                    out.write("\r\n");
                }

                out.append(String.format(formatStr, amountText, descriptionText, dateText));//write the expense
                out.write("\r\n");//write a new line
                out.close();
                Toast.makeText(this, "The expense is saved in the file.", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            checkBudgetWarning();//since the expense is written in the file call the budget warning method
        }
    }

    public void checkBudgetWarning() {
        //call to retrieve amounts from months
        try {
            readTheFile(0,0, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int sum = 0;
        //Intent mIntent = getIntent();
        //get the budget value as stored in Preferences in Budget Activity
        SharedPreferences sp = getSharedPreferences("Preferences", BudgetActivity.MODE_PRIVATE);
        int progressValue = sp.getInt("budgetValue", 0);

        // i get the current month
        final Calendar calendar = Calendar.getInstance();//this gets the current month
        String currentMonth = String.format(Locale.UK, "%tB", calendar);

        //1. Check if the user has enabled this feature from settings.2.if yes find the expenses that have been added so far
        if (budgetWarningEnabled && progressValue > 0) {
            if (currentMonth.equals("January")) {
                for (int i = 0; i < arrayOfamountJan.size(); i++) {
                    sum += arrayOfamountJan.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("February")) {
                for (int i = 0; i < arrayOfamountFeb.size(); i++) {
                    sum += arrayOfamountFeb.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("March")) {
                for (int i = 0; i < arrayOfamountMar.size(); i++) {
                    sum += arrayOfamountMar.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("April")) {
                for (int i = 0; i < arrayOfamountApr.size(); i++) {
                    sum += arrayOfamountApr.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("May")) {
                for (int i = 0; i < arrayOfamountMay.size(); i++) {
                    sum += arrayOfamountMay.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("June")) {
                for (int i = 0; i < arrayOfamountJun.size(); i++) {
                    sum += arrayOfamountJun.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("July")) {
                for (int i = 0; i < arrayOfamountJul.size(); i++) {
                    sum += arrayOfamountJul.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("August")) {
                for (int i = 0; i < arrayOfamountAug.size(); i++) {
                    sum += arrayOfamountAug.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("September")) {
                for (int i = 0; i < arrayOfamountSep.size(); i++) {
                    sum += arrayOfamountSep.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("October")) {
                for (int i = 0; i < arrayOfamountOct.size(); i++) {
                    sum += arrayOfamountOct.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("November")) {
                for (int i = 0; i < arrayOfamountNov.size(); i++) {
                    sum += arrayOfamountNov.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
            if (currentMonth.equals("December")) {
                for (int i = 0; i < arrayOfamountDec.size(); i++) {
                    sum += arrayOfamountDec.get(i);
                }
                double percentWarning = (double) sum / (double) progressValue;
                // show the dialog window
                getDialogForBudgetWarning(percentWarning, MainActivity.this);
            }
        }
    }

    public static void getDialogForBudgetWarning(Double percentWarning, Context mContext) {
        if (percentWarning > 0.75 && percentWarning < 0.85) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Budget Warning!")
                    .setMessage("You have reached the 80% of your budget as set in Budget Control. \n\n" +
                            "*** Remember you can always turn off these notifications from the Settings.");
            AlertDialog alert1;
            builder.setPositiveButton("Close",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert1 = builder.create();
            alert1.show();
        } else if (percentWarning > 0.85 && percentWarning < 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Budget Warning!")
                    .setMessage("You have reached the 90% of your budget as set in Budget Control. \n\n" +
                            "*** Remember you can always turn off these notifications from the Settings.");
            AlertDialog alert1;
            builder.setPositiveButton("Close",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert1 = builder.create();
            alert1.show();
        } else if (percentWarning >= 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Budget Warning!")
                    .setMessage("You have exceeded the 100% of your budget as set in Budget Control. Too many expenses this month? You can increase the budget warning amount.\n\n" +
                            "*** Remember you can always turn off these notifications from the Settings.");
            AlertDialog alert1;
            builder.setPositiveButton("Close",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert1 = builder.create();
            alert1.show();
        }
    }

    public void AddTheExpenses() {

        getDaysBetweenDates();
        if (dates.isEmpty()) {
            return;
        }
        EditText datesField = (EditText) findViewById(R.id.dateFromTo);
        String datesFromTo = datesField.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        Date dateIntheFile = null;
        Set<Date> datesMatchedUserInput = new TreeSet<Date>();
        String expenseAmount = null;
        expenses.clear();
        uniqueDescriptions = new LinkedHashSet<String>();
        uniqueAmounts = new ArrayList<Float>();

        String desc = "";
        String date = "";
        try {
            InputStream inputStream = openFileInput("expenses.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Scanner in = new Scanner(inputStreamReader);//read the file
                int lineIndex = 0;//this is to count the lines
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (++lineIndex > 2 && !line.equals(""))//i need to read after the first line
                    {
                        //StringTokenizer st = new StringTokenizer(line);
                        expenseAmount = line.substring(0, line.indexOf(" "));//take the amount from the file
                        int index = line.lastIndexOf(" ");
                        desc = line.substring(line.indexOf(" "), index).trim();//take the description
                        date = line.substring(index, line.length());//take the date
                        try {
                            dateIntheFile = format.parse(date);//convert the date into Date

                        } catch (ParseException e) {

                            e.printStackTrace();
                        }
                        double firstDateAmountNumber = 0.0;
                        if (dates.contains(dateIntheFile)) {
                            datesMatchedUserInput.add(dateIntheFile);//add the Date in a list that will maintain them as the while loop checks all the dates.
                            String firstDateAmount = expenseAmount;//if the dates taken from the input are the same
                            firstDateAmountNumber = Double.parseDouble(firstDateAmount);//with those in the file
                            expenses.add(firstDateAmountNumber);//then look each line and find the amount given. Add the amount in the list of expenses.

                            //process the addition of expenses for a description
                            if (uniqueDescriptions.contains(desc)) {
                                int i = 0;
                                for (Iterator<String> s = uniqueDescriptions.iterator(); s.hasNext(); i++) {
                                    String descFound = s.next();
                                    if (desc.equals(descFound)) {
                                        amountWithDuplicate = uniqueAmounts.get(i) + Float.valueOf(expenseAmount);//add up the amounts if there are duplicates
                                        uniqueAmounts.set(i, amountWithDuplicate);
                                    }
                                }
                            } else {
                                uniqueDescriptions.add(desc);
                                uniqueAmounts.add(Float.valueOf(expenseAmount));
                            }
                        }
                    }
                }// end of while
                Collections.sort(dates);
                if (datesMatchedUserInput.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("No matches found")
                            .setMessage("The are no expenses in the dates you entered! Please try with different dates.");
                    builder.setPositiveButton("OK",
                            new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
                Date nextValue = null;
                for (Iterator<Date> it = datesMatchedUserInput.iterator(); it.hasNext(); ) {
                    nextValue = it.next();
                }
                if (dates.contains(nextValue) && addExpensesByDescription.getSelectedItemPosition() == 0) {
                    sumTheExpenses();//this sums up all the expenses for the date range. it is not inside the other if
                    //if (dates.contains(datesMatchedUserInput)) because it would pop up all the times the while loop is counting
                } else if (dates.contains(nextValue) && addExpensesByDescription.getSelectedItemPosition() > 0) {
                    String descSelected = addExpensesByDescription.getSelectedItem().toString();
                    sumTheExpensesByDescription(descSelected);
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public double sumTheExpensesByDescription(String desc) {
        EditText datesField = (EditText) findViewById(R.id.dateFromTo);
        String datesFromTo = datesField.getText().toString();
        Float sumFloat = 0f;
        if (desc.equals(addExpensesByDescription.getSelectedItem().toString())) {
            Iterator<String> itr = uniqueDescriptions.iterator();
            int i = 0;
            while (itr.hasNext()) {
                String descIterated = itr.next();
                if (desc.equals(descIterated)) {
                    sumFloat += uniqueAmounts.get(i);
                }
                i++;
            }
        }

        double sum = sumFloat;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(desc + " expenses for: " + datesFromTo)
                .setMessage("You have spent: " + String.format("%.2f", sum));
        AlertDialog alert1;
        builder.setPositiveButton("OK",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert1 = builder.create();
        alert1.show();

        return sum;
    }

    public double sumTheExpenses() {

        EditText datesField = (EditText) findViewById(R.id.dateFromTo);
        String datesFromTo = datesField.getText().toString();
        double sum = 0;
        for (int i = 0; i < expenses.size(); i++) {
            sum += expenses.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Expenses for: " + datesFromTo)
                .setMessage("You have spent: " + String.format("%.2f", sum));
        AlertDialog alert1;
        builder.setPositiveButton("OK",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert1 = builder.create();
        alert1.show();
        return sum;
    }

    public ArrayList<Date> getDaysBetweenDates() {
        EditText datesField = (EditText) findViewById(R.id.dateFromTo);
        String datesFromTo = datesField.getText().toString();
        dates = new ArrayList<>();//the arraylist where i store the dates

        if (datesFromTo.equals("") || datesFromTo.equals(null)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Empty Field!")
                    .setMessage("Add expenses field is empty.Please supply a date range");
            builder.setPositiveButton("OK",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder.create();
            alert1.show();
        } else if (!datesFromTo.matches("^[0-9].*") || !datesFromTo.contains("-")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Wrong Date!")
                    .setMessage("You entered words for a date or a single date. " +
                            "Please supply a correct date range in the format dd/MM/yyyy");
            builder.setPositiveButton("OK",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder.create();
            alert1.show();
        } else {
            String[] token = datesFromTo.split("-");//store dates splitted by -
            String firstdate = token[0];//take the first date entered by the user
            String lastdate = token[1];//take the second date entered by the user

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            Date startdate;
            startdate = null;

            if (!firstdate.matches("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$")
                    || !lastdate.matches("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("Wrong Format!")
                        .setMessage("Dates should be entered in the dd/MM/yyyy format.Please supply a correct date range");
                builder.setPositiveButton("OK",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert1 = builder.create();
                alert1.show();
            } else {
                try {
                    startdate = format.parse(firstdate);
                } catch (ParseException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("Wrong Format!")
                            .setMessage("Please supply a correct date range.");
                    builder.setPositiveButton("OK",
                            new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    e.printStackTrace();
                }
                Date enddate;
                enddate = null;
                try {
                    enddate = format.parse(lastdate);//convert the second date into Date

                } catch (ParseException e) {

                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startdate);

                while (calendar.getTime().getTime() <= enddate.getTime()) {
                    Date result = calendar.getTime();//take the date
                    dates.add(result);//add it to dates arraylist
                    calendar.add(Calendar.DATE, 1);
                }

            }// end of second else
        }
        return dates; //so dates has a range of dates. for instance if the user enters
        // 15/09/2015-17/09/2015 dates will store 15/09/2015 16/09/2015 17/09/2015
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[1]);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void ExportExpensesFileToSdCard() {
        verifyStoragePermissions(MainActivity.this);
        try {

            File root2 = new File("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");//get the directory of the file stored

            File dirAndFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");

            if (!dirAndFolder.exists()) {
                dirAndFolder.mkdir();
            }
            File file = new File(dirAndFolder, "expenses" + ".txt");
            FileOutputStream f = new FileOutputStream(file);//pass the directory of the SD card with the name file in a FileOutputStream

            InputStream inputStream = new FileInputStream(root2);

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = inputStream.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void readFileFromSdCard() {
        verifyStoragePermissions(MainActivity.this);
        String amount = "";
        String desc = "";
        String date = "";
        String formatStr = "%-7s %-15s %-10s";
        StringBuilder myData = new StringBuilder();
        File myExternalFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/", "expenses.txt");
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int lineIndex = 0;
            while ((strLine = br.readLine()) != null) {
                lineIndex++;
                if (strLine.replaceAll("\\s+", " ").contains("Amount Description Date")) {
                    myData.append(String.format(formatStr, "Amount", "Description", "Date") + "\n" + "\n");
                    //myData.append(strLine+ "\r\n"+"\r\n");
                }//if it reads the first line i want to add two empty lines so we have the header enter, enter, and the third line is the first amount
                //if the line is 2 so we are in the first expense
                else if (lineIndex > 2) {
                    //this is the case where the file is one very big line. Current line expense length is 33.
                    if (strLine.length() > 34 && !strLine.contains("Amount")) {
                        String[] fileLineArray = strLine.split("(?<=\\G.{33})");

                        for (int i = 0; i < fileLineArray.length; i++) {
                            String fileLineArrayOneSpace = fileLineArray[i].replaceAll("\\s+", " ");
                            amount = fileLineArrayOneSpace.substring(0, fileLineArray[i].indexOf(" "));
                            int index = fileLineArrayOneSpace.lastIndexOf(" ");
                            desc = fileLineArrayOneSpace.substring(fileLineArray[i].indexOf(" "), index).trim();
                            date = fileLineArrayOneSpace.substring(fileLineArrayOneSpace.lastIndexOf(" "), fileLineArrayOneSpace.length()).trim();
                            myData.append(String.format(formatStr, amount, desc, date + "\n"));
                        }
                    } //this is the case where the file has lines of length<34, probably 33. do the same as above
                    else if (strLine.length() <= 34 && !strLine.contains("Amount")) {
                        if (strLine.replaceAll("\\s+", " ").contains("2015") || (strLine.replaceAll("\\s+", " ").contains("2016"))) {
                            amount = strLine.replaceAll("\\s+", " ").substring(0, strLine.indexOf(" "));
                            int index = strLine.replaceAll("\\s+", " ").lastIndexOf(" ");
                            desc = strLine.replaceAll("\\s+", " ").substring(strLine.indexOf(" "), index).trim();
                            date = strLine.substring(strLine.lastIndexOf(" "), strLine.length()).trim();

                            myData.append(String.format(formatStr, amount, desc, date + "\n"));
                        }//all the other cases like if line=""
                        else {
                            myData.append(strLine);
                        }
                    }
                }
            }// end of While
                    /*int firstChar = myData.charAt(0);
                    int lastChar = myData.length();
                    if (Character.isWhitespace(myData.charAt(lastChar-1))) {
                        myData.substring(firstChar, lastChar);
                    }*/
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //if the file in the Download folder is empty or does not exist
        if (myData.toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("No expenses file!")
                    .setMessage("There is no expenses file in the Download folder. Please export expenses first.");
            builder.setPositiveButton("Close",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder.create();
            alert1.show();
        } else {
            //now save the file from Sd card to internal storage data/data/....
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            String directory = contextWrapper.getFilesDir().getPath();
            File myInternalFile = new File(directory, "expenses.txt");

            try {
                FileOutputStream fos = new FileOutputStream(myInternalFile);
                fos.write(myData.toString().getBytes());
                fos.close();
                Toast.makeText(MainActivity.this, "You imported the file", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String retrieveAccessToken() {
        //check if ACCESS_TOKEN is stored on previous app launches
        SharedPreferences prefs = getSharedPreferences("com.example.valdio.dropboxintegration", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        if (accessToken == null) {
            Log.d("AccessToken Status", "No token found");
            return null;
        } else {
            //accessToken already exists
            Log.d("AccessToken Status", "Token exists");
            return accessToken;
        }
    }

    protected void getUserAccount() {
        if (ACCESS_TOKEN == null)return;
        new UserAccountTask(DropboxClient.getClient(ACCESS_TOKEN), new UserAccountTask.TaskDelegate() {
            @Override
            public void onAccountReceived(FullAccount account) {
                //Print account's info
                Log.d("User", account.getEmail());
                Log.d("User", account.getName().getDisplayName());
                Log.d("User", account.getAccountType().name());
                //updateUI(account);
            }
            @Override
            public void onError(Exception error) {
                Log.d("User", "Error receiving account details.");
                invalidToken = true;
                System.out.println(invalidToken);
            }
        }).execute();
    }

    private boolean tokenExists() {

        SharedPreferences prefs = getSharedPreferences("com.example.valdio.dropboxintegration", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);

        return accessToken != null;
    }

    public void showHelp() {

        Intent intent1 = new Intent(this, HelpActivity.class);
        startActivity(intent1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.removeAds) {
            if (userIsPro) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Ads are Removed")
                    .setMessage("You have already purchased Pro version so ads have been removed.");
            builder.setPositiveButton("Close",
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder.create();
            alert1.show();
            }
            else {
             //prompt to buy PRO version
            mHelper.launchPurchaseFlow(MainActivity.this, sku, 10001, mPurchaseFinishedListener);
            }
        }

        if (id == R.id.action_help) {
            showHelp();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCoachMark(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.coach_mark);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        View masterView = dialog.findViewById(R.id.coach_mark_master_view);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void clear()//this method resets the shared preferences
    {
        SharedPreferences prefs = mPreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences prefs; // here you get your prefrences by either of two methods
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static Float getIncomeForJan() {
        return incomeForJan;
    }

    public static Float getIncomeForFeb() {
        return incomeForFeb;
    }

    public static Float getIncomeForMar() {
        return incomeForMar;
    }

    public static Float getIncomeForApr() {
        return incomeForApr;
    }

    public static Float getIncomeForMay() {
        return incomeForMay;
    }

    public static Float getIncomeForJun() {
        return incomeForJun;
    }

    public static Float getIncomeForJul() {
        return incomeForJul;
    }

    public static Float getIncomeForAug() {
        return incomeForAug;
    }

    public static Float getIncomeForSep() {
        return incomeForSep;
    }

    public static Float getIncomeForOct() {
        return incomeForOct;
    }

    public static Float getIncomeForNov() {
        return incomeForNov;
    }

    public static Float getIncomeForDec() {
        return incomeForDec;
    }

    public static ArrayList <String> getitemsAddedByUser() {
        return itemsAddedByUser;
    }

    public void ExportFileToPDF() {

        verifyStoragePermissions(MainActivity.this);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");

        if (!dir.exists()) {
            dir.mkdir();
        }

        String FILE = dir + "/expenses.pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            addTitlePage(document);
            addContent(document);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addContent(Document document) throws DocumentException {

        Paragraph preface = new Paragraph();

        try {
            InputStream inputStream = new FileInputStream("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = br.readLine()) != null) {

                if (!line.equals("")) {

                    PdfPTable table = new PdfPTable(3);
                    table.setWidthPercentage(50);
                    table.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                    Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);
                    f1.setColor(BaseColor.BLUE);

                    if (line.startsWith("Amount")) {
                        //this is for a case that the line might have spaces at the end
                        String lineTrimmed = line.trim();

                        // add a new font color for the header.
                        table.addCell(new Phrase(lineTrimmed.substring(0, lineTrimmed.indexOf(" ")), f1));
                        table.addCell(new Phrase(lineTrimmed.substring(lineTrimmed.indexOf(" "), lineTrimmed.lastIndexOf(" ")).trim(),f1));
                        table.addCell(new Phrase(lineTrimmed.substring(lineTrimmed.lastIndexOf(" "), lineTrimmed.length()).trim(),f1));

                    }
                    else {
                        // add all the columns of the file amount description and date in a table to be formatted correctly
                        table.addCell(line.substring(0, line.indexOf(" ")));
                        table.addCell(line.substring(line.indexOf(" "), line.lastIndexOf(" ")).trim());
                        table.addCell(line.substring(line.lastIndexOf(" "), line.length()).trim());
                    }
                    document.add(table);
                }

                addEmptyLine(preface, 1);

            }// end of while
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);

        // Lets write a big header
        preface.add(new Paragraph("Total Expenses", new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD)));

        addEmptyLine(preface, 1);
        document.add(preface);
    }


    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
