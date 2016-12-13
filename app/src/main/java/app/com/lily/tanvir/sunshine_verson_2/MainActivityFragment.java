package app.com.lily.tanvir.sunshine_verson_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        setHasOptionsMenu(true);

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
                updateWather();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWather() {
        FatchWethertask fatchWethertask=new FatchWethertask();
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location=preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        fatchWethertask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWather();
    }

    public class FatchWethertask extends AsyncTask<String,Integer,String[]>{

        public final String LOG_TAG=FatchWethertask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... strings) {
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;

            String mode="json";
            String unit="meric";
            String id="APPID";
            int dayCount=7;


            try{
                final String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";

                Uri uri=Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter("q",strings[0])
                        .appendQueryParameter("mode",mode)
                        .appendQueryParameter("unit",unit)
                        .appendQueryParameter("cnt",Integer.toString(dayCount))
                        .appendQueryParameter(id, getString(R.string.open_weathermap_api_key))
                        .build();
                URL url=new URL(uri.toString());
                //URL url=new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=kushtia,bd" +
                  //      "&mode=json&unit=metric&cnt=7&APPID=b862c9689c6bc5d1cb38222f9b7f54c7");

                urlConnection =(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream=urlConnection.getInputStream();

                if(inputStream==null){
                    return null;
                }

                StringBuffer buffer=new StringBuffer();
                reader=new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line=reader.readLine())!=null){
                    buffer.append(line+"\n");
                }

                if(buffer.length()==0){
                    return null;
                }

                forcasttJsonString=buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG,"error",e);
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,"Error closing reader",e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forcasttJsonString,dayCount);
            } catch (JSONException e) {
                Log.e(LOG_TAG,"error",e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(strings!=null){
                adapter.clear();
                for(String s:strings){
                    adapter.add(s);
                }
            }
    }
    /* The date/time conversion code is going to be moved outside the asynctask later,
       * so for convenience we're breaking it out into its own method now.
       */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.

        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit=sharedPreferences.getString(getString(R.string.pref_unit_key),
                getString(R.string.unit_default));

        if(!unit.equals(getString(R.string.unit_default))){
            high=high*1.8+32;
            low=low*1.8+32;
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }
        return resultStrs;
    }

    }
}
