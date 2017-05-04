package com.example.askoura.rxjavaexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void first(View view) {
        startActivity(new Intent(this, RxJavaSimpleActivity.class));

    }

    public void second(View view) {
        startActivity(new Intent(this, ColorsActivity.class));
    }

    public void third(View view) {
        startActivity(new Intent(this, BooksActivity.class));
    }

    public void fourth(View view) {
        startActivity(new Intent(this, SchedulerActivity.class));
    }
}
