package com.example.bear.loveexpress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private LoveView mLove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLove = (LoveView) findViewById(R.id.loveView);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mLove.getBaseLine() < 0) {
                            timer.cancel();
                        }
                        mLove.setBaseLine(mLove.getBaseLine() - 20);
                    }
                });
            }
        }, 0, 5000);
    }
}
