package com.example.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ToggleButton;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ToggleButton button = findViewById(R.id.toggleButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                status = status ? false : true;
                System.out.println("Did button click; status is " + status);
            }
        });



    }

    public boolean doNothing(){
        return true;
    }
}
