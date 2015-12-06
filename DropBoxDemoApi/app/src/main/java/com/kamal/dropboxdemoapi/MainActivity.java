package com.kamal.dropboxdemoapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    //path of the file to be uploaded here
    String path= Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/photo.jpg";
    final static private String APP_KEY = "";
    final static private String APP_SECRET = "";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    DropboxAPI.Entry response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *   i am using this mode . you can use the Async task to upload and download file
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        //Generate dropbox session from the generated keys
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        //Check if user has already authorized the dropbox app for you..
        if(!getAccessToken().equalsIgnoreCase("")){
            mDBApi.getSession().setOAuth2AccessToken(getAccessToken());
        }else{
            //if not authorized. i.e for the first time then
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
        }
        ((Button) findViewById(R.id.uploaddata)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(path);
            }
        });

        ((Button) findViewById(R.id.downloadfile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
            }
        });
    }

    protected void onResume() {
        super.onResume();
        //if first time and save the Access Token here
        if(getAccessToken().equalsIgnoreCase("")) {
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();
                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                    saveAccessToken(accessToken);
                } catch (IllegalStateException e) {
                    Log.i("Error", "Error authenticating", e);
                }
            }
        }
    }

    private void saveAccessToken(String accessToken){
        SharedPreferences sharedPreferences=getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("accessToken", accessToken).commit();
    }
    private String getAccessToken(){
        SharedPreferences sharedPreferences=getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken","");
    }

    public void uploadFile(String file_path) {
        File file = new File(file_path);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            response = mDBApi.putFile("/photo.jpg", inputStream,
                    file.length(), null, null);
            if(response.rev!=null){
                Toast.makeText(this,"File Uploaded successfully..",Toast.LENGTH_LONG).show();
            }
            Log.i("Success", "The uploaded file's rev is: " + response.rev);
        } catch (DropboxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void downloadFile(String file_path) {
        //Path where you want to stored the downloaded file form dropbox
        File file = new File(file_path+"/testphoto.jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DropboxAPI.DropboxFileInfo info = null;
        try {
            info = mDBApi.getFile("/photo.jpg", null, outputStream, null);
            if(info.getMetadata().rev!=null){
                Toast.makeText(this,"File Downloaded successfully..",Toast.LENGTH_LONG).show();
            }
            Log.i("Success", "The file's rev is: "
                    + info.getMetadata().rev);
        } catch (DropboxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
