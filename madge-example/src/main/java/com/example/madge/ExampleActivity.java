package com.example.madge;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ExampleActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ViewGroup content = ActivityViewGroup.get(this);
    LayoutInflater.from(this).inflate(R.layout.example, content);
  }
}
