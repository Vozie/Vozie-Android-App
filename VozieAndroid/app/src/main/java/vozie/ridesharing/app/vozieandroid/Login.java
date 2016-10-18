package vozie.ridesharing.app.vozieandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shane.vozieandroid.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    /**
     *
     */
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callBackManager;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        //info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();
            }
        });

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    public void login(){
        callBackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));

        loginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    //handle error
                                } else {
                                    //Success
                                    startActivity(new Intent(Login.this, MapsActivity.class));

                                }
                                try {
                                    String jsonResult = String.valueOf(json);
                                    String name = json.getString("name");
                                    String email = json.getString("email");
                                    String id = json.getString("id");
                                    //setText(id,name,email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("Login attemp canceled");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Login attemp failed");
                error.printStackTrace();
            }
        });
    }

    public void setText(String str1, String str2, String str3){
        info.setText("ID: " + str1 + "\n NAME:  " + str2 + "\n EMAIL: " + str3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Login.super.onBackPressed();
    }
}
