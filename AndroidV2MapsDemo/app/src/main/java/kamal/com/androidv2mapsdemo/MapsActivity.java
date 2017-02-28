package kamal.com.androidv2mapsdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap googleMap;
    private SupportMapFragment fragment;
    private GoogleApiClient mGoogleApiClient;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setupDynamicMapFragment();
        if(checkGooglePlayServicesAvailable())
        initializeMap();

    }

    private void setupDynamicMapFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initializeMap() {
        if (googleMap == null) {
            fragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        if (googleMap != null) {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            if (Build.VERSION.SDK_INT < 23) {
                googleMap.setMyLocationEnabled(true);
                buildGoogleApiClient();
            }else{
                checkFineLocationPermission();
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this,"buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    /**
     * Check whether Google Play Services are available.
     *
     * If not, then display dialog allowing user to update Google Play Services
     *
     * @return true if available, or false if not
     */
    private boolean checkGooglePlayServicesAvailable( )
    {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        final int status = googleAPI.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS)
        {
            return true;
        }

        Log.e("", "Google Play Services not available: " + GooglePlayServicesUtil.getErrorString(status));

        if (googleAPI.isUserResolvableError(status))
        {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            if (errorDialog != null)
            {
                errorDialog.show();
            }
        }

        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallbacks<LocationSettingsResult>() {
            @Override
            public void onSuccess(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates s= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MapsActivity.this,
                                    100);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.e("","");
                try {
                    status.startResolutionForResult(MapsActivity.this, 100);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }) ;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 ){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    /**
     *   Write external Permission check
     */
    private void checkFineLocationPermission(){
        if ((ContextCompat.checkSelfPermission(this,  android.Manifest.permission.ACCESS_FINE_LOCATION))
                != PackageManager.PERMISSION_GRANTED) {
            // This condition checks whether user has earlier denied the permission or not just by clicking on deny in the permission dialog.
            //Remember not on the never ask check box then deny in the permission dialog
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                /**
                 * Show an explanation to user why this permission in needed by us. Here using alert dialog to show the permission use.
                 */
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This permission was denied earlier by you. This permission is required to use card.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        4);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else {
                //Just ask for the permission for first time. This block will come into play when user is trying to use feature which requires permission grant.
                //So for the first time user will be into this else block. So just ask for the permission you need by showing default permission dialog
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 4);
            }
        }else {
            // If permission is already granted by user then we will be into this else block. So do whatever is required here
            //            Toast.makeText(this,"Permission Already granted",Toast.LENGTH_LONG).show();
            googleMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
        }
    }


    /**
     *  This method will be invoked when user allows or deny's a permission from the permission dialog so take actions accordingly.
     * @param requestCode : returns requestCode
     * @param permissions : returns permissions
     * @param grantResults : returns grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 4:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    buildGoogleApiClient();
                } else {
                    String permission = permissions[0];
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    if (!showRationale) {
                        //We will be in this block when User has ticked the never show dialog and denied the permission from the permission dialog
                        //Here we can not request the permission again if user has denied the permission with never ask option enabled.
                        //Only way is to show a imfo dialog and ask user to grant the permission from settings.
                        Toast.makeText(this, "Write Storage Permission Denied with never show options." +
                                        "Please manually enable the permission and re-start the app to use this feature.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Write Storage Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
        }
    }


}
