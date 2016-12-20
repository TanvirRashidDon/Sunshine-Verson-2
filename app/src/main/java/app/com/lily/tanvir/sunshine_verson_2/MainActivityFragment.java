package app.com.lily.tanvir.sunshine_verson_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    String forcasttJsonString=null;
    public String forecast;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

    }

    public MainActivityFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                forecast=adapter.getItem(i);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action_refress:
                Context context=getContext();
                if(isOnline(context)){
                    updateWather();
                }else {
                    Toast.makeText( context, "NO Network : " , Toast.LENGTH_SHORT ).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isOnline(Context context) {

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
            Log.d("Network Available ", "Flag No 1");
            return true;
        }
        Log.d("NO NETWORK AVAILABLE","Flag No 2");
        return false;
    }

    public void updateWather() {
        FetchWeatherTask fetchWeatherTask=new FetchWeatherTask(getContext(),adapter);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location=preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        fetchWeatherTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context=getContext();
        if(isOnline(context)){
            updateWather();
        }else {
            Toast.makeText( context, "NO Network : " , Toast.LENGTH_SHORT ).show();
        }
    }
    
}
