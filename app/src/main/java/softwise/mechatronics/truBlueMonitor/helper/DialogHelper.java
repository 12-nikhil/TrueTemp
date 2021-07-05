package softwise.mechatronics.truBlueMonitor.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AlertDialog;

import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanWithDialogListener;


public class DialogHelper {

    public static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context, String msg) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.create();
        mProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showMessageDialog(Context context,String title, String message) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MY_DIALOG)).create();
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void conformationDialog(Context context, String message, IBooleanListener listener) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MY_DIALOG)).create();
            dialog.setTitle(context.getString(R.string.lbl_confirmation));
            dialog.setMessage(message);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog1, which) -> {
                listener.callBack(true);
                dialog.dismiss();
            });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog1, which) -> {
                dialog.dismiss();
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void conformationDialogCallBack(Context context, String message, IBooleanWithDialogListener listener) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MY_DIALOG)).create();
            dialog.setTitle(context.getString(R.string.lbl_confirmation));
            dialog.setMessage(message);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog1, which) -> {
                listener.callBack(true,dialog);
            });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog1, which) -> {
                dialog.dismiss();
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void messageDialogWithCallBack(Context context, String message, IBooleanListener listener) {

        try {
            if ("SensorTemperatureActivity".equals(String.valueOf(context.getClass().getSimpleName()))) {
                return;
            }
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MY_DIALOG)).create();
            dialog.setTitle(context.getString(R.string.lbl_alert_message));
            dialog.setMessage(message);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Connect", (dialog1, which) -> {
                listener.callBack(true);
                dialog.dismiss();
            });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog1, which) -> {
                listener.callBack(false);
                dialog1.dismiss();
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void messageDialog(Context context, String title, String message, IBooleanListener listener) {
        /*if ("SensorTemperatureActivity".equals(String.valueOf(context.getClass().getSimpleName()))) {
            listener.callBack(true);
        }else {*/
        try {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MY_DIALOG)).create();
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", (dialog1, which) -> {
                listener.callBack(true);
                dialog.dismiss();
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //}
    }

    public static void warningDialog(Context context, String title, String message, IBooleanListener listener) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MY_DIALOG)).create();
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", (dialog1, which) -> {
                listener.callBack(true);
                dialog.dismiss();
            });
            dialog.setCancelable(false);
            dialog.show();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
