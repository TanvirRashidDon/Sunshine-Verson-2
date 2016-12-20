package app.com.lily.tanvir.sunshine_verson_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tanvir on 12/17/2016.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent )
    {
        if(new MainActivityFragment().isOnline(context)){
            new MainActivityFragment().updateWather();
        }
    }
}


