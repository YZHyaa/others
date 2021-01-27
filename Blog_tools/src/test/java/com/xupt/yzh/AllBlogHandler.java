//package com.xupt.yzh;
//
//import net.yzx66.common.Common;
//import org.openqa.selenium.chrome.ChromeDriver;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
//
//public class AllBlogHandler {
//
//    /**
//     * 刷一篇文章几秒(需测试），一般为 1.5-3.0 左右（宁小不大）
//     * 1. 如果小于实际值，可能导致 1.线程负载变大产生排队时间  2.产生 sleepTime ==> 运行时间超过预期（可接受）
//     * 2. 如果大于实际值，可能导致 1.使用线程变多 2.实际间隔时间小于冷却时间（没有了sleepTime） ==> 实际浏览量少于预期
//     * {@link Common#testQueryTime(String)}
//     */
//    public static double PER_BLOG_COST_SEC =  Common.testQueryTime("https://blog.csdn.net/..."); //
//
//    /**
//     * 线程最优负载
//     * 注：用60去除是因为一篇文章的冷却期是60秒
//     */
//    public static int THR_BEST_LOAD = (int) (60 / PER_BLOG_COST_SEC);
//
//    /** 总访问量 */
//    public static AtomicInteger total = new AtomicInteger();
//    /** 完成时间（大致）*/
//    public static AtomicInteger finishTime = new AtomicInteger();
//    /** 剩余任务 */
//    public static AtomicInteger remainTask = new AtomicInteger();
//
//    /** 每个线程已经刷了多少 */
//    private static int[] hasWorked = new int[100];
//
//
//
//    /**
//     *  并行刷
//     *
//     * @param threadIdx 线程序号（从0开始）
//     * @param threads 线程数量
//     * @param urlArray 要刷的博客数组
//     * @param min 每篇最少刷多少次
//     * @param max 每篇最多刷多少次
//     * @param tasks 当前线程的任务，即还有多少没刷
//     *              * 目的是避免异常重启后重复刷）
//     *              * main 从 null 开始
//     */
//    public static void doQuery(int threadIdx, int threads, String[] urlArray, int min, int max, int[] tasks) {
//
//        int length = urlArray.length;
//        // CAS 只初始化一次
//        remainTask.compareAndSet(0, length);
//
//        // 计算区间容量，判断线程数是否最优
//        int cap = length / threads;
//        // 小于线程最优负载，则使区间成为最优负载，并关闭多余线程
//        // 注：多余的不处理，比如 cap = 100 就不用关闭线程
//        int needThreads = threads;
//        if (cap <= THR_BEST_LOAD) {
//            cap = THR_BEST_LOAD;
//
//            needThreads = length % THR_BEST_LOAD == 0 ? length / THR_BEST_LOAD : length / THR_BEST_LOAD + 1;
//            if (threadIdx >= needThreads) {
//                System.out.println("【INFO】Thread-" + threadIdx + "：" + needThreads + "个线程足矣，我这个线程就先关了...");
//                return;
//            }
//        }
//
//        // 当前线程负责的区间 [startIdx，endIdx]
//        int startIdx = threadIdx * cap;
//        int endIdx = startIdx + cap;
//        // 特殊情况1：对于 cap < THR_BEST_LOAD，比如 (331文章，7线程）这种补到60后，最后一个线程处理不够60的情况，要更新cap
//        if (startIdx + cap > length) {
//            endIdx = length;
//            cap = endIdx - startIdx; // 变小
//        }
//        // 特殊情况2：对于 cap > THR_BEST_LOAD，比如（331文章，6线程）这种最后少处理余数的情况，补给最后一个线程
//        if (cap > THR_BEST_LOAD && length % needThreads != 0) {
//            if (threadIdx == threads - 1) {
//                cap +=  (length - endIdx); // 变大
//                endIdx = length;
//            }
//        }
//
//        // 区间内每刷每篇文章的次数
//        if (tasks == null) {
//            tasks = generateTask(max, min, cap);
//
//            int threadSumTask = 0; // 统计总浏览量
//            int threadMaxTask = 0; // 统计完成时间
//            for (int task : tasks) {
//                threadSumTask += task;
//                threadMaxTask = Math.max(task, threadMaxTask);
//            }
//            total.getAndAdd(threadSumTask);
//
//            int cycleTime = cap * PER_BLOG_COST_SEC <= 60 ? 60 : (int) (cap * PER_BLOG_COST_SEC); // 秒
//            int threadFinishTime = cycleTime * threadMaxTask / 60; // 分钟（不准确，因为周期可能会变小，所以得到的 finishTime是最大情况）
//            if (finishTime.get() < threadFinishTime) {
//                finishTime.set(threadFinishTime);
//            }
//        }
//        System.out.println("【INFO】Thread-" + threadIdx + " " + tasks.length + "篇 " +Arrays.toString(tasks));
//
//        // 最大访问量
//        int maxTask = 0;
//        for (int i = 0; i < cap; i++) {
//            maxTask = Math.max(tasks[i], maxTask);
//        }
//
//        try {
//            // 刷！
//            ChromeDriver chromeDriver = Common.openChrome();
//
//            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//
//            int nextTaskCount;
//            for (int i = 0; i < maxTask; i++) {
//                nextTaskCount = 0;
//                // [startIdx, endIdx)
//                for (int j = startIdx; j < endIdx; j++) {
//                    int taskIdx = j - startIdx;
//                    if (tasks[taskIdx] > 0) {
//                        nextTaskCount++;
//                        chromeDriver.get(urlArray[j]);
//                        if (--tasks[taskIdx] > 0 ) nextTaskCount++;
//                    } else if (tasks[taskIdx] == 0) {
//                        int remainTask = AllBlogHandler.remainTask.decrementAndGet();
//                        System.out.println("【INFO】" + formatter.format(new Date()) + " " + urlArray[j] + "已刷完，剩余" + remainTask + "篇");
//
//                        tasks[taskIdx] = -1;
//                    }
//                }
//                if (++hasWorked[threadIdx] % 50 == 0) {
//                    System.out.println("【INFO】" + formatter.format(new Date()) + " Thread-" + threadIdx + "每篇已刷" + hasWorked[threadIdx] + "次 " + Arrays.toString(tasks));
//                }
//
//                if (nextTaskCount * PER_BLOG_COST_SEC < 60) {
//                    int sleepTime = (int) (60 - nextTaskCount * PER_BLOG_COST_SEC);
//                    TimeUnit.SECONDS.sleep(sleepTime);
//                }
//            }
//
//            chromeDriver.close();
//
//        }catch (Exception e) {
//            int[] finalTasks = tasks;
//            new Thread(() -> doQuery(threadIdx, threads, urlArray, min, max, finalTasks)).start();
//
//            String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
//            System.out.println("【ERROR】" + curTime + " Thread-" + threadIdx +" 出现异常，已启动新页面恢复运行...");
//        }
//    }
//
//    /**
//     * 生成100个随机序列
//     */
//    public static int[] generateTask(int max, int min, int cap) {
//
//        ArrayList<int[]> allTasks = new ArrayList<>();
//
//        // 生成100个随机序列
//        for (int i = 0; i < 100; i++) {
//            int[] tasks = new int[cap];
//            for (int j = 0; j < cap; j++) {
//                tasks[j] = (int) (min + Math.random() * (max - min + 1));
//                // 允许 min 为负数，所以这里将生成的负数置为 0
//                if(tasks[j] < 0) {
//                    tasks[j] = 0;
//                }
//            }
//            allTasks.add(tasks);
//        }
//
//        return findBestTask(allTasks);
//    }
//
//    /**
//     *  寻找方差最大的序列作为最优序列
//     */
//    public static int[] findBestTask(List<int[]> list) {
//
//        int maxVariance = 0; int maxVarianceIdx = 0;
//        for (int i = 0; i < list.size(); i++) {
//            int[] array = list.get(i);
//
//            int sum = 0;
//            for (int a : array) {
//                sum += a;
//            }
//            double average = sum / array.length;
//
//            double variance = 0;
//            // 方差
//            for (int a : array) {
//                variance += (a - average) * (a - average);
//            }
//
//            if (variance > maxVariance) {
//                maxVarianceIdx = i;
//            }
//        }
//
//        return list.get(maxVarianceIdx);
//    }
//}
