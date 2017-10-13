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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class AnnualChartActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {
    private LineChart mChart;
    Float amountJan15, amountFeb15, amountMar15, amountApr15, amountMay15, amountJun15, amountJul15, amountAug15, amountSep15,
            amountOct15, amountNov15, amountDec15;
    Float amountJan16, amountFeb16, amountMar16, amountApr16, amountMay16, amountJun16, amountJul16, amountAug16, amountSep16,
            amountOct16, amountNov16, amountDec16;
    Float amountJan17, amountFeb17, amountMar17, amountApr17, amountMay17, amountJun17, amountJul17, amountAug17, amountSep17,
            amountOct17, amountNov17, amountDec17;
    ImageButton leftYearButton, rightYearButton;
    private TextView specificexpenseForYear;
    private int year;
    private RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_chart);
        setTitle("Annual Chart");

        readTheFile();

        layout = (RelativeLayout) findViewById(R.id.annualChartView);

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

        mChart = (LineChart) findViewById(R.id.annualChartLayout);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // Arrow buttons
        leftYearButton = new ImageButton (this);
        leftYearButton = (ImageButton) findViewById(R.id.leftYearButton);

        rightYearButton = new ImageButton(this);
        rightYearButton = (ImageButton) findViewById(R.id.rigthYearButton);

        //Year Label
        final TextView yearView= (TextView) findViewById(R.id.yearlabel);

        //Bottom Label for each expense in a year
        specificexpenseForYear = (TextView) findViewById(R.id.specificExpensesOfYear);
        specificexpenseForYear.setMovementMethod(LinkMovementMethod.getInstance());
        specificexpenseForYear.setText(Html.fromHtml("<font color=#0000FF> <u>Hom much do I spend on a year for each expense?" +
                "</u></font>"));

        // x-axis limit line
        /*LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(130f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);*/
        XAxis xAxis = mChart.getXAxis();
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //leftAxis.addLimitLine(ll1);
        //leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaxValue(3000f);
        leftAxis.setAxisMinValue(-50f);
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);// get the current year
        yearView.setText("Year: " + year);

        //i am just setting the data for every year that will be displayed on initializing the activity.
        if (year == 2017) {
            setData(amountJan17, amountFeb17, amountMar17, amountApr17, amountMay17, amountJun17, amountJul17, amountAug17, amountSep17, amountOct17, amountNov17, amountDec17);
        }

        if (year == 2016) {
            setData(amountJan16, amountFeb16, amountMar16, amountApr16, amountMay16, amountJun16, amountJul16, amountAug16, amountSep16, amountOct16, amountNov16, amountDec16);
        }

        if (year == 2015) {
            setData(amountJan15, amountFeb15,amountMar15, amountApr15,amountMay15,amountJun15,amountJul15,amountAug15, amountSep15, amountOct15, amountNov15, amountDec15);
        }

        //Listener to events on clicking the image arrows
        leftYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year--;
                switch (year){
                    case 2017:
                        yearView.setText("Year: " + year);
                        setData(amountJan17, amountFeb17, amountMar17, amountApr17, amountMay17, amountJun17, amountJul17, amountAug17, amountSep17, amountOct17, amountNov17, amountDec17);
                        break;
                    case 2016:
                        yearView.setText("Year: " + year);
                        setData(amountJan16, amountFeb16, amountMar16, amountApr16, amountMay16, amountJun16, amountJul16, amountAug16, amountSep16, amountOct16, amountNov16, amountDec16);
                        break;
                    case 2015:
                        yearView.setText("Year: " + year);
                        setData(amountJan15, amountFeb15,amountMar15, amountApr15,amountMay15,amountJun15,amountJul15,amountAug15, amountSep15, amountOct15, amountNov15, amountDec15);
                        break;

                }
            }
        });

        rightYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year++;
                switch (year) {
                    case 2017:
                        yearView.setText("Year: " + year);
                        setData(amountJan17, amountFeb17, amountMar17, amountApr17, amountMay17, amountJun17, amountJul17, amountAug17, amountSep17, amountOct17, amountNov17, amountDec17);
                        break;
                    case 2016:
                        yearView.setText("Year: " + year);
                        setData(amountJan16, amountFeb16, amountMar16, amountApr16, amountMay16, amountJun16, amountJul16, amountAug16, amountSep16, amountOct16, amountNov16, amountDec16);
                        break;
                    case 2015:
                        yearView.setText("Year: " + year);
                        setData(amountJan15, amountFeb15, amountMar15, amountApr15, amountMay15, amountJun15, amountJul15, amountAug15, amountSep15, amountOct15, amountNov15, amountDec15);
                        break;

                }
            }
        });

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend
        l.setForm(Legend.LegendForm.LINE);

        specificexpenseForYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(view.getContext(), CalculateAnnualExpensesActivity.class);
                startActivity(intent1);
            }
        });
    }// end of onCreate


    private void setData(Float J, Float F, Float M, Float A, Float Ma, Float Jun, Float Jul, Float Au, Float S, Float O, Float N, Float D) {

        String monthData [] = new String[] {"January","February","March","April",
                "May","June","July","August","September","October","November","December"};
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < monthData.length; i++) {
            xVals.add((monthData[i]).substring(0,3));
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        ArrayList<Float> addAlltheMonths = new ArrayList<>();
        addAlltheMonths.add(J);
        addAlltheMonths.add(F);
        addAlltheMonths.add(M);
        addAlltheMonths.add(A);
        addAlltheMonths.add(Ma);
        addAlltheMonths.add(Jun);
        addAlltheMonths.add(Jul);
        addAlltheMonths.add(Au);
        addAlltheMonths.add(S);
        addAlltheMonths.add(O);
        addAlltheMonths.add(N);
        addAlltheMonths.add(D);

        for (int i = 0; i < monthData.length; i++) {
            //float mult = (range + 1);
            //float val = (float) (Math.random() * mult) + 3;// + (float)
            float val = addAlltheMonths.get(i);
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Annual Expenses");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);
        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
        //set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
        //update chart
        mChart.invalidate();
    }

    public void readTheFile() {

        String amount;
        String date;
        amountJan15 =0.0f;
        amountFeb15=0.0f;
        amountMar15 =0.0f;
        amountApr15 =0.0f;
        amountMay15 =0.0f;
        amountJun15 =0.0f;
        amountJul15 =0.0f;
        amountAug15 =0.0f;
        amountSep15 =0.0f;
        amountOct15 =0.0f;
        amountNov15 =0.0f;
        amountDec15 =0.0f;

        amountJan16 =0.0f;
        amountFeb16=0.0f;
        amountMar16 =0.0f;
        amountApr16 =0.0f;
        amountMay16 =0.0f;
        amountJun16 =0.0f;
        amountJul16 =0.0f;
        amountAug16 =0.0f;
        amountSep16 =0.0f;
        amountOct16 =0.0f;
        amountNov16 =0.0f;
        amountDec16 =0.0f;

        amountJan17 =0.0f;
        amountFeb17 =0.0f;
        amountMar17 =0.0f;
        amountApr17 =0.0f;
        amountMay17 =0.0f;
        amountJun17 =0.0f;
        amountJul17 =0.0f;
        amountAug17 =0.0f;
        amountSep17 =0.0f;
        amountOct17 =0.0f;
        amountNov17 =0.0f;
        amountDec17 =0.0f;
        try {
            InputStream inputStream = openFileInput("expenses.txt");
            Scanner in = new Scanner(inputStream);
            int lineIndex = 0;//this is to count the lines
            while (in.hasNextLine()) {

                String line = in.nextLine();
                if (++lineIndex > 2 && !line.equals("")) {
                    int index = line.lastIndexOf(" ");
                    amount = line.substring(0, line.indexOf(" "));
                    date = line.substring(index, line.length());
                    String extractMonthFromDate = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
                    String extractYearFromDate = date.substring(date.lastIndexOf("/")+1, date.length());

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2017")) {
                        amountJan17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2017")) {
                        amountFeb17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2017")) {
                        amountMar17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2017")) {
                        amountApr17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2017")) {
                        amountMay17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2017")) {
                        amountJun17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2017")) {
                        amountJul17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2017")) {
                        amountAug17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2017")) {
                        amountSep17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2017")) {
                        amountOct17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2017")) {
                        amountNov17 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2017")) {
                        amountDec17 += Float.valueOf(amount);
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2016")) {
                        amountJan16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2016")) {
                        amountFeb16+= Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2016")) {
                        amountMar16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2016")) {
                        amountApr16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2016")) {
                        amountMay16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2016")) {
                        amountJun16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2016")) {
                        amountJul16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2016")) {
                        amountAug16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2016")) {
                        amountSep16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2016")) {
                        amountOct16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2016")) {
                        amountNov16 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2016")) {
                        amountDec16 += Float.valueOf(amount);
                    }

                    if (extractMonthFromDate.equals("01") && extractYearFromDate.equals("2015")) {
                        amountJan15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("02") && extractYearFromDate.equals("2015")) {
                        amountFeb15+= Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("03") && extractYearFromDate.equals("2015")) {
                        amountMar15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("04") && extractYearFromDate.equals("2015")) {
                        amountApr15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("05") && extractYearFromDate.equals("2015")) {
                        amountMay15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("06") && extractYearFromDate.equals("2015")) {
                        amountJun15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("07") && extractYearFromDate.equals("2015")) {
                        amountJul15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("08") && extractYearFromDate.equals("2015")) {
                        amountAug15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("09") && extractYearFromDate.equals("2015")) {
                        amountSep15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("10") && extractYearFromDate.equals("2015")) {
                        amountOct15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("11") && extractYearFromDate.equals("2015")) {
                        amountNov15 += Float.valueOf(amount);
                    }
                    if (extractMonthFromDate.equals("12") && extractYearFromDate.equals("2015")) {
                        amountDec15 += Float.valueOf(amount);
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
    public Intent getSupportParentActivityIntent() {
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(this, MainActivity.class);

        bundle.putInt("AnnualChart", 2);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_annual_chart, menu);
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

    @Override
    public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

    }

    @Override
    public void onChartScale(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }
}
