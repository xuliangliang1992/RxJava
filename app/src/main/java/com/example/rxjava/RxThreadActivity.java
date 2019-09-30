package com.example.rxjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xll
 * @date 2018/11/29
 */
public class RxThreadActivity extends AppCompatActivity {

    private String TAG = "RxThreadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
    }

    @SuppressLint("CheckResult")
    public void scheduler(View view) {
        //Scheduler是RxJava对线程控制器的一个抽象,RxJava内置了多个Scheduler的实现

        //Observable发射完数据之后切换到newThread。后面两次打印都在newThread中进行
       /* Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("hello");
                e.onNext("world");
            }
        })
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                        Log.i(TAG, "accept: " + Thread.currentThread().getName());

                    }
                });
*/
        Observable.just("aaa", "bbb")
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        Log.i(TAG, "apply: " + Thread.currentThread().getName());
                        return s.toUpperCase();
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        Log.i(TAG, "apply: " + Thread.currentThread().getName());
                        return s.toLowerCase();
                    }
                })
                .subscribeOn(Schedulers.single())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept: " + Thread.currentThread().getName());
                        System.out.println(s);
                    }
                });
    }
}
