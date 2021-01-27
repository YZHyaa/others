package com.xupt.yzh;

import net.yzx66.common.Common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


public class Test {

    public static void main(String[] args) {
        Test test = new Test();

        test.testHasWorked();
    }

    public void testUrl() {
        String  mainClassPath= Common.class.getClassLoader().getResource("").getPath().substring(1);
        System.out.println(mainClassPath);
    }

    public void testCap() {
        int length = 40;
        int threads = 9;
        int cap = 1;
        if (length % threads == 0) {
            cap = length / threads;
        } else {
            cap += length / threads;
        }

        System.out.println(cap);
    }

    public void testRandom() {

        int m = 0, n = 240;
        int times = (int) (m + Math.random() * (n - m + 1));
        System.out.println(times);
    }

    public void testExecption() {
        throw new RuntimeException("test...");
    }

    public void testCount() {
        System.out.println(1/60);
    }


    public void testBiaoZhunCha() {
        int[] array = {15,96,85,88,18,58,68,16,6,99,88,11,8,36,82,44,55,66};

        int sum = 0;
        for(int i=0;i<array.length;i++){
            sum += array[i];      // 求出数组的总和
        }
        System.out.println(sum);  //939
        double average = sum/array.length;  // 求出数组的平均数
        System.out.println(average);   //52.0
        double total=0;
        for(int i=0;i<array.length;i++){
            total += (array[i]-average)*(array[i]-average);   //求出方差，如果要计算方差的话这一步就可以了
        }
        double standardDeviation = Math.sqrt(total/array.length);   //求出标准差
        System.out.println(standardDeviation);    //32.55764119219941

    }

    public void testGetUrl() {
        String[] urls = Common.getBlogUrls("https://blog.csdn.net/...");
        System.out.println(Arrays.toString(urls));
    }

    public void testDate() {
        String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.println(curTime);

        String curTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(curTime2);

    }

    public void testDouble() {
        long l = 1549;
        double res = (double)l / 1000;
        System.out.println(res);
        System.out.println(String.format("%.2f", res));

        int i = (int) (60/res);
        System.out.println(i);
    }

    public void testAtomic() {
        AtomicInteger atomicInteger = new AtomicInteger(100);
        System.out.println(atomicInteger.get());
        System.out.println(atomicInteger.decrementAndGet());
        atomicInteger.compareAndSet(0, 1);
        System.out.println(atomicInteger.get());
    }

    public void testHasWorked() {
        int hasWorked = 0;
        for (int i = 0; i < 1000; i++) {
            if (++hasWorked % 50 == 0) {
                System.out.println(hasWorked);
            }
        }
    }
}
