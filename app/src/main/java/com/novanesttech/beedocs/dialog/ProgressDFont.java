package com.novanesttech.beedocs.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;


public class ProgressDFont extends ProgressDialog {
    public ProgressDFont(Context context) {
        super(context);
    }
    
    public ProgressDFont(Context context, int theme) {
        super(context, theme);
        Typeface face=Typeface.createFromFile("font/droidsans.ttf");
    
        TextView textView = (TextView) findViewById(android.R.id.message);
        TextView Title = (TextView) findViewById(android.R.id.title);
        textView.setTypeface(face);
        Title.setTypeface(face);
    }
}
