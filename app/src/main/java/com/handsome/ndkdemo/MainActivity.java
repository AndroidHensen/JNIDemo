package com.handsome.ndkdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.handsome.ndkdemo.Bean.Human;
import com.handsome.ndkdemo.Bean.Man;
import com.handsome.ndkdemo.Utils.FileUtils;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public native String accessField();

    public native void accessStaticField();

    public native void accessMethod();

    public native void accessStaticMethod();

    public native Date accessConstructor();

    public native void accessNonvirtualMethod();

    public native String chineseChars();

    public native void giveArray(int[] array);

    public native int[] getArray(int len);

    public native void localRef();

    public native void createGlobalRef();

    public native String getGlobalRef();

    public native void deleteGlobalRef();

    public native void exeception();

    public native void cached();

    public native static void initIds();

    static {
        System.loadLibrary("jni_study");
    }

    public String key = "Hensen";
    public static int count = 5;
    public Human human = new Man();
    public int[] array = {9, 100, 10, 37, 5, 11};
    public String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public String normal_path = SD_CARD_PATH + File.separatorChar + "bg.jpg";
    public String crypt_path = SD_CARD_PATH + File.separatorChar + "bg_crypt.jpg";
    public String decrypt_path = SD_CARD_PATH + File.separatorChar + "bg_decrypt.jpg";
    public String pattern_path = SD_CARD_PATH + File.separatorChar + "bg_%d.jpg";
    public String merge_path = SD_CARD_PATH + File.separatorChar + "bg_merge.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void accessField(View view) {
        accessField();
        Toast.makeText(this, "accessField():" + key, Toast.LENGTH_SHORT).show();
    }

    public void accessStaticField(View view) {
        accessStaticField();
        Toast.makeText(this, "accessStaticField():" + count, Toast.LENGTH_SHORT).show();
    }

    public void accessMethod(View view) {
        accessMethod();
        Toast.makeText(this, "accessMethod()", Toast.LENGTH_SHORT).show();
    }

    public void accessStaticMethod(View view) {
        accessStaticMethod();
        Toast.makeText(this, "accessStaticMethod()", Toast.LENGTH_SHORT).show();
    }

    public void accessConstructor(View view) {
        accessConstructor();
        Toast.makeText(this, "accessConstructor()", Toast.LENGTH_SHORT).show();
    }

    public void accessNonvirtualMethod(View view) {
    }

    public void chineseChars(View view) {
        Toast.makeText(this, "chineseChars():" + chineseChars(), Toast.LENGTH_SHORT).show();
    }

    public void giveArray(View view) {
        giveArray(array);
        Toast.makeText(this, "giveArray()", Toast.LENGTH_SHORT).show();
    }

    public void getArray(View view) {
        getArray(10);
        Toast.makeText(this, "getArray()", Toast.LENGTH_SHORT).show();
    }

    public void localRef(View view) {
        localRef();
        Toast.makeText(this, "localRef()", Toast.LENGTH_SHORT).show();
    }

    public void createGlobalRef(View view) {
        createGlobalRef();
        Toast.makeText(this, "createGlobalRef()", Toast.LENGTH_SHORT).show();
    }

    public void getGlobalRef(View view) {
        Toast.makeText(this, "getGlobalRef()" + getGlobalRef(), Toast.LENGTH_SHORT).show();
    }

    public void deleteGlobalRef(View view) {
        deleteGlobalRef();
        Toast.makeText(this, "deleteGlobalRef()", Toast.LENGTH_SHORT).show();
    }

    public void cached(View view) {
        cached();
        Toast.makeText(this, "cached()", Toast.LENGTH_SHORT).show();
    }

    public void exeception(View view) {
        Toast.makeText(this, "exeception()", Toast.LENGTH_SHORT).show();
        try {
            exeception();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initIds(View view) {
        initIds();
        Toast.makeText(this, "initIds()", Toast.LENGTH_SHORT).show();
    }

    public void crypt(View view) {
        FileUtils.crypt(normal_path, crypt_path);
        Toast.makeText(this, "FileUtils.crypt()", Toast.LENGTH_SHORT).show();
    }

    public void decrypt(View view) {
        FileUtils.decrypt(crypt_path, decrypt_path);
        Toast.makeText(this, "FileUtils.decrypt()", Toast.LENGTH_SHORT).show();
    }

    public void diff(View view) {
        FileUtils.diff(normal_path, pattern_path, 5);
        Toast.makeText(this, "FileUtils.diff()", Toast.LENGTH_SHORT).show();
    }

    public void patch(View view) {
        FileUtils.patch(pattern_path, 5, merge_path);
        Toast.makeText(this, "FileUtils.patch()", Toast.LENGTH_SHORT).show();
    }

    /**
     * accessMethod中调用
     *
     * @param max
     * @return
     */
    public int genRandomInt(int max) {
        int i = new Random().nextInt(max);
        return i;
    }

    /**
     * accessStaticMethod中调用
     *
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
