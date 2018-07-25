package com.example.eagleeyenetworks.eagleeye_mediaplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    protected EditText emailInput;
    protected EditText passwordInput;
    OkHttpClient httpClient;
    EENCookieJar cookieJar = new EENCookieJar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.email_field);
        passwordInput = findViewById(R.id.password_field);

        // Quick hack
        httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    void showError(final int string_resource) {
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, string_resource, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submitClicked(View button) {
        String user = emailInput.getText().toString();
        String pass = passwordInput.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            showError(R.string.invalid_email);
            return;
        }

        if (pass.length() < 6) {
            showError(R.string.pass_too_short);
            return;
        }

        authenticate(user, pass);
    }

    void authenticate(String user, String pass) {

        RequestBody payload = new FormBody.Builder().add("username", user).add("password", pass).add("realm", "eagleeyenetworks").build();

        Request authRequest = new Request.Builder().url("https://login.eagleeyenetworks.com/g/aaa/authenticate").method("POST", payload).build();
        httpClient.newCall(authRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError(R.string.server_error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch(response.code()) {
                    case 200:
                        try {
                            JSONObject responseObject = new JSONObject(response.body().string());
                            authorize(responseObject.getString("token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 401:
                        showError(R.string.invalid_user_or_pass);
                        break;
                    default:
                        showError(R.string.server_error);
                        break;
                }
            }
        });
    }

    void authorize(String token) {
        RequestBody payload = new FormBody.Builder().add("token", token).build();
        Request request = new Request.Builder().url("https://login.eagleeyenetworks.com/g/aaa/authorize").method("POST", payload).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError(R.string.server_error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch(response.code()) {
                    case 200:
                        list_devices();
                        break;
                    default:
                        showError(R.string.server_error);
                }
            }
        });
    }

    void list_devices() {
        Request list_devices = new Request.Builder().url("https://login.eagleeyenetworks.com/g/list/devices").build();
        httpClient.newCall(list_devices).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError(R.string.server_error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch(response.code()) {
                    case 200:
                        Intent intent = new Intent(LoginActivity.this, CameraListActivity.class);
                        intent.putExtra("camera_list", response.body().string());
                        intent.putExtra("auth_token", cookieJar.getAuthToken());
                        startActivity(intent);
                        break;
                    default:
                        showError(R.string.server_error);
                }
            }
        });
    }


    class EENCookieJar implements CookieJar {
        private List<Cookie> cookies;
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies.size() > 0) {
                this.cookies = cookies;
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (cookies != null)
                return cookies;
            return new ArrayList<>();
        }

        public String getAuthToken() {
            for (Cookie cookie : cookies) {
                if (cookie.name().equalsIgnoreCase("auth_key")) {
                    return cookie.value();
                }
            }
            return "";
        }
    }

}
