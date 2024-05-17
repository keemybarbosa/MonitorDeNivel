package com.example.monitordenivel;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String obterDensidadeTela(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);

            int density = metrics.densityDpi;

            if (density == DisplayMetrics.DENSITY_LOW) {
                return "Low Density (ldpi)";
            } else if (density == DisplayMetrics.DENSITY_MEDIUM) {
                return "Medium Density (mdpi)";
            } else if (density == DisplayMetrics.DENSITY_HIGH) {
                return "High Density (hdpi)";
            } else if (density == DisplayMetrics.DENSITY_XHIGH) {
                return "Extra High Density (xhdpi)";
            } else if (density == DisplayMetrics.DENSITY_XXHIGH) {
                return "Extra Extra High Density (xxhdpi)";
            } else if (density == DisplayMetrics.DENSITY_XXXHIGH) {
                return "Extra Extra Extra High Density (xxxhdpi)";
            } else {
                return "Unknown Density: " + density;
            }
        }
        return "Unknown Density";
    }

    public static int random(int max){
        return (int) (Math.random() * max);
    }

    public static String getDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        return formattedDate;
    }
}
