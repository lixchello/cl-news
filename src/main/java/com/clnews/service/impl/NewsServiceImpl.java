package com.clnews.service.impl;

import com.clnews.constant.Constant;
import com.clnews.dao.NewsMapper;
import com.clnews.domain.News;
import com.clnews.enums.SourceEnum;
import com.clnews.processor.ToutiaoNewsPuller;
import com.clnews.service.NewsService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with cl-news
 * Created By lxc
 * Date: 2019-03-18
 *
 * @author lxc
 */
@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private ToutiaoNewsPuller toutiaoNewsPuller;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);


    @Override
    public int insertSelective(News record) {
        return newsMapper.insertSelective(record);
    }

    @Override
    public List<News> listNewsAll() {
        List<News> newsList = newsMapper.listNewsAll(Constant.START, Constant.END);
        if (null == newsList || newsList.isEmpty()) {
            return Lists.newArrayList();
        }
        return newsList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void pullNews() {

        for (final SourceEnum sourceEnum : SourceEnum.values()) {
            if (sourceEnum.key.equalsIgnoreCase(SourceEnum.TOU_TIAO.key)) {
                executorService.submit(new TtPullTask(sourceEnum));
            }
            if (sourceEnum.key.equalsIgnoreCase(SourceEnum.SOU_HU.key)) {
                executorService.submit(new ShPullTask(sourceEnum));
            }
        }
    }

    public class TtPullTask implements Runnable {

        private SourceEnum sourceEnum;

        public TtPullTask(SourceEnum sourceEnum) {
            this.sourceEnum = sourceEnum;
        }

        @Override
        public void run() {
            List<News> newsList = toutiaoNewsPuller.pullNews();
            if (null == newsList || newsList.isEmpty()) {
                logger.info("【{}】拉取内容为空不需要插入!", sourceEnum.name);
                return;
            }
            for (News news : newsList) {
                newsMapper.insertSelective(news);
            }
        }
    }

    public class ShPullTask implements Runnable {

        private SourceEnum sourceEnum;

        public ShPullTask(SourceEnum sourceEnum) {
            this.sourceEnum = sourceEnum;
        }

        @Override
        public void run() {
            // TODO: 2019-03-22 添加你写的搜狐的拉取器
            List<News> newsList = souhuNewsPuller.pullNews();
            if (null == newsList || newsList.isEmpty()) {
                logger.info("【{}】拉取内容为空不需要插入!", sourceEnum.name);
                return;
            }
            for (News news : newsList) {
                newsMapper.insertSelective(news);
            }
        }
    }

}
