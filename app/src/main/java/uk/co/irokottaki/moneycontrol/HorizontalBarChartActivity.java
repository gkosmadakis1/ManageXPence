package uk.co.irokottaki.moneycontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

public class HorizontalBarChartActivity extends ActionBarActivity {

    private ArrayList <Float> arrayOfamounts;
    private static ArrayList<Float> arrayOfamountOct15,arrayOfamountNov15,arrayOfamountDec15,arrayOfamountJan16,arrayOfamountFeb16,
            arrayOfamountMar16,arrayOfamountApr16, arrayOfamountMay16,arrayOfamountJun16,arrayOfamountJul16,arrayOfamountAug16,arrayOfamountSep16,
            arrayOfamountOct16,arrayOfamountNov16,arrayOfamountDec16,arrayOfamountJan,arrayOfamountFeb,
            arrayOfamountMar,arrayOfamountApr, arrayOfamountMay,arrayOfamountJun,arrayOfamountJul,arrayOfamountAug,arrayOfamountSep,
            arrayOfamountOct,arrayOfamountNov,arrayOfamountDec;;//all the amounts for the months
    ArrayList<BarDataSet> dataSets;
    private LinkedHashSet<String> descriptionsNoDuplicate; //unique descriptions
    private static LinkedHashSet<String>  descriptionsNoDuplicateOct15,descriptionsNoDuplicateNov15,descriptionsNoDuplicateDec15,
            descriptionsNoDuplicateJan16,descriptionsNoDuplicateFeb16,descriptionsNoDuplicateMar16, descriptionsNoDuplicateApr16,
            descriptionsNoDuplicateMay16,descriptionsNoDuplicateJun16,descriptionsNoDuplicateJul16, descriptionsNoDuplicateAug16,
            descriptionsNoDuplicateSep16,descriptionsNoDuplicateOct16,descriptionsNoDuplicateNov16, descriptionsNoDuplicateDec16,
            descriptionsNoDuplicateJan,descriptionsNoDuplicateFeb,descriptionsNoDuplicateMar, descriptionsNoDuplicateApr,
            descriptionsNoDuplicateMay,descriptionsNoDuplicateJun,descriptionsNoDuplicateJul, descriptionsNoDuplicateAug,
            descriptionsNoDuplicateSep,descriptionsNoDuplicateOct,descriptionsNoDuplicateNov, descriptionsNoDuplicateDec;
    //all the descriptions for the months
    private float amountWithDuplicate;
    static Map<String, Float> storeAmounts = new HashMap<String, Float>();
    private ImageButton arrowLeft, arrowRight;
    HorizontalBarChart chart;
    BarData data;
    ArrayList<BarEntry> valueSet1;
    ArrayList<String> xAxis;
    BarDataSet barDataSet1;
    private int monthInt;
    private Switch toggleAmount;
    private boolean stateSwitchButton;
    private FrameLayout layout;
    private RelativeLayout barLayout;
    protected PreferenceManager mPreferenceManager;
    private TextView monthLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_bar_chart);
        setTitle("Bar Chart");
        readTheFile();

        layout = (FrameLayout) findViewById(R.id.chartLayout);

        barLayout= (RelativeLayout)findViewById(R.id.barChartView);

        //this is to change the background color of the activity when user changes it from settings
        if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")){
            barLayout.setBackgroundResource(R.drawable.backgroundimg);//need to call it somewhere to get the wood style displayed
        }

        //the case where the user has selected for a background on image from the device gallery
        else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")){

            SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            //retrieve the file path from preferences
            String filePath = prefers.getString("GalleryImage", "#00000000");
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            ImageView imageView = (ImageView)this.findViewById(R.id.ImageView);
            imageView.setImageBitmap(bitmap);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            //adjust the width and height to the layout
            //ONLY IN LANDSCAPE VIEW this is for big tablets 7',8',9',10'
            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());
                //marginParams.setMargins(-170,-130,-90,0);
                imageView.setLayoutParams(marginParams);
                layout.setBackground(imageView.getDrawable());
            }
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int deviceWidth = metrics.widthPixels;
                int deviceHeight = metrics.heightPixels;
                float widthInPercentage = ((float) 625 / 600) * 100;
                float heightInPercentage = ((float) 940 / 1024) * 100;
                int mLayoutWidth = (int) ((widthInPercentage * deviceWidth) / 100);
                int mLayoutHeight = (int) ((heightInPercentage * deviceHeight) / 100);
                imageView.getLayoutParams().height = mLayoutHeight;
                imageView.getLayoutParams().width = mLayoutWidth;
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    barLayout.setBackgroundDrawable(imageView.getDrawable());
                } else {
                    barLayout.setBackground(imageView.getDrawable());
                }
        }
        else {
            barLayout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));
        }

        arrowLeft = new ImageButton(this);
        arrowLeft=(ImageButton) findViewById(R.id.arrowLeft);

        monthLabel =(TextView) findViewById(R.id.month);

        arrowRight = new ImageButton(this);
        arrowRight=(ImageButton) findViewById(R.id.arrowRight);

        chart = (HorizontalBarChart) findViewById(R.id.chart);
        valueSet1 = new ArrayList<>();
        xAxis = new ArrayList<>();

        toggleAmount = (Switch) findViewById(R.id.switchButton);
        toggleAmount.setChecked(false);

        data = new BarData(xAxis, dataSets);// initialize data to avoid null pointers

        final Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String currentMonth = String.format(Locale.UK,"%tB",calendar);
        //month_date.format(calendar.getTime());
        monthLabel.setText(currentMonth);

        String getCurrentMonthDisplayed = monthLabel.getText().toString();
        monthInt=0;
        try {
            java.util.Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(getCurrentMonthDisplayed);
            calendar.setTime(date);// here i convert the String month in an integer to be used on the switch-case
            monthInt = calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // switch button listener
        toggleAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //depending on the month the user is i need to show the percentages for that month
                    if (!valueSet1.isEmpty()) {
                        calculatePercentagesAndModifyYAxis();
                    }
                    if (dataSets!=null) {
                        data.setValueFormatter(new PercentFormatter());//this adds % in the percentage
                    }
                    chart.invalidate();
                    stateSwitchButton=true;//this is to know that the switch is ON
                } else {
                    //here i revert back to the actual numbers
                    if (!valueSet1.isEmpty()) {
                        revertToNumbersAndModifyYAxis();
                    }
                    if (dataSets!=null) {
                        data.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                                return Float.toString((float) v);//this returns the value as a float as it used to be
                            }
                        });
                    }
                    chart.invalidate();
                    stateSwitchButton=false;//this is to know that the switch is OFF
                }
            }
        });


        barDataSet1 = new BarDataSet(valueSet1, "Expense");//i initialize it here in order to avoid getting null pointer inside the switch

        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthInt--;
                casesToShowExpensesForMonth();

            }
        });

        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthInt++;
                casesToShowExpensesForMonth();

            }
        });
        arrowRight.performClick();
        data = new BarData(xAxis, dataSets);

    }//end of create method


    //this method calculates percentages from the values on the arrayOfamounts
    private void calculatePercentages(ArrayList<Float> arrayOfamount) {

        int total=0;
        double percentage= 0;
            for (int i = 0; i < arrayOfamount.size(); i++) {
                total += Math.round(arrayOfamount.get(i));
            }
            for (int j = 0; j < arrayOfamount.size(); j++) {
                percentage = Math.round((arrayOfamount.get(j) * 100.0) / (double) total);
                float percentageInFloat = (float) percentage;
                //i replace all the items on the valueSet1 with percentages
                valueSet1.set(j, new BarEntry(percentageInFloat, j));
            }
    }
    //this method converts back to numbers after they were made percentages
    private  void revertPercentagesToNumbers(ArrayList<Float> arrayOfamount){

            for (int j = 0; j < arrayOfamount.size(); j++) {
                valueSet1.set(j, new BarEntry(arrayOfamount.get(j), j));//i replace all the items with actual numbers as they used to be
            }
    }

    private void modifyYAxis() {
        data = new BarData(xAxis, dataSets);
        if (dataSets!=null) {
            chart.setData(data);
            chart.animateXY(2000, 2000);
            chart.invalidate();
        }
    }

    private void modifyData () {
        if (valueSet1.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HorizontalBarChartActivity.this)
                    .setTitle("No data to present")
                    .setMessage("There are no expenses to display on this month.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1= builder.create();
            alert1.show();
            chart.clear();
        }
        else {
            barDataSet1 = new BarDataSet(valueSet1, "Expense");
            barDataSet1.setColor(Color.rgb(0, 153, 204));

            dataSets = new ArrayList<>();
            dataSets.add(barDataSet1);

            data = new BarData(xAxis, dataSets);
            if (stateSwitchButton) {
                calculatePercentagesAndModifyYAxis();

                data.setValueFormatter(new PercentFormatter());

            }
            else {
                revertToNumbersAndModifyYAxis();

                data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        return Float.toString((float) v);
                    }
                });
            }
            chart.setData(data);
            chart.setDescription("");
            chart.animateXY(2000, 2000);
            chart.invalidate();
        }
    }


    public void readTheFile() {
        String amount;
        String date;
        String desc="";
        arrayOfamounts = new ArrayList<Float>();
        arrayOfamountOct15= new ArrayList<Float>();
        arrayOfamountNov15= new ArrayList<Float>();
        arrayOfamountDec15= new ArrayList<Float>();
        arrayOfamountJan16 =new ArrayList<Float>();
        arrayOfamountFeb16 =new ArrayList<Float>();
        arrayOfamountMar16 =new ArrayList<Float>();
        arrayOfamountApr16 =new ArrayList<Float>();
        arrayOfamountMay16 =new ArrayList<Float>();
        arrayOfamountJun16 =new ArrayList<Float>();
        arrayOfamountJul16 =new ArrayList<Float>();
        arrayOfamountAug16 =new ArrayList<Float>();
        arrayOfamountSep16 =new ArrayList<Float>();
        arrayOfamountOct16 =new ArrayList<Float>();
        arrayOfamountNov16 =new ArrayList<Float>();
        arrayOfamountDec16 =new ArrayList<Float>();
        arrayOfamountJan =new ArrayList<Float>();
        arrayOfamountFeb =new ArrayList<Float>();
        arrayOfamountMar =new ArrayList<Float>();
        arrayOfamountApr =new ArrayList<Float>();
        arrayOfamountMay =new ArrayList<Float>();
        arrayOfamountJun =new ArrayList<Float>();
        arrayOfamountJul =new ArrayList<Float>();
        arrayOfamountAug =new ArrayList<Float>();
        arrayOfamountSep =new ArrayList<Float>();
        arrayOfamountOct =new ArrayList<Float>();
        arrayOfamountNov =new ArrayList<Float>();
        arrayOfamountDec =new ArrayList<Float>();

        descriptionsNoDuplicate = new LinkedHashSet<String>();
        descriptionsNoDuplicateOct15 = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateNov15 = new LinkedHashSet<String>();
        descriptionsNoDuplicateDec15 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJan16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateFeb16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateMar16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateApr16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateMay16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJun16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJul16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateAug16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateSep16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateOct16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateNov16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateDec16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJan = new LinkedHashSet<String>();
        descriptionsNoDuplicateFeb = new LinkedHashSet<String>();
        descriptionsNoDuplicateMar = new LinkedHashSet<String>();
        descriptionsNoDuplicateApr = new LinkedHashSet<String>();
        descriptionsNoDuplicateMay = new LinkedHashSet<String>();
        descriptionsNoDuplicateJun = new LinkedHashSet<String>();
        descriptionsNoDuplicateJul = new LinkedHashSet<String>();
        descriptionsNoDuplicateAug = new LinkedHashSet<String>();
        descriptionsNoDuplicateSep = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateOct = new LinkedHashSet<String>();
        descriptionsNoDuplicateNov = new LinkedHashSet<String>();
        descriptionsNoDuplicateDec = new LinkedHashSet<String>();


        try {
            InputStream inputStream = new FileInputStream("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            int lineIndex = 0;//this is to count the lines
            while ((line= br.readLine()) != null) {

                if (++lineIndex > 2 && !line.equals("")) {
                    int index = line.lastIndexOf(" ");

                    amount = line.substring(0, line.indexOf(" "));
                    desc = line.substring(line.indexOf(" "), index).trim();
                    date = line.substring(index, line.length());
                    String extractMonthFromDate = date.substring(date.indexOf("/")+1, date.lastIndexOf("/"));
                    String extractYearFromDate=date.substring(date.lastIndexOf("/")+1,date.length());

                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2015")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateOct15, desc, amount, arrayOfamountOct15);
                    }

                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2015")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateNov15, desc, amount, arrayOfamountNov15);
                    }

                    if (extractMonthFromDate.equals("12" ) && extractYearFromDate.equals("2015")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateDec15, desc, amount, arrayOfamountDec15);
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateJan16, desc, amount, arrayOfamountJan16);
                    }

                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateFeb16, desc, amount, arrayOfamountFeb16);
                    }

                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateMar16, desc, amount, arrayOfamountMar16);
                    }

                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateApr16, desc, amount, arrayOfamountApr16);
                    }

                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateMay16, desc, amount, arrayOfamountMay16);
                    }

                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateJun16, desc, amount, arrayOfamountJun16);
                    }

                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateJul16, desc, amount, arrayOfamountJul16);
                    }

                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateAug16, desc, amount, arrayOfamountAug16);
                    }

                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateSep16, desc, amount, arrayOfamountSep16);
                    }

                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateOct16, desc, amount, arrayOfamountOct16);
                    }

                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateNov16, desc, amount, arrayOfamountNov16);
                    }

                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2016")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateDec16, desc, amount, arrayOfamountDec16);
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateJan, desc, amount, arrayOfamountJan);
                    }

                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateFeb, desc, amount, arrayOfamountFeb);
                    }

                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateMar, desc, amount, arrayOfamountMar);
                    }

                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateApr, desc, amount, arrayOfamountApr);
                    }

                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateMay, desc, amount, arrayOfamountMay);
                    }

                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateJun, desc, amount, arrayOfamountJun);
                    }

                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateJul, desc, amount, arrayOfamountJul);
                    }

                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateAug, desc, amount, arrayOfamountAug);
                    }

                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateSep, desc, amount, arrayOfamountSep);
                    }

                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateOct, desc, amount, arrayOfamountOct);
                    }

                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateNov, desc, amount, arrayOfamountNov);
                    }

                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2017")){

                        addAmountsWithDuplicates(descriptionsNoDuplicateDec, desc, amount, arrayOfamountDec);
                    }

                    if (descriptionsNoDuplicate.contains(desc)) {
                        int i = 0;
                        for (Iterator<String> s = descriptionsNoDuplicate.iterator(); s.hasNext(); i++) {
                            String descFound = s.next();
                            if (desc.equals(descFound)) {
                                amount = line.substring(0, line.indexOf(" "));//get the amount
                                String item = line.substring(line.indexOf(" "), index).trim();//get the item
                                amountWithDuplicate =arrayOfamounts.get(i)+ Float.valueOf(amount);//add up the amounts if there are duplicates
                                arrayOfamounts.set(i, amountWithDuplicate);
                                storeAmounts.put(item, amountWithDuplicate);//store the amount to the item
                            }
                        }
                    }
                    else {
                        descriptionsNoDuplicate.add(desc);
                        arrayOfamounts.add(Float.valueOf(amount));
                        storeAmounts.put(desc, Float.valueOf(amount));
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

    @Override
    public Intent getSupportParentActivityIntent() { // getParentActivityIntent() if you are not using the Support Library
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(this, MainActivity.class);

        bundle.putInt("BarChart", 2); // Both constants are defined in your code
        intent.putExtras(bundle);

        return intent;
    }

    private void showExpensesForMonth(String month, LinkedHashSet descriptions, ArrayList<Float> arrayAmount) {

        monthLabel.setText(month);
        barDataSet1.clear();
        xAxis.clear();
        int z = 0;
        for (Iterator<String> s = descriptions.iterator(); s.hasNext(); z++) {
            String desc = s.next();
            valueSet1.add(new BarEntry(arrayAmount.get(z), z));
            xAxis.add(desc);
        }
        modifyData();
    }

    private void addAmountsWithDuplicates (LinkedHashSet descriptions, String desc, String amount, ArrayList<Float> arrayAmount) {
        if (descriptions.contains(desc)) {
            int i = 0;
            for (Iterator<String> s = descriptions.iterator(); s.hasNext(); i++) {
                String descFound = s.next();
                if (desc.equals(descFound)) {
                    amountWithDuplicate = arrayAmount.get(i) + Float.valueOf(amount);//add up the amounts if there are duplicates
                    arrayAmount.set(i, amountWithDuplicate);
                    storeAmounts.put(desc, amountWithDuplicate);//store the amount to the item
                }
            }
        } else {
            descriptions.add(desc);
            arrayAmount.add(Float.valueOf(amount));
            storeAmounts.put(desc, Float.valueOf(amount));
        }
    }

    private void calculatePercentagesAndModifyYAxis() {

        switch (monthInt) {
            case -14:
                calculatePercentages(arrayOfamountOct15);
                modifyYAxis();
                break;
            case -13:
                calculatePercentages(arrayOfamountNov15);
                modifyYAxis();
                break;
            case -12:
                calculatePercentages(arrayOfamountDec15);
                modifyYAxis();
                break;
            case -11:
                calculatePercentages(arrayOfamountJan16);
                modifyYAxis();
                break;
            case -10:
                calculatePercentages(arrayOfamountFeb16);
                modifyYAxis();
                break;
            case -9:
                calculatePercentages(arrayOfamountMar16);
                modifyYAxis();
                break;
            case -8:
                calculatePercentages(arrayOfamountApr16);
                modifyYAxis();
                break;
            case -7:
                calculatePercentages(arrayOfamountMay16);
                modifyYAxis();
                break;
            case -6:
                calculatePercentages(arrayOfamountJun16);
                modifyYAxis();
                break;
            case -5:
                calculatePercentages(arrayOfamountJul16);
                modifyYAxis();
                break;
            case -4:
                calculatePercentages(arrayOfamountAug16);
                modifyYAxis();
                break;
            case -3:
                calculatePercentages(arrayOfamountSep16);
                modifyYAxis();
                break;
            case -2:
                calculatePercentages(arrayOfamountOct16);
                modifyYAxis();
                break;
            case -1:
                calculatePercentages(arrayOfamountNov16);
                modifyYAxis();
                break;
            case 0:
                calculatePercentages(arrayOfamountDec16);
                modifyYAxis();
                break;
            case 1:
                calculatePercentages(arrayOfamountJan);
                modifyYAxis();
                break;
            case 2:
                calculatePercentages(arrayOfamountFeb);
                modifyYAxis();
                break;
            case 3:
                calculatePercentages(arrayOfamountMar);
                modifyYAxis();
                break;
            case 4:
                calculatePercentages(arrayOfamountApr);
                modifyYAxis();
                break;
            case 5:
                calculatePercentages(arrayOfamountMay);
                modifyYAxis();
                break;
            case 6:
                calculatePercentages(arrayOfamountJun);
                modifyYAxis();
                break;
            case 7:
                calculatePercentages(arrayOfamountJul);
                modifyYAxis();
                break;
            case 8:
                calculatePercentages(arrayOfamountAug);
                modifyYAxis();
                break;
            case 9:
                calculatePercentages(arrayOfamountSep);
                modifyYAxis();
                break;
            case 10:
                calculatePercentages(arrayOfamountOct);
                modifyYAxis();
                break;
            case 11:
                calculatePercentages(arrayOfamountNov);
                modifyYAxis();
                break;
            case 12:
                calculatePercentages(arrayOfamountDec);
                modifyYAxis();
                break;
        }
    }

    private void revertToNumbersAndModifyYAxis() {

        switch (monthInt) {
            case -14:
                revertPercentagesToNumbers(arrayOfamountOct15);
                modifyYAxis();
                break;
            case -13:
                revertPercentagesToNumbers(arrayOfamountNov15);
                modifyYAxis();
                break;
            case -12:
                revertPercentagesToNumbers(arrayOfamountDec15);
                modifyYAxis();
                break;
            case -11:
                revertPercentagesToNumbers(arrayOfamountJan16);
                modifyYAxis();
                break;
            case -10:
                revertPercentagesToNumbers(arrayOfamountFeb16);
                modifyYAxis();
                break;
            case -9:
                revertPercentagesToNumbers(arrayOfamountMar16);
                modifyYAxis();
                break;
            case -8:
                revertPercentagesToNumbers(arrayOfamountApr16);
                modifyYAxis();
                break;
            case -7:
                revertPercentagesToNumbers(arrayOfamountMay16);
                modifyYAxis();
                break;
            case -6:
                revertPercentagesToNumbers(arrayOfamountJun16);
                modifyYAxis();
                break;
            case -5:
                revertPercentagesToNumbers(arrayOfamountJul16);
                modifyYAxis();
                break;
            case -4:
                revertPercentagesToNumbers(arrayOfamountAug16);
                modifyYAxis();
                break;
            case -3:
                revertPercentagesToNumbers(arrayOfamountSep16);
                modifyYAxis();
                break;
            case -2:
                revertPercentagesToNumbers(arrayOfamountOct16);
                modifyYAxis();
                break;
            case -1:
                revertPercentagesToNumbers(arrayOfamountNov16);
                modifyYAxis();
                break;
            case 0:
                revertPercentagesToNumbers(arrayOfamountDec16);
                modifyYAxis();
                break;
            case 1:
                revertPercentagesToNumbers(arrayOfamountJan);
                modifyYAxis();
                break;
            case 2:
                revertPercentagesToNumbers(arrayOfamountFeb);
                modifyYAxis();
                break;
            case 3:
                revertPercentagesToNumbers(arrayOfamountMar);
                modifyYAxis();
                break;
            case 4:
                revertPercentagesToNumbers(arrayOfamountApr);
                modifyYAxis();
                break;
            case 5:
                revertPercentagesToNumbers(arrayOfamountMay);
                modifyYAxis();
                break;
            case 6:
                revertPercentagesToNumbers(arrayOfamountJun);
                modifyYAxis();
                break;
            case 7:
                revertPercentagesToNumbers(arrayOfamountJul);
                modifyYAxis();
                break;
            case 8:
                revertPercentagesToNumbers(arrayOfamountAug);
                modifyYAxis();
                break;
            case 9:
                revertPercentagesToNumbers(arrayOfamountSep);
                modifyYAxis();
                break;
            case 10:
                revertPercentagesToNumbers(arrayOfamountOct);
                modifyYAxis();
                break;
            case 11:
                revertPercentagesToNumbers(arrayOfamountNov);
                modifyYAxis();
                break;
            case 12:
                revertPercentagesToNumbers(arrayOfamountDec);
                modifyYAxis();
                break;
        }
    }

    private void casesToShowExpensesForMonth () {

        switch (monthInt) {
            case -14:
                showExpensesForMonth("October 2015", descriptionsNoDuplicateOct15, arrayOfamountOct15);
                break;
            case -13:
                showExpensesForMonth("November 2015", descriptionsNoDuplicateNov15, arrayOfamountNov15);
                break;
            case -12:
                showExpensesForMonth("December 2015", descriptionsNoDuplicateDec15,arrayOfamountDec15);
                break;
            case -11:
                showExpensesForMonth("January 2016", descriptionsNoDuplicateJan16, arrayOfamountJan16);
                break;
            case -10:
                showExpensesForMonth("February 2016", descriptionsNoDuplicateFeb16, arrayOfamountFeb16);
                break;
            case -9:
                showExpensesForMonth("March 2016", descriptionsNoDuplicateMar16, arrayOfamountMar16);
                break;
            case -8:
                showExpensesForMonth("April 2016", descriptionsNoDuplicateApr16, arrayOfamountApr16);
                break;
            case -7:
                showExpensesForMonth("May 2016", descriptionsNoDuplicateMay16, arrayOfamountMay16);
                break;
            case -6:
                showExpensesForMonth("June 2016", descriptionsNoDuplicateJun16, arrayOfamountJun16);
                break;
            case -5:
                showExpensesForMonth("July 2016", descriptionsNoDuplicateJul16, arrayOfamountJul16);
                break;
            case -4:
                showExpensesForMonth("August 2016", descriptionsNoDuplicateAug16, arrayOfamountAug16);
                break;
            case -3:
                showExpensesForMonth("September 2016", descriptionsNoDuplicateSep16, arrayOfamountSep16);
                break;
            case -2:
                showExpensesForMonth("October 2016", descriptionsNoDuplicateOct16, arrayOfamountOct16);
                break;
            case -1:
                showExpensesForMonth("November 2016", descriptionsNoDuplicateNov16, arrayOfamountNov16);
                break;
            case 0:
                showExpensesForMonth("December 2016", descriptionsNoDuplicateDec16, arrayOfamountDec16);
                break;
            case 1:
                showExpensesForMonth("January", descriptionsNoDuplicateJan, arrayOfamountJan);
                break;
            case 2:
                showExpensesForMonth("February", descriptionsNoDuplicateFeb, arrayOfamountFeb);
                break;
            case 3:
                showExpensesForMonth("March", descriptionsNoDuplicateMar, arrayOfamountMar);
                break;
            case 4:
                showExpensesForMonth("April", descriptionsNoDuplicateApr, arrayOfamountApr);
                break;
            case 5:
                showExpensesForMonth("May", descriptionsNoDuplicateMay, arrayOfamountMay);
                break;
            case 6:
                showExpensesForMonth("June", descriptionsNoDuplicateJun, arrayOfamountJun);
                break;
            case 7:
                showExpensesForMonth("July", descriptionsNoDuplicateJul, arrayOfamountJul);
                break;
            case 8:
                showExpensesForMonth("August", descriptionsNoDuplicateAug, arrayOfamountAug);
                break;
            case 9:
                showExpensesForMonth("September", descriptionsNoDuplicateSep, arrayOfamountSep);
                break;
            case 10:
                showExpensesForMonth("October", descriptionsNoDuplicateOct, arrayOfamountOct);
                break;
            case 11:
                showExpensesForMonth("November", descriptionsNoDuplicateNov, arrayOfamountNov);
                break;
            case 12:
                showExpensesForMonth("December", descriptionsNoDuplicateDec, arrayOfamountDec);
                break;
        }// end of switch
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar_chart, menu);
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
