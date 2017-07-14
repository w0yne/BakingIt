package com.w0yne.android.bakingit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Utility {

    public static boolean isTablet(Context context) {
        return context.getString(R.string.screen_type).equals("tablet");
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}
