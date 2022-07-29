package com.smartpixels.dragMapLocationPicker.utility;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

public class MapUtility {

    public static String KEY_LATITUDE = "Latitude";
    public static String KEY_LONGITUDE = "Longitude";
    public static String KEY_ADDRESS_LINE_1 = "AddressLine1";
    public static String KEY_ADDRESS_LINE_2 = "AddressLine2";

    public static final String MAP_URL = "https://maps.googleapis.com";

    public static String apiKey = "";
    public static Location currentLocation = null;
    public static Dialog popupWindow;
    public static String ADDRESS = "address";
    public static String LATITUDE = "lat";
    public static String LONGITUDE = "long";

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = null;
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception ex) {

        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToast(Context context, String message) {
        try {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } catch (Exception ex) {

        }
    }

    public static void hideProgress() {
        try {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
