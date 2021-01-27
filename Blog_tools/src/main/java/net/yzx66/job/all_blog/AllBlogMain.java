package net.yzx66.job.all_blog;

public class AllBlogMain {

    public static void main(String[] args) throws Exception {
        AllBlogHandler handler = new AllBlogHandler();

        /**
         * TODO 链接，三种模式：
         * patter = 1，博客链接
         * patter = 2，指定博客页链接，另外还要 setPageIdx {@link AllBlogHandler#setPageIdx(int)}
         * patter = 3, 分类专栏链接
         */
        handler.setPattern(1);
        handler.setPath("https://blog.csdn.net/...");
//        handler.setPath("https://blog.csdn.net/.../category_....html");

        /**
         * TODO 刷一篇文章几秒（可选）
         * 三种方式获得：
         * 1. 采用默认值，2.5 {@link AllBlogHandler.DEFAULT_COST}（默认方式）
         * 2. 测试得到，调用 doTest() {@link AllBlogHandler#doTest()}
         * 3. 手动设值，调用 setter {@link AllBlogHandler#setPer_blog_cost_sec(double)}
         * 一般为 1.5-3.5 左右（宁小不大）
         *          * 1. 如果小于实际值，可能导致 1.线程负载变大产生排队时间  2.产生 sleepTime ==> 运行时间超过预期（可接受）
         *          * 2. 如果大于实际值，可能导致 1.使用线程变多 2.实际间隔时间小于冷却时间（没有了sleepTime） ==> 实际浏览量少于预期
         * 注：建议首次使用时务必测试，其余情况可以直接赋值或采用默认值
         */
//        handler.doTest();
//        handler.setPer_blog_cost_sec(3);

        /**
         * TODO 每篇浏览次数范围 [min, max]，两种模式：
         * 1. 平均刷：min = max
         * 2. 随机刷：min != max。注：为了数据更加离散，这里可以用负数（推荐）==> [-n, n] 或 [-n/2, n]
         */
        handler.setMin(-600);
        handler.setMax(600);


        /**
         * TODO 启动多少个线程去刷，
         * 注：最优线程数 n = 文章总数/BEST_LOAD（向上进位），此时线程刚好不用因为冷却时间等待，也不用因为任务多而排队
         *     1.当 threads > n，程序只会采用最优线程数，自动关闭无用线程
         *     2.当 threads < n，程序不做处理，每个线程任务量大了点而已
         */
        handler.setThreads(3);



        handler.start();
    }
}
