package com.org.curesoft.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

 ImageView mProfile;
    TextView mEmail;
    ProgressDialog mProgress;
    LoginButton loginButton;

    CallbackManager callbackManager;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();

        mProfile = (ImageView) findViewById(R.id.profile);
        mEmail= (TextView) findViewById(R.id.email);
        loginButton= (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mProgress=new ProgressDialog(MainActivity.this);
                mProgress.setMessage("Retreiving data");
                mProgress.show();
                String accesstoken=loginResult.getAccessToken().getToken();
                GraphRequest request=GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                    mProgress.dismiss();
                        Log.d("response",response.toString());
                        getData(object);
                    }
                });

                Bundle b=new Bundle();
                b.putString("fields","id,email");
                request.setParameters(b);
                request.executeAsync();
            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        if(AccessToken.getCurrentAccessToken()!=null){
            mEmail.setText(AccessToken.getCurrentAccessToken().getUserId());
        }

    }

    private void getData(JSONObject object) {
        try{
            URL profile_picture=new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=150");
            Picasso.with(this).load(profile_picture.toString()).into(  mProfile);
            mEmail.setText(object.getString("email"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
