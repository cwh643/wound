package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.support.v7.widget.ActivityChooserView;
import android.view.Display;
import android.view.Gravity;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.BaseActivity;

public class ToastUtil {
    public static Toast toast = null;

    public static void showLongToast(Context context, String pMsg) {
        if (toast == null) {
            toast = Toast.makeText(context, pMsg, Toast.LENGTH_LONG);
        } else {
            toast.setText(pMsg);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showLongToastButtom(Context context, String pMsg) {
        if (toast == null) {
            toast = Toast.makeText(context, pMsg, Toast.LENGTH_LONG);
        } else {
            toast.setText(pMsg);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showLongToastTop(Context context, String pMsg) {
        if (toast == null) {
            toast = Toast.makeText(context, pMsg, Toast.LENGTH_LONG);
        } else {
            toast.setText(pMsg);
        }
        Display display = ((BaseActivity)context).getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        toast.setGravity(Gravity.TOP, 0, height / 12);
        toast.show();
    }

    public static void showShortToast(Context context, String pMsg) {
        if (toast == null) {
            toast = Toast.makeText(context, pMsg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(pMsg);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

	/*
	protected void showShortToast(int pResId) {
		showShortToast(getString(pResId));
	}
*/
}
