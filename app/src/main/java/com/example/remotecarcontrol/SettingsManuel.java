package com.example.remotecarcontrol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsManuel extends AppCompatActivity {

    ImageView down;
    ImageView up;
    ImageView right;
    ImageView left;
    Button save;


    Integer x;
    Integer y;

    Integer x2;
    Integer y2;

    Integer _xDelta;
    Integer _yDelta;
    Integer ersti;

    Integer X;
    int Y;


    private ViewGroup rootLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_manuel);

        rootLayout = (ViewGroup) findViewById(R.id.frame);

        final Saving_settings savingsettings = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));
        ersti = 0;

        down = rootLayout.findViewById(R.id.arrow_down);
        up = rootLayout.findViewById(R.id.arrow_up);
        right = rootLayout.findViewById(R.id.arrow_right);
        left = rootLayout.findViewById(R.id.arrow_left);
        save = findViewById(R.id.button_save);





        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingsettings.savePositionB(X,Y);
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        final Saving_settings savingsettings = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            changePosition(down, savingsettings.xB, savingsettings.yB);
            changePosition(up, savingsettings.xF, savingsettings.yF);
            changePosition(right, savingsettings.xR, savingsettings.yR);
            changePosition(left, savingsettings.xL, savingsettings.yL);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void changePosition(ImageView image,Integer x, Integer y){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(150, 150);
        image.setLayoutParams(layoutParams);
        image.setOnTouchListener(new ChoiceTouchListener());
        image.setTop(X);
        image.setLeft(Y);
    }


    private final class ChoiceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent event) {
            final Saving_settings setting = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));
                X = (int) event.getRawX();
                Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
            }
            rootLayout.invalidate();
            event.setLocation(20, 130);
            setting.saveData();
            return true;
        }
    }



}
