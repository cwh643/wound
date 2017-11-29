package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static Toast toast = null;

    public static void showLongToast(Context context, String pMsg) {
        if (toast == null) {
            toast = Toast.makeText(context, pMsg, Toast.LENGTH_LONG);
        } else {
            toast.setText(pMsg);
        }
        toast.show();
    }

    public static void showShortToast(Context context, String pMsg) {
        if (toast == null) {
            toast = Toast.makeText(context, pMsg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(pMsg);
        }
        toast.show();
    }

	/*
	protected void showShortToast(int pResId) {
		showShortToast(getString(pResId));
	}
*/
}
