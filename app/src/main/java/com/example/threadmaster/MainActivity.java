package com.example.threadmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.TestLooperManager;
import android.text.style.EasyEditSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

*루퍼 : 메시지 큐에 들어오는 메시지를 지속적으로 보면서 하나씩 처리하게함
-메인 스레드는 UI 객체들을 처리하기 위해 메시지 큐와 루퍼를 사용하지만, 작업 스레드에는 루퍼가 없음
 -> 순차적으로 작업을 수행하고 싶으면 루퍼 생성 후 실행해야됨

*AsyncTask의 장점 : 코드 간소화(클래스 안에 스레드 코드와 UI 접근 코드를 한꺼번에 넣을 수 있음)

-doInBackground() : 작업 스레드에서 수행, execute()를 호출할 때 사용된 파라미터를 배열로 전달 받음
-onPreExecute() : 메인 스레드에서 수행, 백그라운드 작업 수행 전에 호출됨, 초기화 작업에 사용됨
-onProgressUpdate() : 메인 스레드에서 수행, 백그라운드 진행 상태를 표시함, 작업 중간마다 UI 객체에 접근하는 경우에 사용됨
-onPostExecute() : 메인 스레드에서 수행, 백그라운드 작업이 끝난 후에 호출됨, 메모리 리소스를 해제하는 작업에 사용됨
*/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //int value = 0;

    TextView tv, tv2;
    EditText edt;

    //TestHandler testHandler;

    Handler handler = new Handler(); // API 기본 핸들러 객체 생성

    LooperThread looperThread;

    ProgressBar progressBar;

    ProgressTask progressTask;

    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressbar);

        Button btn3 = (Button)findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 태스크 객체 생성 후 실행하기
                progressTask = new ProgressTask();
                progressTask.execute();
            }
        });
        Button btn4 = (Button)findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressTask.cancel(true);
            }
        });

        edt = (EditText)findViewById(R.id.edt);
        tv2 = (TextView)findViewById(R.id.tv2);

        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = edt.getText().toString();
                Message msg = Message.obtain(); // 메시지 객체 생성
                msg.obj = str; // 입력한 문자열을 메시지 객체의 obj에 할당

                looperThread.looperHandler.sendMessage(msg); // 작업 스레드 안에 있는 핸들러로 메시지 객체를 스레드로 전송
            }
        });

        looperThread = new LooperThread();

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

    class ProgressTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() { // 초기화
            super.onPreExecute();
            value = 0;
            progressBar.setProgress(value);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            while(isCancelled() == false) {
                value++;
                if(value >= 100) {
                    break;
                } else {
                    publishProgress(value); // 100이 넘지 않으면 -> 진행중이면 호출됨
                }

                try {
                        Thread.sleep(1000);
                    } catch (Exception e) {}
                }
            return value;
        }

        @Override
        protected void onProgressUpdate(Integer... values) { // 중간중간 UI 업데이트
            progressBar.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(Integer integer) { // doInBackground() 결과값 전달 받음(완료 상태)
            progressBar.setProgress(0);
        }

        @Override
        protected void onCancelled() {
            progressBar.setProgress(0);
        }
    }

    class LooperThread extends Thread {
        LooperHandler looperHandler = new LooperHandler();

        public void run() { // 이 스레드에서는 메시지 객체를 전달 받을 수 있음
            Looper.prepare(); // 루퍼를 생성
            Looper.loop(); // 무한 루프를 돌며 메시지 큐에 쌓인 메시지나 러너블 객체를 핸들러에 전달함
        }

        class LooperHandler extends Handler {
            @Override
            public void handleMessage(@NonNull Message msg) {
                final String getStr = "루퍼를 이용한 스레드 : " + msg.obj; // 작업 스레드 안에서 전달 받은 메시지 처리

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv2.setText(getStr);
                    }
                });
                super.handleMessage(msg);
            }
        }
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