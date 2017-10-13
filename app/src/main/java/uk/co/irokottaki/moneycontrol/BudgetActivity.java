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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    ImageButton infoBudgetButton;
    private SeekBar budgetSeekBar;
    private TextView warningDisplay, budgetWarningValue;
    private int step = 1;
    private int max = 2000;
    private int min = 0;
    private int progress = 0;
    private String currentMonth;
    private RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    public boolean budgetWarningEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        setTitle("Budget Warnings");

        layout= (RelativeLayout) findViewById(R.id.budgetView);

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
           /* options.inJustDecodeBounds = true;
            int scale = 1;
            while(options.outWidth / scale / 2 >= 70 &&
                    options.outHeight / scale / 2 >= 70) {
                scale *= 2;
            }*/
            //options.inSampleSize = 4;
            //adjust the width and height to the layout
            //ONLY IN LANDSCAPE VIEW this is for big tablets 7',10'
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


        //Button for information
        infoBudgetButton= new ImageButton(this);
        infoBudgetButton = (ImageButton) findViewById(R.id.infoBudgetButton);

        //Seek bar
        budgetSeekBar = (SeekBar) findViewById(R.id.budgetBar);
        budgetSeekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);

        //Warning textview
        warningDisplay = new TextView(this);
        warningDisplay = (TextView) findViewById(R.id.warningDisplay);

        //Budget Warning Value
        budgetWarningValue = (TextView) findViewById(R.id.budgetWarningValue);
        SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //get the current budget value as set by the user
        progress = prefs.getInt("budgetValue", 0);
        budgetWarningValue .setText("Your current warning budget is: "+ String.valueOf(progress));


        infoBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BudgetActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("Information")
                        .setMessage("Here you can set a budget warning using the bar displayed." +"\n"+
                                "You can move the bar and set it as it goes from 0 to 2000. The amount that you set is stored " +
                                "for every month and does not change unless you do so. The application will notify you on the 80% " +
                                "of the warning you set here as well as on the 90% and 100%. The notification will be shown after " +
                                "you add an expense."+"\n" +"\n"+"***Remember to turn on the notifications from the settings.");
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
        });


        budgetSeekBar.setMax((max - min) / step);

        warningDisplay.setText("Warning set on: " + budgetSeekBar.getProgress() + "/" + budgetSeekBar.getMax());

        final Calendar calendar = Calendar.getInstance();//this gets the current month
        //SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        currentMonth = String.format(Locale.UK, "%tB", calendar);

        budgetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                warningDisplay.setText("Warning set on: " + progress + "/" + seekBar.getMax() + " for month " + currentMonth);

                //store the progress to the budget warning value so that stays there until the user changes it.
                budgetWarningValue.setText("Your current warning budget is: " +String.valueOf(progress));

                SharedPreferences sp = getSharedPreferences("Preferences", BudgetActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("budgetValue", progress);
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(BudgetActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("Budget Warning Set")
                        .setMessage("You set your warning budget. Now go to Settings of the Home Screen to turn them on.");
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
        });

    }// end of onCreate method

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(this, MainActivity.class);

        bundle.putInt("Budget", 1);
        intent.putExtras(bundle);

        return intent;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        //this code is for storing the value of progress in the budgetWarningValue when going back to main activity
        budgetWarningValue = (TextView) findViewById(R.id.budgetWarningValue);
        //SharedPreferences.Editor prefEditor = getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit();
        //prefEditor.putString("text", budgetWarningValue.getText().toString());
        //prefEditor.commit();

        //Intent myIntent = new Intent(BudgetActivity.this, MainActivity.class);
       // myIntent.putExtra("warningSet",progress);
        //startActivity(myIntent);//send the progress bar value to main activity

        return super.onOptionsItemSelected(item);

    }




}
