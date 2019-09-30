package com.example.rxjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author xll
 * @date 2018/11/29
 */
@SuppressLint("CheckResult")
public class CreateOperatorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);
    }

    /**
     * 一个形式正确的有限Observable必须尝试调用观察者的onComplete()一次或者它的onError()一次，而且此后不再调用观察者的任何其他方法
     * 使用create方法时，先检查观察者的isDisposed状态，以便在没有观察者的时候，让我们的Observable停止发射数据，防止运行昂贵的运算
     */
    public void useCreate(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    if (!emitter.isDisposed()) {
                        for (int i = 0; i < 10; i++) {
                            emitter.onNext(i);
                        }
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        })
                .subscribe(new Consumer<Integer>() {
                               @Override
                               public void accept(Integer integer) throws Exception {
                                   System.out.println("Next create " + integer);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.err.println("Error: " + throwable.getMessage());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                System.out.println("Sequence complete");
                            }
                        });
    }

    public void useJust(View view) {
        //just将单个数据转换为发射这个单个数据的Observable
        //just类似于from,但是from会将数组或Iterable的数据去除然后逐个发射,而just只是简单地原样发射，将数组或Iterable当作单个数据
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribe(new Consumer<Integer>() {
                               @Override
                               public void accept(Integer integer) throws Exception {
                                   System.out.println("Next just " + integer);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.err.println("Error: " + throwable.getMessage());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                System.out.println("Sequence complete");
                            }
                        });
    }

    public void useFrom(View view) {
        Observable.fromArray("hello", "from")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                    }
                });

        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(i);
        }

        Observable.fromIterable(items)
                .subscribe(new Consumer<Integer>() {
                               @Override
                               public void accept(Integer integer) throws Exception {
                                   System.out.println("Next fromIterable " + integer);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.err.println("Error: " + throwable.getMessage());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                System.out.println("Sequence complete");
                            }
                        });

        ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "xll");
            }
        });
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("模拟一些耗时的操作...");
                Thread.sleep(5000);
                return "OK";
            }
        });
        //from方法有一个可接受两个可选参数的版本，指定时长和时间单位。
        //如果超过指定时长，Future没有返回一个值，那么这个Observable会发射一个错误通知并终止
        Observable.fromFuture(future, 4, TimeUnit.SECONDS)
                .subscribe(new Consumer<String>() {
                               @Override
                               public void accept(String s) throws Exception {
                                   System.out.println("Next fromFuture " + s);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.err.println("onError " + throwable.toString());
                            }
                        });
    }

    public void useRepeat(View view) {
        //repeat会重复地发射数据。
        Observable.just("hello repeat")
                .repeat(3)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("repeat " + s);
                    }
                });

        //repeatWhen不是缓存和重放原始Observable的数据序列，而是有条件地重新订阅和发射原来的Observable
        Observable.range(0, 9)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                        return Observable.timer(10, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("repeatWhen " + integer);
                    }
                });
        //先发射一遍0-9，10s后再发射一遍

        //repeatUntil是RxJava2.x新增的操作符，表示直到某个条件就不再重复发射数据。
        //当BooleanSupplier的getAsBoolean()返回false时，表示重复发射上游的Observable
        //当BooleanSupplier的getAsBoolean()返回true时，表示中止发射上游的Observable
        final long statTimeMillis = System.currentTimeMillis();
        //interval 按照固定的时间间隔发射一个无限递增的整数序列  第一个参数 时间间隔,第二个参数 时间单位
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .take(5)
                .repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {
                        return System.currentTimeMillis() - statTimeMillis > 5000;
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println("repeatUntil " + aLong);
                    }
                });
        //打印了两遍0-4，满足条件 System.currentTimeMillis() - statTimeMillis > 5000 后不再执行
    }

    public void useDefer(View view) {
        Observable<String> observable = Observable.defer(new Callable<ObservableSource<String>>() {

            @Override
            public ObservableSource<String> call() throws Exception {
                return Observable.just("Hello defer");
            }
        });

        observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String str) throws Exception {
                System.out.println(str);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void useTimer(View view) {
        Log.i("MainActivity", "use timer: ");
        //timer操作符创建一个在给定的时间段之后返回一个特殊值的Observable
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println("Hello timer");
                    }
                });
    }
}
