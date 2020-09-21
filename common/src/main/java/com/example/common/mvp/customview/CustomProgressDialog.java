package com.example.common.mvp.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.example.common.R;


public class CustomProgressDialog extends Dialog {
    private static final String TAG = "CustomProgressDialog";

    public CustomProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog, "");
    }

    public CustomProgressDialog(Context context, String strMessage) {
        this(context, R.style.CustomProgressDialog, strMessage);
    }

    public CustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.dialog_progress_loading);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView tvMsg = (TextView) this.findViewById(R.id.progress_text);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //Log.i(TAG, "----->onWindowFocusChanged: " + hasFocus);
        if (!hasFocus) {
            //Log.i(TAG, "----->dismiss");
            //dismiss();
        }
    }

    public void setMessage(String msg) {
        TextView tvMsg = (TextView) this.findViewById(R.id.progress_text);
        if (tvMsg != null) {
            tvMsg.setText(msg);
        }
    }
}
