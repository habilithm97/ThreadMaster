package com.example.threadmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
*스레드 : 동시 수행이 가능한 작업 단위
*멀티 스레드 : 다수의 스레드가 동시에 공통 메모리 리소스에 접근할 때 효율적인 처리가 가능하지만 데드락이 발생할 수 있음
*데드락 : 동시에 여러 곳에서 요청이 생겼을 때 어떤 것을 먼저 처리해야할지 판단할 수 없어 발생하는 시스템 문제

-UI를 처리할 때에는 메인 스레드를 사용해야함
-작업 스레드로 UI를 처리하고 싶을 때에는 핸들러를 사용해야함
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadTest thread = new ThreadTest();
                thread.start();
            }
        });
    }

    class ThreadTest extends Thread {
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                value += 1;
                Log.d(TAG,  "스레드  값 : " + value);
            }
        }
    }
}