package com.example.remotecarcontrol;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;

import static com.example.remotecarcontrol.SettingsGUI.getTouchPositionFromDragEvent;


public class ControlGUI extends AppCompatActivity {
    ImageView back;
    ImageView forward;

    WebView webView;


    Integer FB = 0;
    Integer LR = 0;

    Point touchPosition;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private SensorEventListener gravityEventlistener;

    static SockelConnection socket;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gui);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String ip = prefs.getString("IP", "192.168.1.8:5000");

        changeStatic(ip);

        back = findViewById(R.id.arrow_down);
        forward = findViewById(R.id.arrow_up);


        sendd(back,-1,10);

        gravityControl();

        showSystemUI();
        drag_dropp(forward);

        webStream();

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
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String ip = prefs.getString("IP", "http://192.168.1.8:5000");
        webView = (WebView) findViewById(R.id.web_view2);
        webView = MyWebStreamConnection.connectWebView(webView,ip);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(gravityEventlistener, gravitySensor,SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(gravityEventlistener);
    }


    private void gravityControl(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gravityEventlistener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] >= 3) {
                    if (event.values[1] >= 7) {
                        LR = -2;
                        socket.send(LR + "," + FB);
                        System.out.println(LR + "," + FB);
                    } else {
                        LR = -1;
                        socket.send(LR + "," + FB);
                        System.out.println(LR + "," + FB);
                    }
                    // hier Rechts
                } else if (event.values[1] <= -3) {
                    if (event.values[1] <= -7 ) { //5
                        LR = 2;
                        socket.send(LR + "," + FB);
                        System.out.println(LR + "," + FB);
                    } else {
                        LR = 1;
                        socket.send(LR + "," + FB);
                        System.out.println(LR + "," + FB);
                    }
                    // hier Links
                } else if (event.values[1] < 3 && event.values[2] > -3) {
                    LR = 0;
                    socket.send(LR + "," + FB);
                    System.out.println(LR + "," + FB);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }


    @SuppressLint("ClickableViewAccessibility")
    public void drag_dropp(final ImageView image) {
        final Saving_settings savingsettings = Saving_settings.getInstance(getSharedPreferences("SharedPreferences2", MODE_PRIVATE));

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
                        System.out.println("AAAAAAAAAAAAAA" + touchPosition.y);

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
                        System.out.println("AAAAAAAAAAA :" + touchPosition);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        image.setX(touchPosition.x);
                        image.setY(touchPosition.y);

                        break;
                    default:
                        break;
                }/**if(image.toString().equals("btnB")){
                 image.setX(X-(image.getWidth()/2));
                 image.setY(Y-(image.getHeight()/2));

                 //image.setX(savingsettings.xB);
                 //image.setY(savingsettings.yB);
                 }else{
                 image.setX(savingsettings.xF);
                 image.setY(savingsettings.yF);
                 }**/

                return true;
            }
        });
    }




    @SuppressLint("ClickableViewAccessibility")
    public void sendd(final ImageView image, final Integer up, final Integer side){

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(up == 10 && side != 10){
                    LR = side;
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

                }
                socket.send(LR + "," + FB);
                return true;
            }

        });
    }


   /** @SuppressLint("ClickableViewAccessibility")
    public void sendd(final Integer up, final Integer side){
        if(up == 10 && side != 10){
            LR = side;
        }else if(up != 10 && side == 10){
            FB = up;
        }
        socket.send(LR + "," + FB);
        if(){
            FB = 0;
        }else{
            LR = 0;
        }
        socket.send(LR + "," + FB);

    }**/





    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
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
