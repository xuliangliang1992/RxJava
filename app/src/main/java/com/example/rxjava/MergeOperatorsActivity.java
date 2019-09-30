package com.example.rxjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author xuliangliang
 * @date 2019-09-29
 * copyright(c) 浩鲸云计算科技股份有限公司
 */
@SuppressLint("CheckResult")
public class MergeOperatorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
    }

    /**
     * 合并多个Observable的发射物
     */
    public void merge(View view) {
        Observable<Integer> observable1 = Observable.just(1, 2, 3);
        Observable<Integer> observable2 = Observable.just(2, 3, 4);

        Observable.merge(observable1, observable2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("onNext " + integer);
                    }
                });
    }

    /**
     * zip操作符返回一个Observable，
     * 它使用这个函数按顺序结合两个或多个Observable发射的数据项，
     * 然后发射这个函数返回的结果。
     * 它按照严格的顺序应用这个函数，
     * 只发射与发射数据项最少的那个Observable一样多的数据
     * <p>
     * combineLatest与zip类似，Observable使用一个函数与原始Observable发射的最近一条数据结合
     */
    public void zip(View view) {
        Observable<Integer> observable1 = Observable.just(3, 2, 3);
        Observable<Integer> observable2 = Observable.just(3, 3, 4);

        Observable.zip(observable1, observable2, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("onNext " + integer);
                    }
                });
    }


    /**
     *
     */
    public void join(View view) {
        Observable<Integer> observable1 = Observable.just(1, 2, 3).delay(200, TimeUnit.MILLISECONDS);
        Observable<Integer> observable2 = Observable.just(4, 5, 6);

        observable1.join(observable2, new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just(String.valueOf(integer)).delay(200, TimeUnit.MILLISECONDS);
            }
        }, new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just(String.valueOf(integer)).delay(200, TimeUnit.MILLISECONDS);
            }
        }, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return integer + " : " + integer2;
            }
        })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("onNext " + s);
                    }
                });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在数据开头插入一条指定数据
     */
    public void startWith(View view) {
        Observable.just("a", "b", "c", "d")
                .startWith("e")
                .startWithArray("f", "g")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("onNext " + s);
                    }
                });
    }

}
