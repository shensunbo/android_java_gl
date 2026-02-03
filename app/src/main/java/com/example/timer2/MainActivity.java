package com.example.timer2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView greetingText = findViewById(R.id.greetingText);
        greetingText.setText(getString(R.string.greeting_message, "Android"));
    }
}