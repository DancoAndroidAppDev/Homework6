package net.cozz.danco.homework6;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by danco on 11/22/14.
 */
public class LoginActivity extends ActionBarActivity implements Button.OnClickListener {
    public static final String APP_SHARED_PREFS = LoginActivity.class.getPackage().toString();

    private static final String TAG = LoginActivity.class.getCanonicalName();
    private static final String ALGORITHM = "SHA-1";

    private EditText username;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        username = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);

        Button prefsBtn = (Button)findViewById(R.id.button_login);
        prefsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isEmpty(username.getText())) {
            Toast.makeText(this, "Username can't be empty", Toast.LENGTH_LONG).show();
        } else if (isEmpty(password.getText())) {
            Toast.makeText(this, "Password can't be empty", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, MyActivity.class);
            saveToPrefs(username.getText().toString().trim(), password.getText().toString().trim());
            startActivity(intent);
        }
    }


    public boolean isEmpty(Editable editable){

        if (editable.equals(null)) {
            Log.i(TAG, "isEmpty - editable is null");

            return true;
        }
        else {
            return editable.toString().trim().length() == 0;
        }
    }

    private void saveToPrefs(final String username, final String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }
        md.update(password.getBytes(Charset.defaultCharset()));
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) {
            sb.append(String.format("%02X", b));
        }

        SharedPreferences sharedPrefs = getApplicationContext()
                .getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor;

        prefsEditor = sharedPrefs.edit();
        prefsEditor.putString("username", username);
        prefsEditor.putString("password", sb.toString());
        prefsEditor.apply();
    }
}
