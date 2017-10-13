package uk.co.irokottaki.moneycontrol;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import com.millennialmedia.MMSDK;
import com.millennialmedia.InlineAd;
import com.millennialmedia.MMException;


public class ReportActivity extends AppCompatActivity {
    private Spinner monthItems;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList <String>  monthsAddedToSpinner;
    private TextView reportView;
    private static String fileLine, fileLineOct15,fileLineNov15, fileLineDec15,fileLineJan16,fileLineFeb16,fileLineMar16,fileLineApr16,
            fileLineMay16,fileLineJun16,fileLineJul16,fileLineAug16,fileLineSep16,fileLineOct16,fileLineNov16,fileLineDec16,fileLineJan,fileLineFeb,fileLineMar,fileLineApr,
            fileLineMay,fileLineJun,fileLineJul,fileLineAug,fileLineSep,fileLineOct,fileLineNov,fileLineDec;
    StringBuilder shortLine;
    private Button annualChartButton, exportButton,importButton;
    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private int monthInt;
    private static String [] PERMISSIONS_STORAGE= {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    private String currentMonth;
    private final String TAG="Millenial Media";
    private InlineAd inlineAd;
    public boolean adsDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setTitle("Expenses Report");

        SharedPreferences sharedprefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        adsDisabled = sharedprefs.getBoolean("adsDisabled", false);//retrieve the boolean value for ads

        if (adsDisabled==false) {
            //this is for the ads Millenial Media
            MMSDK.initialize(this); // pass in current activity instance

            //Create the inline placement instance and set the listeners.
            try {
                // NOTE: The ad container argument passed to the createInstance call should be the
                // view container that the ad content will be injected into.

                FrameLayout adsLayout = new FrameLayout(this);
                adsLayout = (FrameLayout) findViewById(R.id.adReportView);
                inlineAd = InlineAd.createInstance("220118", (ViewGroup) adsLayout);

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

            } catch (MMException e) {
                Log.e(TAG, "Error creating inline ad", e);
                // abort loading ad
            }

            //Set the metadata and request the ad.
            if (inlineAd != null) {
                // set a refresh rate of 30 seconds that will be applied after the first request
                inlineAd.setRefreshInterval(30000);

                // The InlineAdMetadata instance is used to pass additional metadata to the server to
                // improve ad selection
                final InlineAd.InlineAdMetadata inlineAdMetadata = new InlineAd.InlineAdMetadata().
                        setAdSize(InlineAd.AdSize.BANNER);

                inlineAd.request(inlineAdMetadata);
            }
        }

        layout = (RelativeLayout) findViewById(R.id.reportView);

        //this is to change the background color of the activity when user changes it from settings

        if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")){
            layout.setBackgroundResource(R.drawable.backgroundimg);//need to call it somewhere to get the wood style displayed
        }

        //the case where the user has selected for a background on image from the device gallery
        else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")){

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
            ImageView imageView = (ImageView)this.findViewById(R.id.ImageView);

            imageView.setImageBitmap(bitmap);
            //adjust the width and height to the layout
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float widthInPercentage=0f;
                float heightInPercentage=0f;
                int deviceWidth = metrics.widthPixels;
                int deviceHeight = metrics.heightPixels;
                widthInPercentage = ((float) 625 / 600) * 100;
                heightInPercentage = ((float) 940 / 1024) * 100;

                //ONLY IN LANDSCAPE VIEW this is for big tablets 7',10'
                int orientation = getResources().getConfiguration().orientation;
                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());
                marginParams.setMargins(-170,-130,-90,0);
                imageView.setLayoutParams(marginParams);

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

        // Spinner with the months
        monthItems = (Spinner) findViewById(R.id.monthSpinner);

        /*String monthData [] = new String[] {"Month", "January","February","March","April",
                "May","June","July","August","September","October","November","December", "Total"};*/

        monthsAddedToSpinner = new ArrayList<String>();
        monthsAddedToSpinner.add("October 2015");
        monthsAddedToSpinner.add("November 2015");
        monthsAddedToSpinner.add("December 2015");
        monthsAddedToSpinner.add("January 2016");
        monthsAddedToSpinner.add("February 2016");
        monthsAddedToSpinner.add("March 2016");
        monthsAddedToSpinner.add("April 2016");
        monthsAddedToSpinner.add("May 2016");
        monthsAddedToSpinner.add("June 2016");
        monthsAddedToSpinner.add("July 2016");
        monthsAddedToSpinner.add("August 2016");
        monthsAddedToSpinner.add("September 2016");
        monthsAddedToSpinner.add("October 2016");
        monthsAddedToSpinner.add("November 2016");
        monthsAddedToSpinner.add("December 2016");
        monthsAddedToSpinner.add("January");
        monthsAddedToSpinner.add("February");
        monthsAddedToSpinner.add("March");
        monthsAddedToSpinner.add("April");
        monthsAddedToSpinner.add("May");
        monthsAddedToSpinner.add("June");
        monthsAddedToSpinner.add("July");
        monthsAddedToSpinner.add("August");
        monthsAddedToSpinner.add("September");
        monthsAddedToSpinner.add("October");
        monthsAddedToSpinner.add("November");
        monthsAddedToSpinner.add("December");
        monthsAddedToSpinner.add("Total");

        spinnerAdapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthsAddedToSpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthItems.setAdapter(spinnerAdapter);

        monthItems.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        spinnerAdapter,
                        R.layout.spinnernothingselected,
                        this));

        // TextView of the report
        reportView = new TextView(this);
        reportView = (TextView) findViewById(R.id.reportTextView);
        reportView.setMovementMethod(new ScrollingMovementMethod());
        reportView.setTypeface(Typeface.MONOSPACE);

        readTheFile();

        //Get the current month
        final Calendar calendar = Calendar.getInstance();//this gets the current month
        currentMonth = String.format(Locale.UK, "%tB", calendar);

        //convert month String to integer
        monthInt=0;
        try {
            java.util.Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(currentMonth);
            calendar.setTime(date);// here i convert the String month in an integer
            monthInt = calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //set the spinner to the current month
        monthItems.setSelection(monthInt+16);

        monthItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthItems = (Spinner) findViewById(R.id.monthSpinner);

                int getMonthSelection = monthItems.getSelectedItemPosition();

                switch (getMonthSelection) {

                    case 1:
                        formatReportArea(fileLineOct15);
                        reportView.setText(shortLine.toString());
                        break;
                    case 2:
                        formatReportArea(fileLineNov15);
                        reportView.setText(shortLine.toString());
                        break;
                    case 3:
                        formatReportArea(fileLineDec15);
                        reportView.setText(shortLine.toString());
                        break;
                    case 4:
                        formatReportArea(fileLineJan16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 5:
                        formatReportArea(fileLineFeb16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 6:
                        formatReportArea(fileLineMar16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 7:
                        formatReportArea(fileLineApr16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 8:
                        formatReportArea(fileLineMay16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 9:
                        formatReportArea(fileLineJun16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 10:
                        formatReportArea(fileLineJul16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 11:
                        formatReportArea(fileLineAug16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 12:
                        formatReportArea(fileLineSep16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 13:
                        formatReportArea(fileLineOct16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 14:
                        formatReportArea(fileLineNov16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 15:
                        formatReportArea(fileLineDec16);
                        reportView.setText(shortLine.toString());
                        break;
                    case 16:
                        formatReportArea(fileLineJan);
                        reportView.setText(shortLine.toString());
                        break;
                    case 17:
                        formatReportArea(fileLineFeb);
                        reportView.setText(shortLine.toString());
                        break;
                    case 18:
                        formatReportArea(fileLineMar);
                        reportView.setText(shortLine.toString());
                        break;
                    case 19:
                        formatReportArea(fileLineApr);
                        reportView.setText(shortLine.toString());
                        break;
                    case 20:
                        formatReportArea(fileLineMay);
                        reportView.setText(shortLine.toString());
                        break;
                    case 21:
                        formatReportArea(fileLineJun);
                        reportView.setText(shortLine.toString());
                        break;
                    case 22:
                        formatReportArea(fileLineJul);
                        reportView.setText(shortLine.toString());
                        break;
                    case 23:
                        formatReportArea(fileLineAug);
                        reportView.setText(shortLine.toString());
                        break;
                    case 24:
                        formatReportArea(fileLineSep);
                        reportView.setText(shortLine.toString());
                        break;
                    case 25:
                        formatReportArea(fileLineOct);
                        reportView.setText(shortLine.toString());
                        break;
                    case 26:
                        formatReportArea(fileLineNov);
                        reportView.setText(shortLine.toString());
                        break;
                    case 27:
                        formatReportArea(fileLineDec);
                        reportView.setText(shortLine.toString());
                        break;
                    default:
                        formatReportArea(fileLine);
                        reportView.setText(shortLine.toString());
                        break;
                }//end of switch
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(ReportActivity.this, "Spinner1: unselected", Toast.LENGTH_SHORT).show();
            }
        });

    }// end of create method

    private int getIndex(Spinner spinner, String myString){

        int index = 0;
        //monthItems = (Spinner) findViewById(R.id.monthSpinner);
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
        verifyStoragePermissions(ReportActivity.this);
        try {

            File root2= new File ("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");//get the directory of the file stored

            File dirAndFolder=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"ManageXPence");

            if (!dirAndFolder.exists()) {
                dirAndFolder.mkdir();
            }
            File file = new File(dirAndFolder, "expenses"+".txt");
            FileOutputStream f = new FileOutputStream(file);//pass the directory of the SD card with the name file in a FileOutputStream

            //InputStream in = c.getInputStream();//read the file expenses
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

    public void readFileFromSdCard () {
        verifyStoragePermissions(ReportActivity.this);
        StringBuilder myData=new StringBuilder();
        File myExternalFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/ManageXPence", "expenses.txt");
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.replaceAll("\\s+", " ").equals("Amount Description Date")){
                    myData.append(strLine+ "\r\n"+"\r\n");
                }//if it reads the first line i want to add two empty lines so we have the header enter, enter, and the third line is the first amount
                else {
                    myData.append(strLine);//if it reads a line with amount etc i don't add a new line because the write method adds
                    // a new line when the user adds a new expense.
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //now save the file from Sd card to internal storage data/data/....
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        String directory = contextWrapper.getFilesDir().getPath();
        File myInternalFile = new File(directory , "expenses.txt");

        try {
            FileOutputStream fos = new FileOutputStream(myInternalFile);
            fos.write(myData.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readTheFile() {
        fileLine = "";
        fileLineOct15="";
        fileLineNov15="";
        fileLineDec15="";
        fileLineJan16="";
        fileLineFeb16="";
        fileLineMar16="";
        fileLineApr16="";
        fileLineMay16="";
        fileLineJun16="";
        fileLineJul16="";
        fileLineAug16="";
        fileLineSep16="";
        fileLineOct16="";
        fileLineNov16= "";
        fileLineDec16="";
        fileLineJan="";
        fileLineFeb="";
        fileLineMar="";
        fileLineApr="";
        fileLineMay="";
        fileLineJun="";
        fileLineJul="";
        fileLineAug="";
        fileLineSep="";
        fileLineOct="";
        fileLineNov= "";
        fileLineDec="";


        String desc="";
        String date;

        try {
            InputStream inputStream = new FileInputStream("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            int lineIndex = 0;//this is to count the lines
            while ((line= br.readLine()) != null) {

                if (lineIndex==0) {
                    fileLine += line + "\n"+"\n";// get the contents of the file after the header
                }
                    if (line.startsWith("Amount")) {
                        fileLineOct15+= line+"\n"+"\n";
                        fileLineNov15+= line+"\n"+"\n";
                        fileLineDec15+= line+"\n"+"\n";

                        fileLineJan16+= line+"\n"+"\n";
                        fileLineFeb16+= line+"\n"+"\n";
                        fileLineMar16+= line+"\n"+"\n";
                        fileLineApr16+= line+"\n"+"\n";
                        fileLineMay16+= line+"\n"+"\n";
                        fileLineJun16+= line+"\n"+"\n";
                        fileLineJul16+= line+"\n"+"\n";
                        fileLineAug16+= line+"\n"+"\n";
                        fileLineSep16+= line+"\n"+"\n";
                        fileLineOct16+= line+"\n"+"\n";
                        fileLineNov16+= line+"\n"+"\n";
                        fileLineDec16+= line+"\n"+"\n";

                        fileLineJan+= line+"\n"+"\n";
                        fileLineFeb+= line+"\n"+"\n";
                        fileLineMar+= line+"\n"+"\n";
                        fileLineApr+= line+"\n"+"\n";
                        fileLineMay+= line+"\n"+"\n";
                        fileLineJun+= line+"\n"+"\n";
                        fileLineJul+= line+"\n"+"\n";
                        fileLineAug+= line+"\n"+"\n";
                        fileLineSep+= line+"\n"+"\n";
                        fileLineOct+= line+"\n"+"\n";
                        fileLineNov+= line+"\n"+"\n";
                        fileLineDec+= line+"\n"+"\n";

                }
                if (++lineIndex > 2 &&!line.equals("")) {
                    fileLine += line + "\n";
                    int index = line.lastIndexOf(" ");
                    desc = line.substring(line.indexOf(" "), index).trim();
                    date = line.substring(index, line.length());
                    String extractMonthFromDate = date.substring(date.indexOf("/")+1, date.lastIndexOf("/"));
                    String extractYearFromDate= date.substring(date.lastIndexOf("/")+1, date.length());

                    if (extractMonthFromDate.equals("10")&& extractYearFromDate.equals("2015")) {
                        fileLineOct15+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("11")&& extractYearFromDate.equals("2015")) {
                        fileLineNov15+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("12")&& extractYearFromDate.equals("2015")) {
                        fileLineDec15+= line+"\n";
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2016")) {
                        fileLineJan16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("02")&& extractYearFromDate.equals("2016")) {
                        fileLineFeb16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("03")&& extractYearFromDate.equals("2016")) {
                        fileLineMar16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("04")&& extractYearFromDate.equals("2016")) {
                        fileLineApr16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("05")&& extractYearFromDate.equals("2016")) {
                        fileLineMay16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("06")&& extractYearFromDate.equals("2016")) {
                        fileLineJun16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("07")&& extractYearFromDate.equals("2016")) {
                        fileLineJul16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("08")&& extractYearFromDate.equals("2016")) {
                        fileLineAug16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("09")&& extractYearFromDate.equals("2016")) {
                        fileLineSep16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("10")&& extractYearFromDate.equals("2016")) {
                        fileLineOct16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("11")&& extractYearFromDate.equals("2016")) {
                        fileLineNov16+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("12")&& extractYearFromDate.equals("2016")) {
                        fileLineDec16+= line+"\n";
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2017")) {
                        fileLineJan+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("02")&& extractYearFromDate.equals("2017")) {
                        fileLineFeb+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("03")&& extractYearFromDate.equals("2017")) {
                        fileLineMar+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("04")&& extractYearFromDate.equals("2017")) {
                        fileLineApr+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("05")&& extractYearFromDate.equals("2017")) {
                        fileLineMay+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("06")&& extractYearFromDate.equals("2017")) {
                        fileLineJun+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("07")&& extractYearFromDate.equals("2017")) {
                        fileLineJul+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("08")&& extractYearFromDate.equals("2017")) {
                        fileLineAug+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("09")&& extractYearFromDate.equals("2017")) {
                        fileLineSep+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("10")&& extractYearFromDate.equals("2017")) {
                        fileLineOct+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("11")&& extractYearFromDate.equals("2017")) {
                        fileLineNov+= line+"\n";
                    }
                    if (extractMonthFromDate.equals("12")&& extractYearFromDate.equals("2017")) {
                        fileLineDec+= line+"\n";
                    }

                }
            }// end of while
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void formatReportArea (String fileLine) {
        shortLine = null;
        shortLine = new StringBuilder();
        String formatStr = "%-8s%-15s%-10s";

        String [] lines = fileLine.split("\n");

        for (int i=0; i<lines.length; i++){
            if (i==0 ){
                String amount = "Amount";
                String shortDesc= "Description";
                String date = "Date";
                shortLine.append(String.format(formatStr,amount,shortDesc,date)).trimToSize();
                shortLine.append("\n");
            }
            else if (i>1 && !lines[i].equals("")){
                String amount = lines[i].substring(0,lines[i].indexOf(" "));
                String shortDesc= lines[i].substring(lines[i].indexOf(" "), lines[i].lastIndexOf(" ")).trim();
                String date = lines[i].substring(lines[i].lastIndexOf(" "), lines[i].length()).trim();
                shortLine.append(String.format(formatStr,amount,shortDesc,date)).trimToSize();
                shortLine.append("\n");
            }
            else  {
                shortLine.append(lines[i]);//this is to write the header Amount Description Date and a new line
                shortLine.append("\n");
            }
        }

    }

    @Override
    public Intent getSupportParentActivityIntent() {
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(this, MainActivity.class);

        bundle.putInt("Report", 1);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
