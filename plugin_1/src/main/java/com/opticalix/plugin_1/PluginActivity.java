package com.opticalix.plugin_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by opticalix@gmail.com on 16/5/10.
 */
public class PluginActivity extends Activity {
    public static Intent getLaunchIntent(Context context) throws ClassNotFoundException {
        return new Intent(context, Class.forName("com.opticalix.plugin_1.PluginActivity"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_plugin);
    }
}
