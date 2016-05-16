package com.opticalix.testdynamicapk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import ctrip.android.bundle.loader.BundlePathLoader;
import ctrip.android.bundle.runtime.RuntimeArgs;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test_plugin_project) {
            testPluginProject();
            return true;
        }else if (id == R.id.action_test) {
            testHotFix();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void testPluginProject(){
        try {
            Intent intent = new Intent(MainActivity.this, Class.forName("com.opticalix.plugin_1.PluginActivity"));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "You should use x_final.apk in build-outputs. Act not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 需要在sdcard/Download下放置新的dex
     * unchecked
     */
    private void testHotFix() {
        String clsName = "com.opticalix.hotfix_lib.Provider";
        String exDexPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator + HotFixHelper.DEX_NAME;
        File exDexFile = new File(exDexPath);
        ArrayList<File> files = new ArrayList<>();
        files.add(exDexFile);
        String optimizedPath = HotFixHelper.getInnerDexDirPath(this);
        File optimizedFile = new File(optimizedPath);

        //use dexCL来加载新的dex/apk java文件
        PathClassLoader pathClassLoader = (PathClassLoader) HotFixHelper.class.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(exDexPath, optimizedPath, null, pathClassLoader);
        toastByMethod(HotFixHelper.loadInstance(dexClassLoader, clsName), "getString");

        //test 动态替换之前的类 fixme
        clsName = "com.opticalix.testdynamicapk.Dummy";
        Object dummy = HotFixHelper.loadInstance(dexClassLoader, clsName);
        toastByField(dummy, "foo");//old
        try {
            BundlePathLoader.installBundleDexs(RuntimeArgs.androidApplication.getClassLoader(), optimizedFile, files, true);
            dummy = HotFixHelper.loadInstance(dexClassLoader, clsName);
            toastByField(dummy, "foo");//new
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void toastByMethod(Object o, String methodName) {
        try {
            Toast.makeText(MainActivity.this, (String) o.getClass().getMethod(methodName, new Class[]{}).invoke(o), Toast.LENGTH_SHORT).show();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void toastByField(Object o, String fieldName) {
        try {
            Toast.makeText(MainActivity.this, (String) o.getClass().getField(fieldName).get(o), Toast.LENGTH_SHORT).show();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
