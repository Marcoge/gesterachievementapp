package com.gester.use.gesterachievement;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gester.use.gesterachievement.objects.Achievement;
import com.gester.use.gesterachievement.objects.LoggedAchievement;
import com.gester.use.gesterachievement.objects.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//todo onclick for textview achievement name: popup with description
//todo

public class LogAchievement extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Achievement foundAchievement;

    //UI Elements
    private TextView textViewDiscription;
    private EditText editText;
    private CheckBox checkBox;
    private User testUser;


    //Google Api client stuff
    Location location;
    private GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_achievement);
        textViewDiscription = (TextView) findViewById(R.id.textViewDiscriptionLogAchievement);
        editText = (EditText) findViewById(R.id.editTextLogAchievement);
        checkBox = (CheckBox) findViewById(R.id.checkBoxLogAchievementPrivate);

        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@test.com");
        testUser.setInstagram("test@test.com");
        buildGoogleApiClient();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_achievement, menu);
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

    public void onClickLogAchievement(View view){
        //todo log achievement with server
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.connector));
        sb.append("receiveachievement");

        LoggedAchievement temp = new LoggedAchievement();
        temp.setAchievementID(foundAchievement.getId());
        temp.setComment(editText.getText().toString());
        temp.setTimeStamp(new Date());
        temp.setUserEmail(testUser.getEmail());
        temp.setPriv(false);
        Gson t = new Gson();
        t.toJson(temp);

        Map mt = new HashMap();
        mt.put("iD", temp.getiD());
        mt.put("userEmail", temp.getUserEmail());
        mt.put("achievementID", temp.getAchievementID());
        mt.put("comment", temp.getComment());
        mt.put("pic", temp.getPic());
        mt.put("timeStamp",temp.getTimeStamp());
        mt.put("priv",temp.isPriv());

        JSONObject jo = new JSONObject(mt);




        RequestQueue rq = VolleySingleton.getInstance(this).getRequestQueue();
        JsonObjectRequest sendAchievement = new JsonObjectRequest(Request.Method.POST, sb.toString(), jo,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                String breakpointcatcher = "lalalallalal";
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {};

        rq.add(sendAchievement);

    }


    public void onClickAttachPhoto(View view){
        //todo How to attach Photo
    }


    //checking for Achievement with server
    private void checkForAchievement(){


        //Preparing Query String
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.connector));
        sb.append("getachievements?");
        sb.append("n=");
        sb.append(String.valueOf(location.getLatitude()));
        sb.append("&e=");
        sb.append(String.valueOf(location.getLongitude()));
        sb.append("&d=0.002");


        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        GsonRequest<Achievement[]> getAchievement =
                new GsonRequest<Achievement[]>(sb.toString(), Achievement[].class,

                        new Response.Listener<Achievement[]>() {
                            @Override
                            public void onResponse(Achievement[] response) {
                                List<Achievement> al = (List<Achievement>) Arrays.asList(response);
                                if (al.isEmpty()){
                                    textViewDiscription.setText("No Achievements Near By");
                                }
                                else{
                                    textViewDiscription.setText(al.get(0).getName());
                                    foundAchievement = al.get(0);
                                }

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("dat funzt nicht");
                    }
                });

        queue.add(getAchievement);
    }


    //google play api setup etc

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            checkForAchievement();
        }
        else{

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }

}
