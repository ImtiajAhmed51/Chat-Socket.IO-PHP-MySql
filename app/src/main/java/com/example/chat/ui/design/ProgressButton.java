package com.example.chat.ui.design;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.chat.R;

public class ProgressButton {
    private CardView cardView;
    private Context context;
    private Animation fade_in;
    private Animation fade_out;
    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private TextView textView;

    public ProgressButton(Context context, View view) {
        this.fade_in = AnimationUtils.loadAnimation(context, R.anim.button_fade_in);
        this.fade_out = AnimationUtils.loadAnimation(context, R.anim.button_fade_out);
        this.cardView = (CardView) view.findViewById(R.id.cardView);
        this.layout = (ConstraintLayout) view.findViewById(R.id.constraint_layout);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.textView = (TextView) view.findViewById(R.id.buttonTextView);
        this.context = context;
    }

    public void buttonSet(String button_Name) {
        this.progressBar.setAnimation(this.fade_out);
        this.progressBar.setVisibility(View.GONE);
        this.textView.setText(button_Name);
        int i = Build.VERSION.SDK_INT;
        this.layout.setBackground(ContextCompat.getDrawable(this.context, R.drawable.second_custom_button));
    }

    public void buttonActivated() {
        int i = Build.VERSION.SDK_INT;
        this.layout.setBackground(ContextCompat.getDrawable(this.context, R.drawable.second_custom_button_click));
        this.progressBar.setAnimation(this.fade_in);
        this.progressBar.setVisibility(View.VISIBLE);
        this.textView.setText("Please wait...");
    }

    public void buttonActivatedWithOutProgressNText() {
        int i = Build.VERSION.SDK_INT;
        this.layout.setBackground(ContextCompat.getDrawable(this.context, R.drawable.second_custom_button_click));

    }

    public void buttonFinished() {
        this.layout.setBackgroundColor(this.cardView.getResources().getColor(R.color.green));
        this.progressBar.setAnimation(this.fade_out);
        this.progressBar.setVisibility(View.GONE);
        this.textView.setText("Done");
    }
}
