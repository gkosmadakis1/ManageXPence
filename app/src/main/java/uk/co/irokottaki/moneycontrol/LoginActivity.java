package uk.co.irokottaki.moneycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dropbox.core.android.Auth;

/**
 * A login screen that offers login via email/password.
 */


    public class LoginActivity extends AppCompatActivity {
    RelativeLayout layout;
    protected PreferenceManager mPreferenceManager;
    private int mLayoutWidth,mLayoutHeight;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            Button SignInButton = (Button) findViewById(R.id.sign_in_button);

            layout = (RelativeLayout) findViewById(R.id.loginActivityView);
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    DisplayMetrics metrics = getResources().getDisplayMetrics();

                    int deviceWidth = metrics.widthPixels;

                    int deviceHeight = metrics.heightPixels;

                    float widthInPercentage = ((float) 600 / 600) * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
                    //height:935
                    float heightInPercentage = ((float) 1150 / 1024) * 100; // same procedure 300 is the height of the LinearLayout and i'm converting it into percentage

                    mLayoutWidth = (int) ((widthInPercentage * deviceWidth) / 100);

                    mLayoutHeight = (int) ((heightInPercentage * deviceHeight) / 100);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mLayoutWidth, mLayoutHeight);

                    layout.setLayoutParams(layoutParams);
                }
            });

            if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#ffffff")){
                layout.setBackgroundResource(R.drawable.backgroundimg);//need to call it to get the wood style displayed
            }

            //the case where the user has selected for a background on image from the device gallery
            else if (mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff").equals("#00000000")){

                SharedPreferences prefers = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                //retrieve the file path from preferences
                String filePath = prefers.getString("GalleryImage", "#00000000");
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                ImageView imageView = (ImageView)this.findViewById(R.id.ImageView);
                imageView.setImageBitmap(bitmap);
                //final BitmapFactory.Options options = new BitmapFactory.Options();
            /*options.inJustDecodeBounds = true;
            int scale = 1;
            while(options.outWidth / scale / 2 >= 70 &&
                    options.outHeight / scale / 2 >= 70) {
                scale *= 2;
            }*/
                //options.inSampleSize = 4;

                final BitmapFactory.Options options = new BitmapFactory.Options();
                //adjust the width and height to the layout
                //ONLY IN LANDSCAPE VIEW this is for big tablets 7',10'
                int orientation = getResources().getConfiguration().orientation;

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float widthInPercentage =0f;
                float heightInPercentage =0f;
                int deviceWidth = metrics.widthPixels;
                int deviceHeight = metrics.heightPixels;
                widthInPercentage = ((float) 625 / 600) * 100;
                heightInPercentage = ((float) 1380 / 1024) * 100;//height was 980
                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());
                    marginParams.setMargins(-170,-130,-90,0);
                    imageView.setLayoutParams(marginParams);
                    heightInPercentage = ((float) 2245 / 1024) * 100;//height was 980
                    layout.setBackground(imageView.getDrawable());
                }
                mLayoutWidth = (int) ((widthInPercentage * deviceWidth) / 100);
                mLayoutHeight = (int) ((heightInPercentage * deviceHeight) / 100);
                imageView.getLayoutParams().height = mLayoutHeight;
                imageView.getLayoutParams().width = mLayoutWidth;
                int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    RelativeLayout.LayoutParams marginParams = new RelativeLayout.LayoutParams(imageView.getLayoutParams());
                    marginParams.setMargins(-50, -40, 0, 0);//i override here the Top and Bottom margin that is set on the xml view file
                    imageView.setLayoutParams(marginParams);
                    layout.setBackgroundDrawable(imageView.getDrawable());
                } else {
                    layout.setBackground(imageView.getDrawable());
                }

            }
            else {
                layout.setBackgroundColor(Color.parseColor(mPreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("background_color", "#ffffff")));
            }
            SignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.APP_KEY));
                }
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            getAccessToken();
        }

        public void getAccessToken() {
            String accessToken = Auth.getOAuth2Token(); //generate Access Token
            if (accessToken != null) {
                //Store accessToken in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("com.example.valdio.dropboxintegration", Context.MODE_PRIVATE);
                prefs.edit().putString("access-token", accessToken).apply();

                //Proceed to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }




    /*private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    *//**
     * Callback received when a permissions request has been completed.
     *//*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    *//**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     *//*
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    *//**
     * Shows the progress UI and hides the login form.
     *//*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }*/



