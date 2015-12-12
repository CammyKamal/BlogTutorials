package com.example.kamalvaid.marshmallowfeatures;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareViews();
    }

    private void prepareViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ((Button)findViewById(R.id.permission_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // here check whether app is runnung on 23 or greater then check for permission else perform normal functionality
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else {
                    Toast.makeText(MainActivity.this, "Device is below 6.0 do your work", Toast.LENGTH_LONG).show();
                }
            }
        });
        ((Button)findViewById(R.id.write_permission_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkCallPermission();
                } else {
                    Toast.makeText(MainActivity.this, "Device is below 6.0 do your work", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // This condition checks whether user has earlier denied the permission or not just by clicking on deny in the permission dialog.
            //Remember not on the never ask check box then deny in the permission dialog
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                /**
                 * Show an explanation to user why this permission in needed by us. Here using alert dialog to show the permission use.
                 */
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This permission was denied earlier by you. This permission is required to access your location for app accuracy purposes.So, in order to use this feature please allow this permission by clickking ok.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        1);
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
                // Just ask for the permission for first time. This block will come into play when user is trying to use feature which requires permission grant.
                //So for the first time user will be into this else block. So just ask for the permission you need by showing default permission dialog
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }
        }else {
            // If permission is already granted by user then we will be into this else block. So do whatever is required here
            Toast.makeText(this,"Permission Aleardy granted",Toast.LENGTH_LONG).show();
        }
    }
    private void checkCallPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                /**
                 *
                 */
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This permission was denied earlier by you. This permission is required to call from app .So, in order to use this feature please allow this permission by clicking ok.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        2);
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
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},2);
            }
        }else {
            Toast.makeText(this,"Permission Aleardy granted",Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  This method will be invoked when user allows or deny's a permission from the permission dialog so take actions accordingly.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, Do the Location -related task you need to do.
                    Toast.makeText(this, "COARSE Permission granted", Toast.LENGTH_LONG).show();
                } else {
                    // This block will be called when user has denied the permission. There will be two possibilities.
                    //1. User has simple denied the permission by clicking on the deny button the permission dialog.
                    //2. User has ticked the never show dialog and denied the permission from the permission dialog.
                    String permission = permissions[0];
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    if (!showRationale) {
                        //We will be in this block when User has ticked the never show dialog and denied the permission from the permission dialog
                        //Here we can not request the permission again if user has denied the permission with never ask option enabled.
                        //Only way is to show a imfo dialog and ask user to grant the permission from settings.
                        Toast.makeText(this, "COARSE Permission Denied with never show options", Toast.LENGTH_LONG).show();
                    } else {
                        //We will be in this block when User has denied the permission from the permission dialog and not clicked on the never show again checkbox.
                        //We can show a explaination why we need this permission and request permission again.
                        Toast.makeText(this, "COARSE Permission Denied", Toast.LENGTH_LONG).show();
                        //permission denied,Disable the functionality that depends on this permission.
                    }

                }
                break;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Call Permission granted", Toast.LENGTH_LONG).show();
                } else {
                    String permission = permissions[0];
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                    if (!showRationale) {
                        Toast.makeText(this, "Call Permission Denied with never show options", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Call Permission Denied", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
        }
    }
}

