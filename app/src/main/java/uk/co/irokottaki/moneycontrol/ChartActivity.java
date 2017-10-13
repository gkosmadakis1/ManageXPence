package uk.co.irokottaki.moneycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class ChartActivity extends AppCompatActivity  {

    private FrameLayout chartLayout;
    private PieChart mChart;
    //my data
    private static ArrayList<Float> arrayOfamounts,arrayOfamountOct15,arrayOfamountNov15,arrayOfamountDec15,arrayOfamountJan16,arrayOfamountFeb16,
            arrayOfamountMar16,arrayOfamountApr16, arrayOfamountMay16,arrayOfamountJun16,arrayOfamountJul16,arrayOfamountAug16,arrayOfamountSep16,
            arrayOfamountOct16,arrayOfamountNov16,arrayOfamountDec16,arrayOfamountJan,arrayOfamountFeb,
            arrayOfamountMar,arrayOfamountApr, arrayOfamountMay,arrayOfamountJun,arrayOfamountJul,arrayOfamountAug,arrayOfamountSep,
            arrayOfamountOct,arrayOfamountNov,arrayOfamountDec;//all the amounts for the months
    //unique descriptions
    private static LinkedHashSet<String> descriptionsNoDuplicate, descriptionsNoDuplicateOct15,descriptionsNoDuplicateNov15,descriptionsNoDuplicateDec15,
            descriptionsNoDuplicateJan16,descriptionsNoDuplicateFeb16,descriptionsNoDuplicateMar16, descriptionsNoDuplicateApr16,
            descriptionsNoDuplicateMay16,descriptionsNoDuplicateJun16,descriptionsNoDuplicateJul16, descriptionsNoDuplicateAug16,
            descriptionsNoDuplicateSep16,descriptionsNoDuplicateOct16,descriptionsNoDuplicateNov16, descriptionsNoDuplicateDec16,
            descriptionsNoDuplicateJan,descriptionsNoDuplicateFeb,descriptionsNoDuplicateMar, descriptionsNoDuplicateApr,
            descriptionsNoDuplicateMay,descriptionsNoDuplicateJun,descriptionsNoDuplicateJul, descriptionsNoDuplicateAug,
            descriptionsNoDuplicateSep,descriptionsNoDuplicateOct,descriptionsNoDuplicateNov, descriptionsNoDuplicateDec;//all the descriptions for the months
    static Map<String, Float> storeAmounts = new HashMap<String, Float>();
    private static float amountWithDuplicate;
    private ImageButton arrowLeft, arrowRight;
    ArrayList<Entry> yVals1;
    ArrayList<String> xVals;
    PieDataSet dataSet;
    PieData data;
    private int monthInt;
    private Switch toggleAmount;
    private boolean stateSwitchButton;
    private RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    private TextView monthLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_v14);
        setTitle("Pie Chart");

        readTheFile();
        chartLayout= (FrameLayout)findViewById(R.id.chartLayout);

        layout = (RelativeLayout) findViewById(R.id.chartView);

        //this is to change the background color of the activity when user changes it from settings
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
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            //adjust the width and height to the layout
            //ONLY IN LANDSCAPE VIEW this is for big tablets 7',8',9',10'
            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());
                marginParams.setMargins(-170,-130,-90,0);
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
                layout.setBackgroundDrawable(imageView.getDrawable());
            } else {
                layout.setBackground(imageView.getDrawable());
            }
        }
        else {
            layout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));
        }

        mChart = new PieChart(this);
        //add pie chart to main layout

        chartLayout.addView(mChart);
        chartLayout.setBackgroundColor(Color.TRANSPARENT);

        //configure pie chart
        mChart.setUsePercentValues(true);
        // will be the descriptions from the array mChart.setDescription();
        mChart.setDescription("");

        //enable hole and configure
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        //enable rotation of the chart and touch
        mChart.setRotation(0);
        mChart.setRotationEnabled(true);

        arrowLeft = new ImageButton(this);
        arrowLeft=(ImageButton) findViewById(R.id.arrowLeft);

        monthLabel =(TextView) findViewById(R.id.month);

        arrowRight = new ImageButton(this);
        arrowRight=(ImageButton) findViewById(R.id.arrowRight);

        //initialize switch button
        toggleAmount = (Switch) findViewById(R.id.toggleButton);
        toggleAmount.setChecked(false);

        // switch button listener
        toggleAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //i set percent values to false
                    mChart.setUsePercentValues(false);
                    data.setValueFormatter(new ValueFormatter() {
                        //this returns the values as floats
                        @Override
                        public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                            return Float.toString((float) v);
                        }
                    });
                    mChart.invalidate();//refresh the chart
                    stateSwitchButton=true;// i use this to know the state of the switch
                    //Toast.makeText(getApplicationContext(), "The switch is ON", Toast.LENGTH_SHORT).show();
                } else {

                    mChart.setUsePercentValues(true);//this calculates the percentages from my values
                    data.setValueFormatter(new PercentFormatter());//this enables the percentages, adds % after the number
                    mChart.invalidate();
                    stateSwitchButton=false;
                    //Toast.makeText(getApplicationContext(), "The switch is OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //set a chart value selected listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                //display msg when selected
                if (entry == null)
                    return;
                Toast.makeText(ChartActivity.this, xVals.get(entry.getXIndex())+": "+entry.getVal(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //add data
        addData();

        //customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        final Calendar calendar = Calendar.getInstance();//this gets the current month
        //SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String currentMonth = String.format(Locale.UK, "%tB", calendar);
        //month_date.format(calendar.getTime());
        monthLabel.setText(currentMonth);// and displays it on the month field

        String getCurrentMonthDisplayed = monthLabel.getText().toString();
        monthInt=0;
        try {
            java.util.Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(getCurrentMonthDisplayed);
            calendar.setTime(date);// here i convert the String month in an integer to be used on the switch-case
            monthInt = calendar.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        arrowRight.performClick();//this shows the pie chart for the current month
        data.setValueFormatter(new PercentFormatter());

    }// end of create method


    private void addData() {
        yVals1 = new ArrayList<Entry>();
        xVals = new ArrayList<String>();
        int i=0;
        for (Map.Entry<String, Float> entry : storeAmounts.entrySet()) {
            //i want to pass only the values that are >0, otherwise the expenses
            //that the user has added
            i++;
            if (entry.getValue() > 0) {
                //add getkey=description-getvalue=amount in the result set
                //yVals1.add(new Entry(entry.getValue(), i));
                //xVals.add(entry.getKey());
            }
        }
        //create pie data set
        dataSet = new PieDataSet(yVals1, "Expenses");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        //add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //instantiate pie data object
        PieData data = new PieData(xVals, dataSet);
        //data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);
        //undo all highlights
        mChart.highlightValues(null);
        //update pie chart
        mChart.invalidate();
    }

    public void modifyData() {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        data = new PieData(xVals, dataSet);

        //this is called every time the user presses the arrows left or right
        if (stateSwitchButton) {
            mChart.setUsePercentValues(false);//if switch is ON it shows numbers not percentages
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    return Float.toString((float) v);
                }
            });
        }
        else {

            mChart.setUsePercentValues(true);//if switch is OFF it shows percentages
            data.setValueFormatter(new PercentFormatter());
        }

        data.setValueTextSize(12f);//this is the size of the percentages/numbers
        data.setValueTextColor(Color.GRAY);
        mChart.setDrawSliceText(false);//this removes the descriptions from every slice

        mChart.setData(data);
        //undo all highlights
        mChart.highlightValues(null);
        //update pie chart
        mChart.invalidate();
    }

    public static void readTheFile() {
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
        descriptionsNoDuplicateOct15 = new LinkedHashSet<String>();
        descriptionsNoDuplicateNov15 = new LinkedHashSet<String>();
        descriptionsNoDuplicateDec15 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJan16 = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateFeb16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateMar16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateApr16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateMay16 = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateJun16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJul16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateAug16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateSep16 = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateOct16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateNov16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateDec16 = new LinkedHashSet<String>();
        descriptionsNoDuplicateJan = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateFeb = new LinkedHashSet<String>();
        descriptionsNoDuplicateMar = new LinkedHashSet<String>();
        descriptionsNoDuplicateApr = new LinkedHashSet<String>();
        descriptionsNoDuplicateMay = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateJun = new LinkedHashSet<String>();
        descriptionsNoDuplicateJul = new LinkedHashSet<String>();
        descriptionsNoDuplicateAug = new LinkedHashSet<String>();
        descriptionsNoDuplicateSep = new LinkedHashSet<String>();//store unique descriptions
        descriptionsNoDuplicateOct = new LinkedHashSet<String>();
        descriptionsNoDuplicateNov = new LinkedHashSet<String>();
        descriptionsNoDuplicateDec = new LinkedHashSet<String>();

        try {
            InputStream inputStream =  new FileInputStream("/data/data/uk.co.irokottaki.moneycontrol/files/expenses.txt");
            Scanner in = new Scanner(inputStream);
            int lineIndex = 0;//this is to count the lines
            while (in.hasNextLine()) {

                String line = in.nextLine();
                if (++lineIndex >2 && !line.equals("")) {
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
                                amountWithDuplicate = arrayOfamounts.get(i)+Float.valueOf(amount);//add up the amounts if there are duplicates
                                arrayOfamounts.set(i, amountWithDuplicate);
                                storeAmounts.put(item, amountWithDuplicate);//store the amount to the item
                            }
                        }
                    }
                    else    {
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

    private void showExpensesForMonth(String month, LinkedHashSet descriptions, ArrayList<Float> arrayAmount) {

        monthLabel.setText(month);
        dataSet.clear();
        xVals.clear();
        int x=0;
        for (Iterator<String> s = descriptions.iterator();s.hasNext(); x++){
            String desc = s.next();
            yVals1.add(new Entry(arrayAmount.get(x), x));
            xVals.add(desc);
        }
        dataSet = new PieDataSet(yVals1, "Expenses for" +month);
        modifyData();
    }

    private static void addAmountsWithDuplicates (LinkedHashSet descriptions, String desc, String amount, ArrayList<Float> arrayAmount) {
        if (descriptions.contains(desc)) {
            int i = 0;
            for (Iterator<String> s = descriptions.iterator(); s.hasNext(); i++) {
                String descFound = s.next();
                if (desc.equals(descFound)) {
                    amountWithDuplicate = arrayAmount.get(i)+Float.valueOf(amount);//add up the amounts if there are duplicates
                    arrayAmount.set(i, amountWithDuplicate);
                }
            }
        }
        else    {
            descriptions.add(desc);
            arrayAmount.add(Float.valueOf(amount));
        }
    }

    private void casesToShowExpensesForMonth () {

        switch ( monthInt  ) {
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
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(this, MainActivity.class);

        bundle.putInt("Chart", 2);
        intent.putExtras(bundle);

        return intent;
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

    public static LinkedHashSet getDescriptionsOfOct15() {
        return descriptionsNoDuplicateOct15;
    }
    public static LinkedHashSet getDescriptionsOfNov15() {
        return descriptionsNoDuplicateNov15;
    }
    public static LinkedHashSet getDescriptionsOfDec15() {
        return descriptionsNoDuplicateDec15;
    }
    public static LinkedHashSet getDescriptionsOfJan16() {
        return descriptionsNoDuplicateJan16;
    }
    public static LinkedHashSet getDescriptionsOfFeb16() {
        return descriptionsNoDuplicateFeb16;
    }
    public static LinkedHashSet getDescriptionsOfMar16() {
        return descriptionsNoDuplicateMar16;
    }
    public static LinkedHashSet getDescriptionsOfApr16() {
        return descriptionsNoDuplicateApr16;
    }
    public static LinkedHashSet getDescriptionsOfMay16() {
        return descriptionsNoDuplicateMay16;
    }
    public static LinkedHashSet getDescriptionsOfJun16() {
        return descriptionsNoDuplicateJun16;
    }
    public static LinkedHashSet getDescriptionsOfJul16() {
        return descriptionsNoDuplicateJul16;
    }
    public static LinkedHashSet getDescriptionsOfAug16() {
        return descriptionsNoDuplicateAug16;
    }
    public static LinkedHashSet getDescriptionsOfSep16() {
        return descriptionsNoDuplicateSep16;
    }
    public static LinkedHashSet getDescriptionsOfOct16() {
        return descriptionsNoDuplicateOct16;
    }
    public static LinkedHashSet getDescriptionsOfNov16() {
        return descriptionsNoDuplicateNov16;
    }
    public static LinkedHashSet getDescriptionsOfDec16() {
        return descriptionsNoDuplicateDec16;
    }
    public static LinkedHashSet getDescriptionsOfJan() {
        return descriptionsNoDuplicateJan;
    }
    public static LinkedHashSet getDescriptionsOfFeb() {
        return descriptionsNoDuplicateFeb;
    }
    public static LinkedHashSet getDescriptionsOfMar() {
        return descriptionsNoDuplicateMar;
    }
    public static LinkedHashSet getDescriptionsOfApr() {
        return descriptionsNoDuplicateApr;
    }
    public static LinkedHashSet getDescriptionsOfMay() {
        return descriptionsNoDuplicateMay;
    }
    public static LinkedHashSet getDescriptionsOfJun() {
        return descriptionsNoDuplicateJun;
    }
    public static LinkedHashSet getDescriptionsOfJul() {
        return descriptionsNoDuplicateJul;
    }
    public static LinkedHashSet getDescriptionsOfAug() {
        return descriptionsNoDuplicateAug;
    }
    public static LinkedHashSet getDescriptionsOfSep() {
        return descriptionsNoDuplicateSep;
    }
    public static LinkedHashSet getDescriptionsOfOct() {
        return descriptionsNoDuplicateOct;
    }
    public static LinkedHashSet getDescriptionsOfNov() {
        return descriptionsNoDuplicateNov;
    }
    public static LinkedHashSet getDescriptionsOfDec() {
        return descriptionsNoDuplicateDec;
    }

    public static ArrayList getAmountsOfOct15 () {
        return arrayOfamountOct15;
    }
    public static ArrayList getAmountsOfNov15 () {
        return arrayOfamountNov15;
    }
    public static ArrayList getAmountsOfDec15 () {
        return arrayOfamountDec15;
    }
    public static ArrayList getAmountsOfJan16 () {
        return arrayOfamountJan16;
    }
    public static ArrayList getAmountsOfFeb16 () {
        return arrayOfamountFeb16;
    }
    public static ArrayList getAmountsOfMar16 () {
        return arrayOfamountMar16;
    }
    public static ArrayList getAmountsOfApr16 () {
        return arrayOfamountApr16;
    }
    public static ArrayList getAmountsOfMay16 () {
        return arrayOfamountMay16;
    }
    public static ArrayList getAmountsOfJun16 () {
        return arrayOfamountJun16;
    }
    public static ArrayList getAmountsOfJul16 () {
        return arrayOfamountJul16;
    }
    public static ArrayList getAmountsOfAug16 () {
        return arrayOfamountAug16;
    }
    public static ArrayList getAmountsOfSep16 () {
        return arrayOfamountSep16;
    }
    public static ArrayList getAmountsOfOct16 () {
        return arrayOfamountOct16;
    }
    public static ArrayList getAmountsOfNov16 () {
        return arrayOfamountNov16;
    }
    public static ArrayList getAmountsOfDec16 () {
        return arrayOfamountDec16;
    }
    public static ArrayList getAmountsOfJan () {
        return arrayOfamountJan;
    }
    public static ArrayList getAmountsOfFeb () {
        return arrayOfamountFeb;
    }
    public static ArrayList getAmountsOfMar () {
        return arrayOfamountMar;
    }
    public static ArrayList getAmountsOfApr () {
        return arrayOfamountApr;
    }
    public static ArrayList getAmountsOfMay () {
        return arrayOfamountMay;
    }
    public static ArrayList getAmountsOfJun () {
        return arrayOfamountJun;
    }
    public static ArrayList getAmountsOfJul () {
        return arrayOfamountJul;
    }
    public static ArrayList getAmountsOfAug () {
        return arrayOfamountAug;
    }
    public static ArrayList getAmountsOfSep () {
        return arrayOfamountSep;
    }
    public static ArrayList getAmountsOfOct () {
        return arrayOfamountOct;
    }
    public static ArrayList getAmountsOfNov () {
        return arrayOfamountNov;
    }
    public static ArrayList getAmountsOfDec () {
        return arrayOfamountDec;
    }



}
