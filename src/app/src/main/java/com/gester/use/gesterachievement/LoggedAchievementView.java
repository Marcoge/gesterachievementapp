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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gester.use.gesterachievement.objects.Achievement;
import com.gester.use.gesterachievement.objects.LoggedAchievement;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoggedAchievementView extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private ListView achievementDoneList;
    private ListView achievementTodoList;
    private AdapterDistanceView adapterTodo;
    private AdapterDistanceView adapterDone;
    private ArrayList<DistanceViewObject> todoArray = new ArrayList<DistanceViewObject>();
    private ArrayList<DistanceViewObject> doneArray = new ArrayList<DistanceViewObject>();
    private Location location;
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
        setContentView(R.layout.activity_logged_achievement);

        buildGoogleApiClient();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }


        //Todo List setup
        fillAchievementDoneList();

//        achievementDoneList = (ListView) findViewById(R.id.listViewLoggedAchievementDone);
//        ArrayAdapter<String> adapterDone = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, doneArray);
//        achievementDoneList.setAdapter(adapterDone);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_achievement, menu);
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

    private void fillAchievementTodoList(){

        achievementTodoList = (ListView) findViewById(R.id.listViewLoggedAchievementTodo);

        //Preparing Query String
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.connector));
        sb.append("getachievements?");
        sb.append("n=");
        sb.append(String.valueOf(location.getLatitude()));
        sb.append("&e=");
        sb.append(String.valueOf(location.getLongitude()));
        sb.append("&d=10");


        //requesting data from server
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        GsonRequest<Achievement[]> getAchievement =
                new GsonRequest<Achievement[]>(sb.toString(), Achievement[].class,

                        new Response.Listener<Achievement[]>() {
                            @Override
                            public void onResponse(Achievement[] response) {
                                List<Achievement> al = (List<Achievement>) Arrays.asList(response);
                                todoArray = achievementToDistanceViewDistance(al);
                                adapterTodo.addAll(todoArray);

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("dat funzt nicht");
                    }
                });

        queue.add(getAchievement);

        adapterTodo = new AdapterDistanceView(this, todoArray);
        achievementTodoList.setAdapter(adapterTodo);
    }

    private void fillAchievementDoneList() {

        achievementDoneList = (ListView) findViewById(R.id.listViewLoggedAchievementDone);

        //Preparing Query String
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.connector));
        sb.append("getloggedachievements?");
        sb.append("u=");
        sb.append("test@test.com");



        //requesting data from server
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        GsonRequest<LoggedAchievement[]> getLoggedAchievement =
                new GsonRequest<LoggedAchievement[]>(sb.toString(), LoggedAchievement[].class,

                        new Response.Listener<LoggedAchievement[]>() {
                            @Override
                            public void onResponse(LoggedAchievement[] response) {
                                List<LoggedAchievement> la = (List<LoggedAchievement>) Arrays.asList(response);
                                doneArray = achievementToDistanceViewTime(la);
                                adapterDone.addAll(doneArray);

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("dat funzt nicht");
                    }
                });

        queue.add(getLoggedAchievement);

        adapterDone = new AdapterDistanceView(this, doneArray);
        achievementDoneList.setAdapter(adapterDone);

    }


    private ArrayList<DistanceViewObject> achievementToDistanceViewTime(List<LoggedAchievement> la){
        ArrayList<DistanceViewObject> t = new ArrayList<DistanceViewObject>();

        for(LoggedAchievement b : la){
            DistanceViewObject dvtemp = new DistanceViewObject(b.getUserEmail(), b.getTimeStamp().toString(),
                    getResources().getDrawable(R.drawable.ic_plusone_medium_off_client));
            t.add(dvtemp);
        }


        return t;
    }


    private ArrayList<DistanceViewObject> achievementToDistanceViewDistance(List<Achievement> aa){
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
            fillAchievementTodoList();
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
