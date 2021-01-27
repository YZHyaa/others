package net.yzx66.common;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Common {

    /**
     * 打开谷歌
     */
     public static ChromeDriver openChrome(){
        String  mainClassPath = Common.class.getClassLoader().getResource("").getPath().substring(1);
        String chromDriverPath = mainClassPath + "chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromDriverPath);
        return new ChromeDriver();
    }

    /**
     * 获取所有博客url
     * @param blogPath 博客链接
     * @return
     */
    public static String[] getBlogUrls(String blogPath) {
        StringBuilder urls = new StringBuilder();

        ChromeDriver chromeDriver = openChrome();

        int i = 1;
        while (true) {
            chromeDriver.get(blogPath + "/article/list/" + i);

            // 注：一页正好 40 篇
            for(int j = 1;j <= 40;j++){
                try{
                    // //*[@id="articleMeList-blog"]/div[2]/div[1]/h4/a
                    WebElement element = chromeDriver.findElementByXPath("//*[@id=\"articleMeList-blog\"]/div[2]/div[" + j + "]/h4/a");

                    String url = element.getAttribute("href");
                    urls.append(url+" ");

                }catch (Exception e){
                    chromeDriver.close();
                    String[] urlArr = new String(urls).split(" ");
                    System.out.println("【INFO】共扫描到" + urlArr.length +"篇博客");
                    return urlArr;
                }
            }
            i++;
        }
    }

    public static String[] getBlogUrls(String blogPath, int pageIdx) {
        return getBlogUrls(blogPath, pageIdx, false);
    }

    /**
     * 获取指定页的博客
     *
     * @param blogPath 博客链接
     * @param pageIdx 指定页索引
     * @return
     */
    public static String[] getBlogUrls(String blogPath, int pageIdx, boolean isTest){
        StringBuilder urls = new StringBuilder();

        ChromeDriver chromeDriver = Common.openChrome();

        chromeDriver.get(blogPath + "/article/list/" + pageIdx);
        // 注：一页正好 40 篇
        for(int j = 1;j <= 40;j++){
            try{
                // //*[@id="articleMeList-blog"]/div[2]/div[1]/h4/a
                WebElement element = chromeDriver.findElementByXPath("//*[@id=\"articleMeList-blog\"]/div[2]/div[" + j + "]/h4/a");
                String url = element.getAttribute("href");
                urls.append(url+" ");
            }catch (Exception e){
                break;
            }
        }

        chromeDriver.close();

        String[] urlArr = new String(urls).split(" ");
        if (isTest) {
            System.out.println("【TEST】共扫描到" + urlArr.length +"篇博客");
        } else {
            System.out.println("【INFO】共扫描到" + urlArr.length +"篇博客");
        }

        return urlArr;
    }


    /**
     * 获取指定分类专栏的所有博客
     */
    public static String[] getCategoryUrls(String categoryPath){
        StringBuilder urls = new StringBuilder();
        ChromeDriver webDriver = Common.openChrome();

        webDriver.get(categoryPath);

        for(int j = 1;j <= 40;j++){
            try{
                // class="column_article_list"
                // //*[@class=\"column_article_list\"]/li[" + j + "]/a
                WebElement element = webDriver.findElementByXPath("//*[@class=\"column_article_list\"]/li[" + j + "]/a");
                String url = element.getAttribute("href");
                urls.append(url+" ");
            }catch (Exception e){
                break;
            }
        }

        webDriver.close();

        String[] urlArr = new String(urls).split(" ");
        System.out.println("【INFO】共扫描到" + urlArr.length +"篇博客");
        return urlArr;
    }

    /**
     * 刷一篇博客需要多少时间
     */
    public static double testQueryTime(String[] urlArray){

        System.out.println("【TEST】启动测试...");
        double avgSec;
        try {
            if (urlArray == null || urlArray.length == 0) {
                throw new IllegalArgumentException();
            }

            int testLen = urlArray.length / 2 <= 20 ? urlArray.length / 2 : 20;

            ChromeDriver chromeDriver = Common.openChrome();
            long start = System.currentTimeMillis();
            for (int i = 0; i < testLen; i++) {
                chromeDriver.get(urlArray[i]);
            }
            long end = System.currentTimeMillis();

            long avg = (end - start) / testLen;
            avgSec =  (double)avg / 1000;

            chromeDriver.close();

            System.out.println("【TEST】平均刷一篇博客耗时：" + avgSec + " 秒");
            System.out.println("【TEST】-------------------------------------------------------------------------------");
            System.out.println("【TEST】测试得 PER_BLOG_COST_SEC = " + avgSec + ",  THR_BEST_LOAD = " + (int)(60/avgSec));
            System.out.println("【TEST】-------------------------------------------------------------------------------");
        } catch (Exception e) {
            System.out.println("【ERROR】测试失败，采用 DEFALUT_VALUE = 2.0");
            return 2.0;
        }

        return avgSec;
    }

}
