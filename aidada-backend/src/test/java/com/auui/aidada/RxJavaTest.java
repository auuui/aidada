package com.auui.aidada;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RxJavaTest {

    @Test
    public void test() throws InterruptedException {
        //创建数据流
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(i -> i + 1)
                .subscribeOn(Schedulers.io());//指定使用的线程池
//创建观察者，订阅flowable流
        flowable
                .observeOn(Schedulers.io())
                .doOnNext(item-> System.out.println(item.toString()))
                .subscribe();
        //主线程睡眠，以便观察结果
        Thread.sleep(10000L);
    }
}
