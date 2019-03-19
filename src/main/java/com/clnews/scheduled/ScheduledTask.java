package com.clnews.scheduled;

import com.clnews.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created with cl-news
 * Created By lxc
 * Date: 2019-03-19
 *
 * @author lxc
 */
@Component
public class ScheduledTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NewsService newsService;


    @Scheduled(cron = "* * 0/1 * * ?")
    public void logTime() {
        logger.info("【爬虫定时】开始执行！");
        long startTime = System.currentTimeMillis();
        try {
            newsService.pullNews();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("【爬虫定时】异常 e = {}", e);
        }
        long endTime = System.currentTimeMillis();
        logger.info("【爬虫定时】结束,运行时间 = {}ms", (endTime - startTime));
    }

}
