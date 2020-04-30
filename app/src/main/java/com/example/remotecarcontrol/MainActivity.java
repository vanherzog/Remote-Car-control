package com.example.remotecarcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;



// graue Farbe für die Pfeile #FFFAFA

public class MainActivity extends AppCompatActivity {
    Button btn;
    Intent intent;
    protected int type = 0;
    Spinner dropdown;
    ImageView settings;
    private static final String TAG ="MainActivity";
    Integer quelle= 0;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            btn = findViewById(R.id.button);
            dropdown = findViewById(R.id.spinner1);
            settings = findViewById(R.id.settings);

           SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
           SharedPreferences.Editor editor = prefs.edit();

           quelle = prefs.getInt("Quelle", 0);

            switch(quelle) {
                case 0:
                    break;
                case 1:
                    intent = new Intent(MainActivity.this, Ask.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(MainActivity.this, Introduction.class);
                    startActivity(intent);
                    break;
                case 3:
                    editor.putInt("Quelle", 2);
                    editor.apply();
                    break;
            }

            //Dropdown
            String[] items = new String[]{"Choose one", "Manuel Control", "GUI Control"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            //set the spinners adapter to the previously created one.
            dropdown.setAdapter(adapter);

            //Start Button
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateDropdown()) {
                        switch (type) {
                            case 1:
                                intent = new Intent(MainActivity.this, ControlManuel.class);
                                break;
                            case 2:
                                intent = new Intent(MainActivity.this, ControlGUI.class);
                                break;
                        }
                        startActivity(intent);
                    }
                }
            });


            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //editor.putInt("intro", 0);
                    intent = new Intent(MainActivity.this, Setting.class);
                    startActivity(intent);
                }
            });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        quelle = prefs.getInt("Quelle", 0);

        switch(quelle) {
            case 0:
                break;
            case 1:
                intent = new Intent(MainActivity.this, Ask.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(MainActivity.this, Introduction.class);
                startActivity(intent);
                break;
            case 3:
                editor.putInt("Quelle", 2);
                editor.apply();
                break;
        }
    }

    protected boolean validateDropdown(){
        if (dropdown.getSelectedItem().toString().trim().equals("Choose one")) {
            Toast.makeText(MainActivity.this, "Please choose one mode", Toast.LENGTH_SHORT).show();
            dropdown.setBackgroundColor(getResources().getColor(R.color.red));
            //dropdown.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            return false;
        }else if(dropdown.getSelectedItem().toString().trim().equals("Manuel Control")){
            type = 1;
        }else if(dropdown.getSelectedItem().toString().trim().equals("GUI Control")){
            type = 2;
        }
        return true;
    }
}
