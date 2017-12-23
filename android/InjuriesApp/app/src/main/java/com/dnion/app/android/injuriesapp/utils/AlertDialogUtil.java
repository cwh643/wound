package com.dnion.app.android.injuriesapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.dnion.app.android.injuriesapp.R;

public class AlertDialogUtil {

    private static AlertDialog mAlertDialog;

    private static Handler handler = new Handler();

    private static Runnable runnable = new Runnable() {
        public void run() {
            if (mAlertDialog != null) {
                mAlertDialog.show();
            }
        }
    };

    public static void showAlertDialogAsync(Context context, View view, String title, String Message, AlertCallback cb) {
        new ProcessTask(context, view, title, Message, cb).execute();
    }

    public static AlertDialog showAlertDialog(Context context, String TitleID, String Message) {
        mAlertDialog = new AlertDialog.Builder(context).setTitle(TitleID)
                .setMessage(Message).create();
        handler.postDelayed(runnable, 100);
        return mAlertDialog;
    }

    public static void dismissAlertDialog(Context context) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public static AlertDialog showAlertDialog(Context context, String TitleID, String Message, DialogInterface.OnClickListener listener) {
        mAlertDialog = new AlertDialog.Builder(context).setTitle(TitleID).setMessage(Message)
                .setPositiveButton(R.string.confirm, listener).show();
        return mAlertDialog;
    }

    public static AlertDialog showConfirmDialog(Context context, String TitleID, String Message, DialogInterface.OnClickListener listener) {
        mAlertDialog = new AlertDialog.Builder(context).setTitle(TitleID).setMessage(Message)
                .setPositiveButton(R.string.confirm, listener).setNegativeButton(R.string.cancel, null).show();
        return mAlertDialog;
    }

    public static AlertDialog showConfirmDialog(Context context, Integer TitleID, Integer Message, DialogInterface.OnClickListener listener) {
        mAlertDialog = new AlertDialog.Builder(context).setTitle(TitleID).setMessage(Message)
                .setPositiveButton(R.string.confirm, listener).setNegativeButton(R.string.cancel, null).show();
        return mAlertDialog;
    }

    public static AlertDialog showInputDialog(Context context, EditText editText, DialogInterface.OnClickListener listener) {
        mAlertDialog = new AlertDialog.Builder(context).setTitle("请输入").setView(editText)
                .setPositiveButton(R.string.confirm, listener).setNegativeButton(R.string.cancel, null).show();
        return mAlertDialog;
    }

    public static class AlertCallback {
        public void pre() {
        }

        public void exec() {
        }

        public void post() {
        }
    }

    public static class ProcessTask extends AsyncTask<String, Object, Long> {

        private View view;
        private Context context;
        private AlertCallback cb;
        private String title;
        private String msg;


        public ProcessTask(Context context, View view, String title, String msg, AlertCallback cb) {
            this.view = view;
            this.context = context;
            this.cb = cb;
            this.title = title;
            this.msg = msg;
        }

        @Override
        protected void onPreExecute() {
            AlertDialogUtil.showAlertDialog(context,
                    title,
                    msg);
            view.setClickable(false);
            cb.pre();
        }

        @Override
        protected Long doInBackground(String... params) {
            cb.exec();
            // 复制深度信息
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            cb.post();
            view.setClickable(true);
            AlertDialogUtil.dismissAlertDialog(context);
        }

    }

}
    /*
    public void setAlertDialogIsClose(DialogInterface pDialog, Boolean pIsClose) {
		try {
			Field field = pDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(pDialog, pIsClose);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected AlertDialog showAlertDialog(String pTitle, String pMessage,
			String pPositiveButtonLabel, String pNegativeButtonLabel,
			DialogInterface.OnClickListener pOkClickListener,
			DialogInterface.OnClickListener pCancelClickListener,
			DialogInterface.OnDismissListener pDismissListener) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle(pTitle)
				.setMessage(pMessage)
				.setPositiveButton(pPositiveButtonLabel, pOkClickListener)
				.setNegativeButton(pNegativeButtonLabel, pCancelClickListener)
				.show();
		if (pDismissListener != null) {
			mAlertDialog.setOnDismissListener(pDismissListener);
		}
		return mAlertDialog;
	}

	protected ProgressDialog showProgressDialog(int pTitelResID,
			String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener) {
		String title = getResources().getString(pTitelResID);
		return showProgressDialog(title, pMessage, pCancelClickListener);
	}

	protected ProgressDialog showProgressDialog(String pTitle, String pMessage,
			DialogInterface.OnCancelListener pCancelClickListener) {
		mAlertDialog = ProgressDialog.show(this, pTitle, pMessage, true, true);
		mAlertDialog.setOnCancelListener(pCancelClickListener);
		return (ProgressDialog) mAlertDialog;
	}
*/
