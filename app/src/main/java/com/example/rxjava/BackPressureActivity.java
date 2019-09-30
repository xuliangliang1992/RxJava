package com.example.rxjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 在RxJava中，会遇到被观察者发射消息太快以至于它的操作符或订阅者不能及时处理相关消息，这就是背压场景(Back Pressure)
 * 背压必须是在异步的场景下才会出现，即被观察者和观察者处于不同的线程中
 * 在RxJava 2.x中，只有新增的Flowable类型是支持背压的，并且Flowable很多操作符内部都使用了背压策略，从而避免过多的数据填满内部的队列
 *
 * @author xuliangliang
 * @date 2019-09-30
 * copyright(c) 浩鲸云计算科技股份有限公司
 */
@SuppressWarnings("CheckResult")
public class BackPressureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_pressure);
    }

    /**
     * MISSING  通过Create方法创建的Flowable没有指定背压策略，不会对通过onNext发射的数据做缓存处理或丢弃处理
     * 需要下游通过背压操作符(
     * onBackpressureBuffer()  对应 BUFFER
     * onBackpressureDrop()    对应 DROP
     * onBackpressureLatest()  对应 LATEST
     * )指定背压策略
     */
    @SuppressLint("CheckResult")
    public void missing(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.MISSING)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept " + integer);
                    }
                });
    }

    /**
     * ERROR 如果放入Flowable的异步缓存池中的数据超限了，则会抛出MissingBackpressureException异常
     */
    public void error(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept " + integer);
                    }
                });
    }

    /**
     * BUFFER Flowable的异步缓存池同Observable的一样，没有固定大小，可以无限制添加数据，不会抛出MissingBackpressureException异常，但会导致OOM
     */
    public void buffer(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept " + integer);
                    }
                });
    }

    /**
     * DROP 如果Flowable的异步缓存池满了，则会丢掉将要放入缓存池中的数据
     */
    public void drop(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept " + integer);
                    }
                });
    }

    /**
     * LATEST 与DROP类似，但最后一项会强行加入缓存池
     */
    public void latest(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 130; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("accept " + integer);
                    }
                });
    }
}
