package app.com.lily.tanvir.sunshine_verson_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private String LOG_TAG="DON : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Log.e(LOG_TAG,"OnCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }
        if(id==R.id.action_map){
            openPreferedLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferedLocationInMap() {
        SharedPreferences preferences= PreferenceManager.
                getDefaultSharedPreferences(this);
        String location=preferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else {
            Log.d("Error", "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG_TAG,"OnStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG,"OnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(LOG_TAG,"OnPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(LOG_TAG,"OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG,"OnDestroy");
    }

}
