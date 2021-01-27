package net.yzx66.job.some_blog;

import net.yzx66.common.Common;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SomeBlogHandler {

    /**
     * 刷一篇文章几秒（重要）
     * 三种赋值方式：1.测试  2.默认值 3.手动set
     */
    private double per_blog_cost_sec = DEFAULT_COST;
    private static final double DEFAULT_COST = 2.5;

    /** 任务队列 */
    private ArrayList<JobNode> jobNodes = new ArrayList<>();
    /** 剩余任务 */
    private int remainTask = -1;
    /** 每篇已经刷了多少浏览 */
    private int hasWorked = 0;

    /** 用户手动重启后，可减去已刷的次数*/
    private int decrement;

    /**
     * 单线程刷
     */
    public void start(){

        // CAS 只初始化一次
        remainTask = remainTask == -1 ? jobNodes.size() : remainTask;

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        System.out.println("【INFO】" + formatter.format(new Date()) + " 共 " + jobNodes.size() + " 篇博客要刷");
        int sum = 0;
        int max = 0;
        for (JobNode jobNode : jobNodes) {
            sum += jobNode.getTimes();
            max = Math.max(jobNode.getTimes(), max);
        }
        System.out.println("【INFO】" + formatter.format(new Date()) + " 预计可刷浏览量：" + sum);

        int circle = jobNodes.size() * per_blog_cost_sec <= 60 ? 60 : (int) (jobNodes.size() * per_blog_cost_sec);
        System.out.println("【INFO】" + formatter.format(new Date()) + " 预计完成时间（分钟）：" + max * circle / 60); // 理想值（不准）

        // 刷！
        try {
            ChromeDriver chromeDriver = Common.openChrome();
            while (!jobNodes.isEmpty()) {
                for (JobNode j : jobNodes) {
                    chromeDriver.get(j.getUrl());
                    if (!j.updateTimes()) {
                        jobNodes.remove(j);
                        remainTask--;
                        System.out.println("【INFO】" + formatter.format(new Date()) + " " + j.getUrl() + "已刷完，剩余" + remainTask + "篇");
                    }
                }
                if (++hasWorked % 50 == 0) {
                    System.out.println("【INFO】" + formatter.format(new Date()) + " 每篇博客已经刷了" + hasWorked + "次");
                }

                int sleepTime = jobNodes.size() * per_blog_cost_sec <= 60 ? 60 : 0;
                TimeUnit.SECONDS.sleep(sleepTime);
            }

            chromeDriver.close();

        } catch (Exception e) {
            new Thread(this::start).start();
            System.out.println("【ERROR】" + formatter.format(new Date()) + " 出现异常，已启动新页面恢复运行...");
        }
    }

    /**
     * 测试刷一篇文章要多久
     * @param testBlogUrl 测试博客链接
     * {@link Common#testQueryTime(String[])}
     */
    public void doTest(String testBlogUrl) {
        if (StringUtils.isNotBlank(testBlogUrl)) {
            per_blog_cost_sec = Common.testQueryTime(Common.getBlogUrls(testBlogUrl, 1, true));
        }
    }

    /**
     * 创建节点
     */
    public void createJob(String url, int times) {
        JobNode jobNode = new JobNode(url, times);
        jobNodes.add(jobNode);
    }

    /**
     * 队列节点
     */
    private class JobNode  {

        private int times;
        private String url;

        public JobNode(String url, int times) {
            this.times = times - decrement;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public boolean updateTimes() {
            return --times != 0;
        }

        int getTimes() {
            return times;
        }
    }

    public void setPer_blog_cost_sec(double per_blog_cost_sec) {this.per_blog_cost_sec = per_blog_cost_sec;}
    public void setDecrement(int decrement) {
        this.decrement = decrement;
    }
}
