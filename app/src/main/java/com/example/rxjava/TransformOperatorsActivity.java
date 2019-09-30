package com.example.rxjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 变换操作符
 *
 * @author xuliangliang
 * @date 2019-09-23
 * copyright(c) 浩鲸云计算科技股份有限公司
 */
@SuppressLint("CheckResult")
public class TransformOperatorsActivity extends AppCompatActivity {
    private List<Student> mStudentList;
    private String TAG = "TransformOperatorsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_operators);
        String students = "[{\n" +
                "\t\"name\": \"a\",\n" +
                "\t\"courses\": [{\n" +
                "\t\t\"courseName\": \"语文\"\n" +
                "\t}, {\n" +
                "\t\t\"courseName\": \"数学\"\n" +
                "\t}, {\n" +
                "\t\t\"courseName\": \"英语\"\n" +
                "\t}]\n" +
                "},{\n" +
                "\t\"name\": \"b\",\n" +
                "\t\"courses\": [{\n" +
                "\t\t\"courseName\": \"语文\"\n" +
                "\t}, {\n" +
                "\t\t\"courseName\": \"数学\"\n" +
                "\t}]\n" +
                "},{\n" +
                "\t\"name\": \"c\",\n" +
                "\t\"courses\": [{\n" +
                "\t\t\"courseName\": \"语文\"\n" +
                "\t}, {\n" +
                "\t\t\"courseName\": \"英语\"\n" +
                "\t}]\n" +
                "},{\n" +
                "\t\"name\": \"d\",\n" +
                "\t\"courses\": [{\n" +
                "\t\t\"courseName\": \"数学\"\n" +
                "\t}, {\n" +
                "\t\t\"courseName\": \"英语\"\n" +
                "\t}]\n" +
                "}]";
        Gson gson = new Gson();
        mStudentList = gson.fromJson(students, new TypeToken<List<Student>>() {
        }.getType());
    }

    /**
     * 对Observable发射的每一项数据应用一个函数，执行变换操作。
     * map操作符对原始Observable发射的每一项数据应用一个你选择的函数，
     * 然后返回一个发射这些结果的Observable
     */
    public void map(View view) {
        Observable.fromIterable(mStudentList)
                .map(student -> {
                    System.out.println(student.getName());
                    return student.getCourses();
                })
                .subscribe(courses -> {
                    for (int i = 0; i < courses.size(); i++) {
                        System.out.println(courses.get(i).getCourseName());
                    }
                });
    }

    /**
     * flatMap操作符使用一个指定的函数对原始Observable发射的每一项数据执行变换操作，
     * 这个函数返回一个本身也发射数据的Observable，
     * 然后flatMap合并这些Observables发射的数据，
     * 最后将合并后的结果当作它自己的数据序列发射。
     * Android中常用于连续调用接口，A接口的返回值作为B接口的参数
     * 如先获取token在查询数据
     * <p>
     * concatMap是有序的，flatMap是无序的，
     * concatMap最终输出的顺序与原序列保持一致，
     * 而flatMap则不一定，有可能出现交错。
     * <p>
     * flatMapIterable与flatMap是类似的，
     * 不同的是flatMapIterable把每一个元素转换成Iterable
     * <p>
     * flatMap是RxJava中一个强大的操作符，在实际项目中，应用的场景很多，
     * 比如开始列举的化解循环嵌套，
     * 还有一种场景在我们实际项目中是非常多的，那就是连续请求两个接口，第一个接口的返回值是第二个接口的请求参数，
     * 在这种情况下，以前我们会在一个请求完成后，在onResponse中获取结果再请求另一个接口。
     * 这种接口嵌套，代码看起来是非常丑陋的，运用flatMap就能很好的解决这个问题。
     * 代码看起来非常优雅而且逻辑清晰。
     * 如果需要保证顺序的话，请使用concatMap。
     */
    public void flatMap(View view) {

        Observable.fromIterable(mStudentList)
                .flatMap((Function<Student, Observable<Student.Course>>) student -> {
                    Log.i(TAG, student.getName());
                    int delay = 0;
                    if ("a".equals(student.getName())) {
                        delay = 500;
                    }
                    return Observable.fromIterable(student.getCourses()).delay(delay, TimeUnit.MILLISECONDS);
                })
                .subscribe(course -> Log.i(TAG, course.getCourseName()));

       /* Observable.fromIterable(mStudentList)
                .flatMapIterable(new Function<Student, Iterable<Student.Course>>() {
                    @Override
                    public Iterable<Student.Course> apply(Student student) throws Exception {
                        return student.getCourses();
                    }
                })
                .subscribe(new Consumer<Student.Course>() {
                    @Override
                    public void accept(Student.Course course) throws Exception {
                        Log.i(TAG, "accept: "+course.getCourseName());
                    }
                });*/
    }

    /**
     * groupBy操作符将一个Observable拆分成一些Observables集合，
     * 它们中的每一个都发射原始Observable的一个子序列。
     * 哪个数据项由哪个Observable发射是由一个函数判定的，
     * 这个函数给每一项指定一个key，
     * key相同的数据会被同一个Observable发射
     */
    public void groupBy(View view) {
        Observable.range(0, 20)
                .groupBy(integer -> integer % 2 == 0 ? "偶数组" : "奇数组")
                .subscribe(
                        stringIntegerGroupedObservable -> {
                            if (stringIntegerGroupedObservable.getKey().equalsIgnoreCase("偶数组")) {
                                stringIntegerGroupedObservable
                                        .subscribe(integer -> Log.i(TAG, stringIntegerGroupedObservable.getKey() + " " + integer));
                            }
                        });
    }

    /**
     * buffer会定期收藏Observable数据并收藏进一个数据包裹，
     * 然后发射这些数据包裹，而不是一次发射一个值
     * count 缓存个数
     * skip 缓存间隔
     * <p>
     * long timespan 间隔时间
     * TimeUnit  unit 间隔单位
     * int count 缓存个数
     */
    public void buffer(View view) {
       /* Observable.range(1, 20)
                .buffer(3, 2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        System.out.println(integers);。
}
                });*/

        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(20)
                .buffer(250, TimeUnit.MILLISECONDS, 3)
                .subscribe(System.out::println);
    }

    /**
     * 定期将来自原始Observable的数据分解为一个Observable窗口，发射这些窗口，而不是每次发射一项数据
     */
    public void window(View view) {
        Observable.range(1, 10)
                .window(2)
                .subscribe(new Consumer<Observable<Integer>>() {
                    @Override
                    public void accept(Observable<Integer> integerObservable) throws Exception {
                        System.out.println("onNext");
                        integerObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                System.out.println("accept " + integer);
                            }
                        });
                    }
                });
    }

    /**
     * first 只发射第一项（或满足某个条件的第一项）数据
     * 若未发射数据，则取默认值 defaultItem
     * firstElement 无默认值
     * firstOrError 要么能取到默认值，要么执行onError
     * <p>
     * last 同first用法 取最后一项
     */
    public void first(View view) {
        //        Observable.range(2, 5)
        Observable.<Integer>empty()
                //                .first(1)
                //                .firstElement()
                .firstOrError()
                .subscribe(new Consumer<Integer>() {
                               @Override
                               public void accept(Integer integer) throws Exception {
                                   System.out.println("accept " + integer);
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   System.out.println("onError ");
                               }
                           }
                );
    }

    /**
     * take(n) 只发射前面的n项数据
     * 如果Observable发射的数据少于n项，
     * 那么take生成的Observable就不会抛出异常或者发射onError通知，
     * 而是仍然会发射这些数据
     * <p>
     * take(time,unit) 发射(time unit)时间内发射的数据
     * <p>
     * takeLast 只发射后面的n项数据
     */
    public void take(View view) {
        //        Observable.range(1, 10)
        //                .take(5)
        Observable.intervalRange(0, 10, 1, 1, TimeUnit.SECONDS)
                .takeLast(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long integer) {
                        System.out.println("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError ");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete ");
                    }
                });
    }

    /**
     * 跳过数据
     * 用法与take类似
     */
    public void skip(View view) {
        Observable.intervalRange(0, 10, 1, 1, TimeUnit.SECONDS)
                .skip(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long integer) {
                        System.out.println("onNext " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError ");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete ");
                    }
                });
    }

    /**
     * 只发射第n项数据 索引从0开始算起
     * 若n<0则抛出IndexOutOfBoundsException
     * <p>
     * elementAt(index)
     * elementAt(index,defaultItem)
     * elementAtOnError
     * <p>
     * ignoreElements() 不发射任何数据，只允许onComplete或onError通过
     */
    public void elementAt(View view) {
        Observable.range(0, 10)
                //        Observable.<Integer>empty()
                .ignoreElements()
                //                .elementAt(2)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("onComplete");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * distinct过滤掉重复的数据项,只允许还没有发射过的数据项通过
     * <p>
     * distinctUntilChanged 一项数据与前一项相同则过滤
     * <p>
     * filter 满足条件的数据通过
     */
    public void distinct(View view) {
        Observable.just(1, 2, 3, 3, 5, 1, 2, 3, 4, 5)
                .distinct(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer / 2;
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
     * debounce过滤掉发射速率过快的数据
     */
    public void debounce(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1); // skip
                Thread.sleep(400);
                emitter.onNext(2); // deliver
                Thread.sleep(505);
                emitter.onNext(3); // skip
                Thread.sleep(100);
                emitter.onNext(4); // deliver
                Thread.sleep(605);
                emitter.onNext(5); // deliver
                Thread.sleep(510);
                emitter.onComplete();
            }
        })
                .throttleWithTimeout(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer aLong) throws Exception {
                        System.out.println("onNext " + aLong);
                    }
                });

    }
}
