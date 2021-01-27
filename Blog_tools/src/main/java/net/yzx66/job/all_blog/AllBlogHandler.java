package net.yzx66.job.all_blog;

import net.yzx66.common.Common;
import org.openqa.selenium.chrome.ChromeDriver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AllBlogHandler {

    /**
     * 刷一篇文章几秒
     * 测试{@link Common#testQueryTime(String[])}
     */
    private double per_blog_cost_sec = DEFAULT_COST;
    private static final double DEFAULT_COST = 2.5;
    /**
     * 线程最优负载
     * 注：用60去除是因为一篇文章的冷却期是60秒
     */
    private int thr_best_load;

    /** 线程数量 */
    private int threads;
    /** 路径：1.博客路径 2.分类专栏路径 */
    private String path;
    /** 模式：1.刷博客 2.刷博客指定页 3.刷分类专栏 */
    private int pattern;
    /** 指定页，配合上面模式2 */
    private int pageIdx;
    /** 刷的浏览数最小值 */
    private int min;
    /** 刷的浏览数最大值 */
    private int max;
    /** 是否进行启动测试（测刷一篇博客要多久）*/
    private boolean doTest = false;

    /** 总访问量 */
    private AtomicInteger total = new AtomicInteger();
    /** 完成时间（大致）*/
    private AtomicInteger finishTime = new AtomicInteger();
    /** 剩余任务（即剩余要刷的文章数） */
    private AtomicInteger remainTask = new AtomicInteger();
    /** 每个线程的每篇文章已经刷了多少浏览 */
    private int[] hasWorked = new int[100];

    /** 辅助打印日志 */
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    /**
     * 入口
     */
    public void start() throws Exception {

        String[] urlArray;

        if (pattern == 1) {
            urlArray = Common.getBlogUrls(path);
        } else if (pattern == 2) {
            urlArray = Common.getBlogUrls(path, pageIdx);
        } else{
            urlArray = Common.getCategoryUrls(path);
        }

        if (doTest) {
            per_blog_cost_sec = Common.testQueryTime(urlArray);
        }
        thr_best_load = (int) (60 / per_blog_cost_sec);


        // 启动线程开刷
        for(int thrIdx = 0; thrIdx < threads; thrIdx++){
            int finalThrIdx = thrIdx;
            new Thread(() -> this.doQuery(finalThrIdx, threads, urlArray, min, max, null)).start();
        }

        TimeUnit.SECONDS.sleep(1);

        System.out.println("【INFO】" + formatter.format(new Date()) + " 预计可刷浏览量：" + total.get());
        System.out.printf("【INFO】%s 预计完成时间（分钟）：%d \n", formatter.format(new Date()), finishTime.get());
    }


    /**
     *  并行刷
     *
     * @param threadIdx 线程序号（从0开始）
     * @param threads 线程数量
     * @param urlArray 要刷的博客数组
     * @param min 每篇最少刷多少次
     * @param max 每篇最多刷多少次
     * @param tasks 当前线程的任务，即还有多少没刷
     *              * 目的是避免异常重启后重复刷）
     *              * main 从 null 开始
     */
    public void doQuery(int threadIdx, int threads, String[] urlArray, int min, int max, int[] tasks) {

        int length = urlArray.length;
        // CAS 只初始化一次
        remainTask.compareAndSet(0, length);

        // 计算区间容量，判断线程数是否最优
        int cap = length / threads;
        // 小于线程最优负载，则使区间成为最优负载，并关闭多余线程
        // 注：多余的不处理，比如 cap = 100 就不用关闭线程
        int needThreads = threads;
        if (cap <= thr_best_load) {
            cap = thr_best_load;

            needThreads = length % thr_best_load == 0 ? length / thr_best_load : length / thr_best_load + 1;
            if (threadIdx >= needThreads) {
                System.out.println("【INFO】Thread-" + threadIdx + "：" + needThreads + "个线程足矣，我这个线程就先关了...");
                return;
            }
        }

        // 当前线程负责的区间 [startIdx，endIdx]
        int startIdx = threadIdx * cap;
        int endIdx = startIdx + cap;
        // 特殊情况1：对于 cap < thr_best_load，比如 (331文章，7线程）这种补到60后，最后一个线程处理不够60的情况，要更新cap
        if (startIdx + cap > length) {
            endIdx = length;
            cap = endIdx - startIdx; // 变小
        }
        // 特殊情况2：对于 cap > thr_best_load，比如（331文章，6线程）这种最后少处理余数的情况，补给最后一个线程
        if (cap > thr_best_load && length % needThreads != 0) {
            if (threadIdx == threads - 1) {
                cap +=  (length - endIdx); // 变大
                endIdx = length;
            }
        }

        // 区间内每刷每篇文章的次数（只有第一次进来才能设置）
        if (tasks == null) {
            tasks = generateTask(max, min, cap);

            int threadSumTask = 0; // 统计总浏览量
            int threadMaxTask = 0; // 统计完成时间
            for (int task : tasks) {
                threadSumTask += task;
                threadMaxTask = Math.max(task, threadMaxTask);
            }
            total.getAndAdd(threadSumTask);

            // 周期
            int cycleTime = cap * per_blog_cost_sec <= 60 ? 60 : (int) (cap * per_blog_cost_sec); // 秒
            // 理论完成时间（不准确）
            // 因为周期可能会随着任务变少而变小，也可能因为网络阻塞而变长，所以得到的 finishTime 只是个理想值
            int threadFinishTime = cycleTime * threadMaxTask / 60; // 分钟
            if (finishTime.get() < threadFinishTime) {
                finishTime.set(threadFinishTime);
            }
        }
        System.out.println("【DEBUG】Thread-" + threadIdx + " " + tasks.length + "篇 " +Arrays.toString(tasks));

        // 最大访问量
        int maxTask = 0;
        for (int i = 0; i < cap; i++) {
            maxTask = Math.max(tasks[i], maxTask);
        }

        try {
            // 刷！
            ChromeDriver chromeDriver = Common.openChrome();

            int nextTaskCount;
            for (int i = 0; i < maxTask; i++) {
                nextTaskCount = 0;
                // [startIdx, endIdx)
                for (int j = startIdx; j < endIdx; j++) {
                    int taskIdx = j - startIdx;
                    if (tasks[taskIdx] > 0) {
                        chromeDriver.get(urlArray[j]);
                        if (--tasks[taskIdx] > 0 ) nextTaskCount++;
                    } else if (tasks[taskIdx] == 0) {
                        int rt = remainTask.decrementAndGet();
                        System.out.println("【INFO】" + formatter.format(new Date()) + " Thread-"+ threadIdx + " " + urlArray[j] + "已刷完，剩余" + rt + "篇");

                        tasks[taskIdx] = -1;
                    }
                }
                if (++hasWorked[threadIdx] % 50 == 0) {

                    int tmp = 0;
                    for (int task : tasks) {
                        if (task != -1) tmp++;
                    }
                    System.out.println("【INFO】" + formatter.format(new Date()) + " Thread-" + threadIdx + " 每篇博客已刷" + hasWorked[threadIdx] + "次，当前线程剩余" + tmp + "篇");
                    System.out.println("【DEBUG】Thread-" + threadIdx + " " + tasks.length + "篇 " + Arrays.toString(tasks));
                }

                if (nextTaskCount * per_blog_cost_sec < 60) {
                    int sleepTime = (int) (60 - nextTaskCount * per_blog_cost_sec);
                    TimeUnit.SECONDS.sleep(sleepTime);
                }
            }

            chromeDriver.close();

        }catch (Exception e) {
            int[] finalTasks = tasks;
            new Thread(() -> this.doQuery(threadIdx, threads, urlArray, min, max, finalTasks)).start();

            System.out.println("【ERROR】" + formatter.format(new Date()) + " Thread-" + threadIdx +" 出现异常，已启动新页面恢复运行...");
        }
    }

    /**
     * 生成100个随机序列
     */
    public int[] generateTask(int max, int min, int cap) {

        ArrayList<int[]> allTasks = new ArrayList<>();

        // 生成100个随机序列
        for (int i = 0; i < 100; i++) {
            int[] tasks = new int[cap];
            for (int j = 0; j < cap; j++) {
                tasks[j] = (int) (min + Math.random() * (max - min + 1));
                // 允许 min 为负数，所以这里将生成的负数置为 0
                if(tasks[j] < 0) {
                    tasks[j] = 0;
                }
            }
            allTasks.add(tasks);
        }

        return findBestTask(allTasks);
    }

    /**
     *  寻找方差最大的序列作为最优序列
     */
    public int[] findBestTask(List<int[]> list) {

        int maxVariance = 0; int maxVarianceIdx = 0;
        for (int i = 0; i < list.size(); i++) {
            int[] array = list.get(i);

            int sum = 0;
            for (int a : array) {
                sum += a;
            }
            double average = sum / array.length;

            double variance = 0;
            // 方差
            for (int a : array) {
                variance += (a - average) * (a - average);
            }

            if (variance > maxVariance) {
                maxVarianceIdx = i;
            }
        }

        return list.get(maxVarianceIdx);
    }

    public void doTest() {
        this.doTest = true;
    }

    public void setPer_blog_cost_sec(double per_blog_cost_sec) {
        this.per_blog_cost_sec = per_blog_cost_sec;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public void setPageIdx(int pageIdx) {
        this.pageIdx = pageIdx;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
