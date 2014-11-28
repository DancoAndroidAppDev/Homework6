package net.cozz.danco.homework6;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getCanonicalName();
    public static final String VIEW_FLAG = "View flag";
    public static final String VIEW_FLOWER = "View flower";

    private int fontSize = 4;

    private int position;

    public int getFontSize() {
        return fontSize;
    }

    private GridView gridView;
    private TextView textView;

    private Random rand = new Random();
    private final List<Integer> cellIndecies = new ArrayList<Integer>(50);
    private List<String> theStates;
    private List<String> capitals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //enable the Android home button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setNavigationIcon(R.drawable.ic_launcher);
        }

        Intent data = getIntent();
        String username = data.getStringExtra("username");
        if (username == null) {
            // try to read username from prefs
            SharedPreferences prefs = getSharedPreferences(LoginActivity.APP_SHARED_PREFS,
                    Activity.MODE_PRIVATE);
            username = prefs.getString("username", "dude");
        }

        setTitle("Welcome " + username);

        final String[] flowers = getResources().getStringArray(R.array.flowers);
        theStates = Arrays.asList(getResources().getStringArray(R.array.states));
        capitals = Arrays.asList(getResources().getStringArray(R.array.capitals));

        FlowersDataSource datasource = new FlowersDataSource(this);

        try {
            datasource.open();
            int i = 0;
            for (String state : theStates) {
                datasource.addFlower(state, flowers[i++]);
            }
            List <Flower> capitalsList = datasource.getFlowers();
            Log.d("", capitalsList.toString());
        } catch (SQLException e) {
            Log.d("MyActivity", "unable to open datasource");
        }

        loadContent();
    }


    private void loadContent() {
        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new CellViewAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                Log.i(TAG, "setting click listener for position: " + position);
                Toast.makeText(getApplicationContext(),
                        "Capital is " + capitals.get(position), Toast.LENGTH_LONG).show();

                showView(position, ViewFlagActivity.class);
            }
        });

        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.increase_font) {
            fontSize += 1;
            loadContent();
            return true;
        } else if (id == R.id.decrease_font) {
            fontSize -= 1;
            loadContent();
            return true;
        } else if (id == R.id.logout) {
            doLogout();
        } else if (id == R.id.animate) {
            doAnimate();
        } else if (id == R.id.animate_all) {
            doAnimation();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doLogout() {
        String prefs = LoginActivity.APP_SHARED_PREFS;

        SharedPreferences sharedPrefs = getSharedPreferences(prefs, Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

        prefsEditor.remove("username");
        prefsEditor.apply();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        textView = (TextView) v;
        String stateName = textView.getText().toString();
        menu.setHeaderTitle(stateName);
        position = theStates.indexOf(stateName);
        menu.add(0, v.getId(), 0, VIEW_FLOWER);
        menu.add(0, v.getId(), 0, VIEW_FLAG);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(VIEW_FLAG)) {
            showFlagView();
        } else if (item.getTitle().equals(VIEW_FLOWER)) {
            showFlowerView();
        }

        return true;
    }

    public void doAnimate() {
        int position = rand.nextInt(gridView.getChildCount());

        Log.i(TAG, "animating cell at position: " + position);
        View view = gridView.getChildAt(position);
        cellIndecies.add(position);

        TextView textView = (TextView) view.findViewById(R.id.state_cell_row1);

        Log.i(TAG, String.format("animating: %s", textView.getText().toString()));
        rotate(textView, false);
    }


    public void doAnimation() {
        TextView textView = null;
        int position = rand.nextInt(gridView.getChildCount());
        while (cellIndecies.contains(position)) {
            position = rand.nextInt(gridView.getChildCount());
        }

        Log.i(TAG, "animating cell at position: " + position);
        View view = gridView.getChildAt(position);
        if (view.isEnabled()) {
            cellIndecies.add(position);
            if (cellIndecies.size() == 50) {
                cellIndecies.clear();
            }
            textView = (TextView) view.findViewById(R.id.state_cell_row1);
//        view = (TextView) findViewById(R.id.state_cell_row1);

            Log.i(TAG, String.format("animating: %s", textView.getText().toString()));
            rotate(textView, true);
        } else {
            doAnimation();
        }
    }


    public void animateColor(TextView textView){
        final int RED = 0xffFF8080;
        final int BLUE = 0xff8080FF;

        ValueAnimator colorAnim = ObjectAnimator.ofInt(textView, "textColor", RED, BLUE);
        colorAnim.setDuration(1000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();

    }


    public void rotate(final TextView textView, final boolean cycle){
        Animation animation;

        List<Integer> animations = new ArrayList<Integer>(4);
        animations.add(R.anim.blinking);
        animations.add(R.anim.fade_in);
        animations.add(R.anim.fade_out);
        animations.add(R.anim.rotate);
        animations.add(R.anim.color);

        animation =
                AnimationUtils.loadAnimation(this, animations.get(rand.nextInt(animations.size())));

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i(TAG, "animation ended");
                if (cycle) {
                    doAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                textView.clearAnimation();
                Log.i(TAG, "animation repeating");
            }
        });

        textView.startAnimation(animation);
    }


    public void fadeInOut(final TextView textView){
        Animation animFadeIn, animFadeOut;

        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                continueAnim(textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                doAnimation();
            }
        });

        textView.startAnimation(animFadeIn);
    }


    public void continueAnim(final TextView textView){
        Animation animFadeOut;

        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        textView.startAnimation(animFadeOut);
    }


    private void showFlagView() {
        showView(position, ViewFlagActivity.class);
    }


    private void showFlowerView() {
        showView(position, FlowerViewActivity.class);
    }


    private void showView(final int position, Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
