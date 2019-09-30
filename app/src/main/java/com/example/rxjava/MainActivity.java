package com.example.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create(View view) {
        startActivity(new Intent(this, CreateOperatorsActivity.class));
    }

    public void thread(View view) {
        startActivity(new Intent(this, RxThreadActivity.class));
    }


    public void transform(View view) {
        startActivity(new Intent(this, TransformOperatorsActivity.class));
    }

    public void merge(View view) {
        startActivity(new Intent(this, MergeOperatorsActivity.class));
    }

    public void backPressure(View view) {
        startActivity(new Intent(this, BackPressureActivity.class));
    }
}

