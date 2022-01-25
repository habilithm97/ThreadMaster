package com.example.threadmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
*스레드 : 동시 수행이 가능한 작업 단위
*멀티 스레드 : 다수의 스레드가 동시에 공통 메모리 리소스에 접근할 때 효율적인 처리가 가능하지만 데드락이 발생할 수 있음
*데드락 : 동시에 여러 곳에서 요청이 생겼을 때 어떤 것을 먼저 처리해야할지 판단할 수 없어 발생하는 시스템 문제

-UI를 처리할 때에는 메인 스레드를 사용해야함
-작업 스레드로 UI를 처리하고 싶을 때에는 핸들러를 사용해야함

*핸들러
-메시지 큐 : 코드를 순차적으로 수행, 메인 스레드에서 처리할 메시지를 전달하는 역할을 핸들러 클래스가 담당함

*작업 스레드에서 메인 스레드로 전달하기
 -> 핸들러가 관리하는 메시지 큐에서 처리할 수 있는 메시지 객체를 참조함(obtainMessage()) -> 메시지 객체를 반환받음
 -> 메시지 객체에 데이터를 넣은 후 sendMessage()로 메시지 큐에 넣음
 -> 메시지 큐에 들어간 메시지는 핸들러가 순차적으로 처리함 -> handleMessage()에 정의한 코드는 메인 스레드에서 실행

 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //int value = 0;

    TextView tv;

    //TestHandler testHandler;

    Handler handler = new Handler(); // API 기본 핸들러 객체 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadTest thread = new ThreadTest();
                thread.start();
            }
        });

        //testHandler = new TestHandler(); // 핸들러는 메인 스레드에서 싫행
    }

    class ThreadTest extends Thread {
        int value = 0;

        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                value += 1;
                Log.d(TAG,  "스레드  값 : " + value);
                //tv.setText("스레드 값 : " + value);

                /*
                Message message = testHandler.obtainMessage(); // 메시지 객체 참조 -> 메시지 객체 반환 받음
                Bundle bundle = new Bundle();
                bundle.putInt("value", value);
                message.setData(bundle); // 메시지 객체에 데이터를 넣음

                testHandler.sendMessage(message); // 메시지 객체를 핸들러를 이용해서 메시지 큐에 넣음
                 */

                handler.post(new Runnable() { // 러너블 객체를 핸들러의 post()로 전달해주면 이 객체에 전달된 run() 안의 코드들은 메인 스레드에서 실행됨
                    @Override
                    public void run() {
                        tv.setText("핸들러를 이용한 스레드 값 : " + value); // 핸들러 내부 메인 스레드에서 실행
                    }
                });
            }
        }
    }

    class TestHandler extends Handler { // 핸들러는 메인 스레드에서 동작이 가능하므로 UI에 직접 접근이 가능함

        @Override
        public void handleMessage(@NonNull Message msg) { // 핸들러 안에서 전달 받은 메시지 객체를 처리함
            super.handleMessage(msg);

            Bundle bundle = msg.getData(); // 번들 객체 참조
            int value = bundle.getInt("value");
            tv.setText("핸들러를 이용한 스레드 값 : " + value);
        }
    }
}