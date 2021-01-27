package net.yzx66.job.some_blog;

public class SomeBlogMain {

    public static void main(String[] args) {
        SomeBlogHandler handler = new SomeBlogHandler();

        /**
         * TODO 刷一篇文章几秒（可选）
         * 三种方式获得：
         * 1. 采用默认值，2.5 {@link SomeBlogHandler.DEFAULT_COST}（默认方式）
         * 2. 测试得到，调用 doTest() {@link SomeBlogHandler#doTest(String)}
         * 3. 手动设值，调用 setter {@link SomeBlogHandler#setPer_blog_cost_sec(double)}
         * 一般为 1.5-3.5 左右（宁小不大）
         *          * 1. 如果小于实际值，可能导致产生 sleepTime ==> 运行时间超过预期（可接受）
         *          * 2. 如果大于实际值，可能导致实际间隔时间小于冷却时间（60秒） ==> 实际浏览量少于预期
         * 注：建议首次使用时务必测试，其余情况可以直接赋值或者默认值
         */
//        handler.doTest("https://blog.csdn.net/...");
//        handler.setPer_blog_cost_sec(3.2);

        /**
         * TODO 手动启动时减去已刷次数（可选）
         */
        handler.setDecrement(100);

        /**
         * TODO 文章链接和次数
         * 注：建议按次数从到小排列，刷完一篇删一篇，重启后再设置 decrement 减去已刷的
         */
        handler.createJob("https://blog.csdn.net/.../article/details/...", 1000);






        handler.start();
    }

}
