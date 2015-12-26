package com.bookstore.main;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    public FloatButton mainFloatButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void createFloatButtonMenu()
    {
        mainFloatButton = (FloatButton)findViewById(R.id.FloatActionButton);
    }
}
