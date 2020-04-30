package com.example.remotecarcontrol;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsGUI extends AppCompatActivity {

    ImageView btnB;
    ImageView btnF;
    RelativeLayout.LayoutParams layoutParams;

    Point touchPosition;
    Integer X;
    int Y;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_gui);

        final Saving_settings savingsettings = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));

        btnB = findViewById(R.id.arrow_down);
        btnF = findViewById(R.id.arrow_up);

        btnB.setX(savingsettings.xB);
        btnB.setY(savingsettings.yB);

        btnF.setX(savingsettings.xF);
        btnF.setY(savingsettings.yB);


        drag_drop(btnF);
        drag_drop(btnB);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void drag_drop(final ImageView image){
        final Saving_settings savingsettings = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(image);
                    image.startDrag(data, shadowBuilder, image, 0);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        image.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        v.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        v.setX(x_cord);
                        v.setY(y_cord);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DROP:
                        break;
                    default:
                        break;
                }
                savingsettings.saveData();
                return true;
            }
        });    }

    /**
     * @param item  the view that received the drag event
     * @param event the event from {@link android.view.View.OnDragListener#onDrag(View, DragEvent)}
     * @return the coordinates of the touch on x and y axis relative to the screen
     */
    public static Point getTouchPositionFromDragEvent(View item, DragEvent event) {
        Rect rItem = new Rect();
        item.getGlobalVisibleRect(rItem);
        return new Point(rItem.left + Math.round(event.getX()), rItem.top + Math.round(event.getY()));
    }
}
