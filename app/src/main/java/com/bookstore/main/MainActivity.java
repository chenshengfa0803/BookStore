package com.bookstore.main;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    public FloatButton mainFloatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFloatButtonMenu();
    }

    public void createFloatButtonMenu() {
        SubFloatButton subFab_camera = new SubFloatButton(this, getResources().getDrawable(R.drawable.sub_floatbutton_camera), null);
        SubFloatButton subFab_chat = new SubFloatButton(this, getResources().getDrawable(R.drawable.sub_floatbutton_chat), null);
        SubFloatButton subFab_location = new SubFloatButton(this, getResources().getDrawable(R.drawable.sub_floatbutton_location), null);
        mainFloatButton = (FloatButton) findViewById(R.id.FloatActionButton);
        mainFloatButton.addSubFloatButton(subFab_camera)
                .addSubFloatButton(subFab_chat)
                .addSubFloatButton(subFab_location)
                .createFloatButtonMenu();
    }
}
