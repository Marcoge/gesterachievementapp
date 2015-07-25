package com.gester.use.gesterachievement;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gester.use.gesterachievement.R;



import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gester.use.gesterachievement.objects.Achievement;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ListView achievementsNearbyList;
    private TextView textViewHome;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private ArrayList<DistanceViewObject> testArray = new ArrayList<DistanceViewObject>();
    private AdapterDistanceView distanceViewAdapter;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewHome = (TextView) findViewById(R.id.textViewHome);
        buildGoogleApiClient();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }

        //setupListViewTest();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openLogAchievement(View view){
        Intent intent = new Intent(this, LogAchievement.class);
        startActivity(intent);
    }

    public void openLoggedAchievement(View view){
        Intent intent = new Intent(this, LoggedAchievementView.class);
        startActivity(intent);
    }

    public void onClick(View view){
        //here be magic
        //todo: comon on click method for all uses in homescreen
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


    private void setupListViewTest(){

        achievementsNearbyList = (ListView) findViewById(R.id.listAchievementsNearby);

        //Preparing Query String
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.connector));
        sb.append("getachievements?");
        sb.append("n=");
        sb.append(String.valueOf(location.getLatitude()));
        sb.append("&e=");
        sb.append(String.valueOf(location.getLongitude()));
        sb.append("&d=1");


        //requesting data from server
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        GsonRequest<Achievement[]> getAchievement =
                new GsonRequest<Achievement[]>(sb.toString(), Achievement[].class,

                        new Response.Listener<Achievement[]>() {
                            @Override
                            public void onResponse(Achievement[] response) {
                                List<Achievement> al = (List<Achievement>)Arrays.asList(response);
                                testArray = achievementToDistanceView(al);
                                distanceViewAdapter.addAll(testArray);

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("dat funzt nicht");
                    }
                });

        queue.add(getAchievement);

        distanceViewAdapter = new AdapterDistanceView(this, testArray);
        achievementsNearbyList.setAdapter(distanceViewAdapter);


    }

    //transforming Achievement Array into DistanceView object array

    private ArrayList<DistanceViewObject> achievementToDistanceView(List<Achievement> aa){
        ArrayList<DistanceViewObject> temp = new ArrayList<DistanceViewObject>();
        for(Achievement a : aa ){
            Location l = new Location("");
            l.setLongitude(a.getLocationE());
            l.setLatitude(a.getLocationN());
            DistanceViewObject dvoTemp = new DistanceViewObject(a.getName(),
                    String.format("%.2f", (l.distanceTo(location)/1000))+" Km",
                    getResources().getDrawable(R.drawable.ic_plusone_medium_off_client));
            temp.add(dvoTemp);
        }
        return temp;
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
            setupListViewTest();
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
