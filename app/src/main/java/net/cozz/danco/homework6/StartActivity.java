package net.cozz.danco.homework6;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by danco on 11/22/14.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Class<? extends Activity> activityClass;
        if (userIsLoggedIn())
            activityClass = MyActivity.class;
        else
            activityClass = LoginActivity.class;

        Intent newActivity = new Intent(this, activityClass);
        startActivity(newActivity);
    }


    private boolean userIsLoggedIn() {

        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(
                LoginActivity.APP_SHARED_PREFS, Activity.MODE_PRIVATE);

        return !sharedPrefs.getString("username", "").isEmpty();
    }
}
