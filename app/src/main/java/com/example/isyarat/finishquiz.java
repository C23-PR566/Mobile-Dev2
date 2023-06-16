package com.example.isyarat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizCompletionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_completion);

        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        Button homeButton = findViewById(R.id.homeButton);

        imageView.setImageResource(R.drawable.accept1);
        textView.setText("Selamat, kamu telah menyelesaikan quiz dengan baik!");
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizCompletionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
