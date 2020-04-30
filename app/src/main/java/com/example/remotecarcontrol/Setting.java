package com.example.remotecarcontrol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Setting extends AppCompatActivity {

    TextView textView;
    Switch switch1;
    RelativeLayout uploader_area;
    TextInputLayout textInputIP;
    TextInputEditText edit;
    Intent intent;
    Button save;
    Button giro;
    Button manuell;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);



        textView = findViewById(R.id.text_enable);
        switch1 = findViewById(R.id.switch1);
        uploader_area = (RelativeLayout) findViewById(R.id.pop_up);
        textInputIP = findViewById(R.id.text_input_ip);
        save = findViewById(R.id.button_confirm);
        edit = findViewById(R.id.edit_text);
        giro = findViewById(R.id.button_giro);
        manuell = findViewById(R.id.button_manuell);

        giro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this, SettingsGUI.class);
                startActivity(intent);
            }
        });

        manuell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this, SettingsManuel.class);
                startActivity(intent);
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String ip = prefs.getString("IP", "http://192.168.1.8:5000");


        String fieldValue = ip;

        textInputIP.setHintAnimationEnabled(fieldValue == null);
        edit.setText(fieldValue);


        //Popup Settings
        Window window = getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        window.setLayout((int) (width*.9),(int) (height*.45));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //window.setBackgroundDrawable(new ColorDrawable(Color.alpha(255)));
        window.setGravity(Gravity.TOP|Gravity.CENTER);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 300;
        window.setAttributes(params);


        switch1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(switch1.isChecked()){
                    switch1.setChecked(false);
                }else{
                    switch1.setChecked(true);
                }
                return true;
            }
        });
    }

    protected boolean validateIP(){
        String UsernameInput = textInputIP.getEditText().getText().toString().trim();

        if(UsernameInput.isEmpty()){
            textInputIP.setError("Field cant be empty");
            return false;
        }else{
            textInputIP.setError(null);
            textInputIP.setErrorEnabled(false);
            return true;
        }
    }

    public void confirmInput(View v){
        if(!validateIP()){
            return;
        }
        String input = "IP changed to: " + textInputIP.getEditText().getText().toString();
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IP", textInputIP.getEditText().getText().toString());
        editor.apply();

        if(switch1.isChecked()) {
            //switch enabled
            intent = new Intent(Setting.this, Introduction.class);
            startActivity(intent);
            finish();
        }else{
            //switch disabled
            finish();
        }

    }


}
