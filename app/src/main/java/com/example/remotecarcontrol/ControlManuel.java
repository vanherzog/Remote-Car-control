package com.example.remotecarcontrol;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import android.webkit.WebView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;


import java.net.URISyntaxException;


public class ControlManuel extends AppCompatActivity{

    ImageView back;
    ImageView forward;
    ImageView right;
    ImageView left;
    Switch hard_soft;
    Point touchPosition;
    static SockelConnection socket;

    Integer FB = 0;
    Integer LR = 0;

    WebView webView;
    private Bitmap bitmap;
    String ip;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manuell);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        ip = prefs.getString("IP", "192.168.1.8:5000");

        changeStatic(ip);


        back = findViewById(R.id.arrow_down);
        forward = findViewById(R.id.arrow_up);
        right = findViewById(R.id.arrow_right);
        left = findViewById(R.id.arrow_left);

        hard_soft = findViewById(R.id.hard_soft);

        hard_soft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(hard_soft.isChecked()){
                    hard_soft.setChecked(false);
                }else{
                    hard_soft.setChecked(true);
                }
                return false;
            }
        });


        webStream();

        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache(true);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == event.ACTION_DOWN ||event.getAction() == event.ACTION_MOVE) {
                    bitmap = webView.getDrawingCache();
                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);
                    if(r + b + g < 120){
                        back.setColorFilter(Color.WHITE);
                        forward.setColorFilter(Color.WHITE);
                        right.setColorFilter(Color.WHITE);
                        left.setColorFilter(Color.WHITE);
                    }else{
                        back.setColorFilter(Color.BLACK);
                        forward.setColorFilter(Color.BLACK);
                        right.setColorFilter(Color.BLACK);
                        left.setColorFilter(Color.BLACK);
                    }

                }
                return true;
            }
        });


        sendd(back, -1,10);
        sendd(right,10,-1);
        sendd(left, 10,1);
        showSystemUI();
        drag_dropp(forward);
    }

    public static void changeStatic(String ip){
            try {
                socket = SockelConnection.getInstance(ip);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
    }




    @SuppressLint("SetJavaScriptEnabled")
    public void webStream(){
        webView = (WebView) findViewById(R.id.web_view);
        webView = MyWebStreamConnection.connectWebView(webView, ip);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void drag_dropp(final ImageView image) {
        final Saving_settings savingsettings = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {


                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(null, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });

        image.getRootView().setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        float X = event.getX();
                        float Y = event.getY();
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setX(0);
                        v.setY(0);

                        FB = 0;
                        socket.send(LR + "," + FB);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        touchPosition = getTouchPositionFromDragEvent(v, event);
                        if(touchPosition.y < 1000 && touchPosition.y > 570){
                            FB = 1;
                            socket.send(LR + "," + FB);
                        }else if(touchPosition.y < 570 && touchPosition.y > 440){
                            FB = 2;
                            socket.send(LR + "," + FB);
                        }else if(touchPosition.y < 440){
                            FB = 3;
                            socket.send(LR + "," + FB);
                        }

                        //do something with the position (a scroll i.e);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        image.setX(touchPosition.x);
                        image.setY(touchPosition.y);

                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void sendd(final ImageView image, final Integer up, final Integer side){

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Integer s = side;
                if(hard_soft.isChecked()){
                    if(side == 1){
                        s += 1;
                    }else if(side == -1){
                        s -= 1;
                    }
                }
                if(up == 10 && side != 10){
                    LR = s;
                }else if(up != 10 && side == 10){
                    FB = up;
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    socket.send(LR + "," + FB);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){  //ACTION_UP
                    if(image.equals(back) || image.equals(forward)){
                        FB = 0;
                    }else{
                        LR = 0;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                socket.send(LR + "," + FB);
                return true;
            }

        });
    }

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

    public void pos(){
        float density = getResources().getDisplayMetrics().density;
        right.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = (int)(back.getWidth() / density);
        int height = (int)( back.getHeight()/density);
        int y =  (int)(back.getTop()/density);
        int x = (int)(back.getLeft()/density);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
            pos();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Closes the Bar after 5sek again
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                hideSystemUI();
                handler.postDelayed(this, 5000);
            }
        }, 5000);

    }

}
