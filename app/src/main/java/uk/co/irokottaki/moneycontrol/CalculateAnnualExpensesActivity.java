package uk.co.irokottaki.moneycontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class CalculateAnnualExpensesActivity extends AppCompatActivity {
    private Spinner expensesList, yearList;
    private Button calculateButton;
    ArrayAdapter<String> spinnerAdapter;
    private RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    private LinkedHashSet descriptionsWithourDuplicates2015, descriptionsWithourDuplicates2016;
    private ArrayList itemsAddedByUser = null;
    private ArrayList<Float> arrayOfamountOct15,arrayOfamountNov15,arrayOfamountDec15,arrayOfamountJan16,arrayOfamountFeb16,
            arrayOfamountMar16,arrayOfamountApr16, arrayOfamountMay16,arrayOfamountJun16,arrayOfamountJul16,arrayOfamountAug16,arrayOfamountSep16,
            arrayOfamountOct16,arrayOfamountNov16,arrayOfamountDec16,arrayOfamountJan,arrayOfamountFeb,
            arrayOfamountMar,arrayOfamountApr, arrayOfamountMay,arrayOfamountJun,arrayOfamountJul,arrayOfamountAug,arrayOfamountSep,
            arrayOfamountOct,arrayOfamountNov,arrayOfamountDec;//all the amounts for the months
    //unique descriptions
    private LinkedHashSet<String>  descriptionsNoDuplicateOct15,descriptionsNoDuplicateNov15,descriptionsNoDuplicateDec15,
            descriptionsNoDuplicateJan16,descriptionsNoDuplicateFeb16,descriptionsNoDuplicateMar16, descriptionsNoDuplicateApr16,
            descriptionsNoDuplicateMay16,descriptionsNoDuplicateJun16,descriptionsNoDuplicateJul16, descriptionsNoDuplicateAug16,
            descriptionsNoDuplicateSep16,descriptionsNoDuplicateOct16,descriptionsNoDuplicateNov16, descriptionsNoDuplicateDec16,
            descriptionsNoDuplicateJan,descriptionsNoDuplicateFeb,descriptionsNoDuplicateMar, descriptionsNoDuplicateApr,
            descriptionsNoDuplicateMay,descriptionsNoDuplicateJun,descriptionsNoDuplicateJul, descriptionsNoDuplicateAug,
            descriptionsNoDuplicateSep,descriptionsNoDuplicateOct,descriptionsNoDuplicateNov, descriptionsNoDuplicateDec;
    private Double annualExpenseDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_annual_expenses);

        setTitle("Calculate Annual Expenses");

        //retrieve the spinner items from main activity so whatever is populated in the main activity to be displayed here as well
        itemsAddedByUser = MainActivity.getitemsAddedByUser();
        //retrieve the data for calculation
        retrieveDataFromChartActivity();

        layout = (RelativeLayout) findViewById(R.id.activity_calculate_annual_expenses);

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

        expensesList = (Spinner) findViewById(R.id.expensesSpinner);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsAddedByUser);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expensesList.setAdapter(spinnerAdapter);

        expensesList.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        spinnerAdapter,
                        R.layout.spinnernothingselected,
                        this));

        yearList = (Spinner) findViewById(R.id.yearSpinner);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        //get the year items from the array
        Resources res = getResources();
        String[] yearInTheList = res.getStringArray(R.array.yearItems);
        int length=yearInTheList.length;
        String yearInArray=null;
        for (int i=0; i<length; i++) {
            yearInArray = yearInTheList[i];

            if (year == Integer.parseInt(yearInArray)) {
                yearList.setSelection(i);
            }
        }
        //calculate Button
        calculateButton = new Button(this);
        calculateButton = (Button) findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateExpensesByYear();
                DecimalFormat df = new DecimalFormat("#.00");
                if (expensesList.getSelectedItem() != null && !expensesList.getSelectedItem().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalculateAnnualExpensesActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("Annual Expenses for year: " + yearList.getSelectedItem())
                            .setMessage("You have spent: " + df.format(annualExpenseDouble) + " for " + expensesList.getSelectedItem());
                    AlertDialog alert1;
                    builder.setPositiveButton("Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    alert1 = builder.create();
                    alert1.show();
                }
            }
        });

    }// end of onCreate method

    public void retrieveDataFromChartActivity () {
        ChartActivity.readTheFile();
        descriptionsNoDuplicateOct15 = ChartActivity.getDescriptionsOfOct15();
        descriptionsNoDuplicateNov15 = ChartActivity.getDescriptionsOfNov15();
        descriptionsNoDuplicateDec15 = ChartActivity.getDescriptionsOfDec15();
        descriptionsNoDuplicateJan16   = ChartActivity.getDescriptionsOfJan16();
        descriptionsNoDuplicateFeb16   = ChartActivity.getDescriptionsOfFeb16();
        descriptionsNoDuplicateMar16   = ChartActivity.getDescriptionsOfMar16();
        descriptionsNoDuplicateApr16   = ChartActivity.getDescriptionsOfApr16();
        descriptionsNoDuplicateMay16   = ChartActivity.getDescriptionsOfMay16();
        descriptionsNoDuplicateJun16   = ChartActivity.getDescriptionsOfJun16();
        descriptionsNoDuplicateJul16   = ChartActivity.getDescriptionsOfJul16();
        descriptionsNoDuplicateAug16   = ChartActivity.getDescriptionsOfAug16();
        descriptionsNoDuplicateSep16   = ChartActivity.getDescriptionsOfSep16();
        descriptionsNoDuplicateOct16   = ChartActivity.getDescriptionsOfOct16();
        descriptionsNoDuplicateNov16   = ChartActivity.getDescriptionsOfNov16();
        descriptionsNoDuplicateDec16   = ChartActivity.getDescriptionsOfDec16();
        descriptionsNoDuplicateJan   = ChartActivity.getDescriptionsOfJan();
        descriptionsNoDuplicateFeb   = ChartActivity.getDescriptionsOfFeb();
        descriptionsNoDuplicateMar   = ChartActivity.getDescriptionsOfMar();
        descriptionsNoDuplicateApr   = ChartActivity.getDescriptionsOfApr();
        descriptionsNoDuplicateMay   = ChartActivity.getDescriptionsOfMay();
        descriptionsNoDuplicateJun   = ChartActivity.getDescriptionsOfJun();
        descriptionsNoDuplicateJul   = ChartActivity.getDescriptionsOfJul();
        descriptionsNoDuplicateAug   = ChartActivity.getDescriptionsOfAug();
        descriptionsNoDuplicateSep   = ChartActivity.getDescriptionsOfSep();
        descriptionsNoDuplicateOct   = ChartActivity.getDescriptionsOfOct();
        descriptionsNoDuplicateNov   = ChartActivity.getDescriptionsOfNov();
        descriptionsNoDuplicateDec   = ChartActivity.getDescriptionsOfDec();

        arrayOfamountOct15 = ChartActivity.getAmountsOfOct15();
        arrayOfamountNov15 = ChartActivity.getAmountsOfNov15();
        arrayOfamountDec15 = ChartActivity.getAmountsOfDec15();
        arrayOfamountJan16   = ChartActivity.getAmountsOfJan16();
        arrayOfamountFeb16   = ChartActivity.getAmountsOfFeb16();
        arrayOfamountMar16   = ChartActivity.getAmountsOfMar16();
        arrayOfamountApr16   = ChartActivity.getAmountsOfApr16();
        arrayOfamountMay16   = ChartActivity.getAmountsOfMay16();
        arrayOfamountJun16   = ChartActivity.getAmountsOfJun16();
        arrayOfamountJul16   = ChartActivity.getAmountsOfJul16();
        arrayOfamountAug16   = ChartActivity.getAmountsOfAug16();
        arrayOfamountSep16   = ChartActivity.getAmountsOfSep16();
        arrayOfamountOct16   = ChartActivity.getAmountsOfOct16();
        arrayOfamountNov16   = ChartActivity.getAmountsOfNov16();
        arrayOfamountDec16   = ChartActivity.getAmountsOfDec16();
        arrayOfamountJan   = ChartActivity.getAmountsOfJan();
        arrayOfamountFeb   = ChartActivity.getAmountsOfFeb();
        arrayOfamountMar   = ChartActivity.getAmountsOfMar();
        arrayOfamountApr   = ChartActivity.getAmountsOfApr();
        arrayOfamountMay   = ChartActivity.getAmountsOfMay();
        arrayOfamountJun   = ChartActivity.getAmountsOfJun();
        arrayOfamountJul   = ChartActivity.getAmountsOfJul();
        arrayOfamountAug   = ChartActivity.getAmountsOfAug();
        arrayOfamountSep   = ChartActivity.getAmountsOfSep();
        arrayOfamountOct   = ChartActivity.getAmountsOfOct();
        arrayOfamountNov   = ChartActivity.getAmountsOfNov();
        arrayOfamountDec   = ChartActivity.getAmountsOfDec();

     }

    private Double calculateExpensesByYear () {

        annualExpenseDouble = 0.0;

        if (expensesList.getSelectedItem() == null || expensesList.getSelectedItem().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CalculateAnnualExpensesActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("Empty Field")
                    .setMessage("Expenses Field is empty, select first an expense and try again");
            AlertDialog alert1;
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert1 = builder.create();
            alert1.show();
        }
        else {
            String selectedExpense = expensesList.getSelectedItem().toString();

            if (yearList.getSelectedItem().equals("2017")) {

                calculateExpensesByMonth(descriptionsNoDuplicateJan, selectedExpense, arrayOfamountJan);
                calculateExpensesByMonth(descriptionsNoDuplicateFeb, selectedExpense, arrayOfamountFeb);
                calculateExpensesByMonth(descriptionsNoDuplicateMar, selectedExpense, arrayOfamountMar);
                calculateExpensesByMonth(descriptionsNoDuplicateApr, selectedExpense, arrayOfamountApr);
                calculateExpensesByMonth(descriptionsNoDuplicateMay, selectedExpense, arrayOfamountMay);
                calculateExpensesByMonth(descriptionsNoDuplicateJun, selectedExpense, arrayOfamountJun);
                calculateExpensesByMonth(descriptionsNoDuplicateJul, selectedExpense, arrayOfamountJul);
                calculateExpensesByMonth(descriptionsNoDuplicateAug, selectedExpense, arrayOfamountAug);
                calculateExpensesByMonth(descriptionsNoDuplicateSep, selectedExpense, arrayOfamountSep);
                calculateExpensesByMonth(descriptionsNoDuplicateOct, selectedExpense, arrayOfamountOct);
                calculateExpensesByMonth(descriptionsNoDuplicateNov, selectedExpense, arrayOfamountNov);
                calculateExpensesByMonth(descriptionsNoDuplicateDec, selectedExpense, arrayOfamountDec);
            }

            else if (yearList.getSelectedItem().equals("2016")) {

                calculateExpensesByMonth(descriptionsNoDuplicateJan16, selectedExpense, arrayOfamountJan16);
                calculateExpensesByMonth(descriptionsNoDuplicateFeb16, selectedExpense, arrayOfamountFeb16);
                calculateExpensesByMonth(descriptionsNoDuplicateMar16, selectedExpense, arrayOfamountMar16);
                calculateExpensesByMonth(descriptionsNoDuplicateApr16, selectedExpense, arrayOfamountApr16);
                calculateExpensesByMonth(descriptionsNoDuplicateMay16, selectedExpense, arrayOfamountMay16);
                calculateExpensesByMonth(descriptionsNoDuplicateJun16, selectedExpense, arrayOfamountJun16);
                calculateExpensesByMonth(descriptionsNoDuplicateJul16, selectedExpense, arrayOfamountJul16);
                calculateExpensesByMonth(descriptionsNoDuplicateAug16, selectedExpense, arrayOfamountAug16);
                calculateExpensesByMonth(descriptionsNoDuplicateSep16, selectedExpense, arrayOfamountSep16);
                calculateExpensesByMonth(descriptionsNoDuplicateOct16, selectedExpense, arrayOfamountOct16);
                calculateExpensesByMonth(descriptionsNoDuplicateNov16, selectedExpense, arrayOfamountNov16);
                calculateExpensesByMonth(descriptionsNoDuplicateDec16, selectedExpense, arrayOfamountDec16);
            }
            else if (yearList.getSelectedItem().equals("2015")){

                calculateExpensesByMonth(descriptionsNoDuplicateOct15, selectedExpense, arrayOfamountOct15);
                calculateExpensesByMonth(descriptionsNoDuplicateNov15, selectedExpense, arrayOfamountNov15);
                calculateExpensesByMonth(descriptionsNoDuplicateDec15, selectedExpense, arrayOfamountDec15);

            }
        }
        return annualExpenseDouble;
    }

    private void calculateExpensesByMonth (LinkedHashSet descriptions, String selectedExpense,ArrayList<Float> arrayAmount) {

        Iterator Itr = descriptions.iterator();
        int i = 0;
        while (Itr.hasNext()) {
            String descFound = Itr.next().toString();
            if (selectedExpense.equals(descFound)) {
                annualExpenseDouble += (double) arrayAmount.get(i);
            }
            i++;
        }
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
}
