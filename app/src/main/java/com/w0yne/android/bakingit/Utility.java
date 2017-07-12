package com.w0yne.android.bakingit;

import android.content.Context;

public final class Utility {

    public static boolean isTablet(Context context) {
        return context.getString(R.string.screen_type).equals("tablet");
    }
}
