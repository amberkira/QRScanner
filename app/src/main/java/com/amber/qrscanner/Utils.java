package com.amber.qrscanner;

import android.content.Context;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amber_sleepeanuty on 2017/10/11.
 */

public class Utils {
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static int calStringLength(Context context,String text){
        int length = 0;
        char[] sub = text.toCharArray();

        return length;
    }

    public static boolean regx(String target,String reg){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

}
