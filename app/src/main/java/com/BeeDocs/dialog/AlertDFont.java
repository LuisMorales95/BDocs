package com.BeeDocs.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;
import android.widget.TextView;

public class AlertDFont extends AlertDialog {
    protected AlertDFont(Context context) {
        super(context);
    }
    
    protected AlertDFont(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    
    protected AlertDFont(Context context, int themeResId) {
        super(context, themeResId);
        Typeface face=Typeface.createFromFile("font/droidsans.ttf");
    
        TextView textView = (TextView) findViewById(android.R.id.message);
        TextView Title = (TextView) findViewById(android.R.id.title);
        textView.setTypeface(face);
        Title.setTypeface(face);
    }
}
