package at.ennui.backend.crawling.controller;

import at.ennui.backend.events.EventService;
import at.ennui.backend.events.model.EventEntity;
import at.ennui.backend.facebook.FacebookService;
import at.ennui.backend.pages.PageService;
import at.ennui.backend.pages.model.PageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.UncategorizedApiException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class CrawlingController {
    private PageService pageService;
    private EventService eventService;
    private FacebookService facebookService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void crawlEventsFromPages(){
        setup();
        Thread mainCrawlThread = new Thread(() -> {
            List<PageEntity> pageEntities;
            do {
                ExecutorService executors = Executors.newFixedThreadPool(50);
                pageEntities = pageService.getPagesLimitedWithCrawlStateFalse();
                final String token = facebookService.getAppAccessToken();
                pageEntities.forEach(p -> executors.execute(new CrawlEventFromPageTask(p.getId()+"",token,eventService)));

                executors.shutdown();
                try {
                    executors.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pageService.setPagesCrawled(pageEntities);
            }while(pageEntities.size() != 0);
           logger.info("everything done (pages)");
        });
        mainCrawlThread.start();
    }

    public void crawlEventsFromPages(List<PageEntity> pageEntities){
        Thread mainCrawlThread = new Thread(() -> {
            ExecutorService executors = Executors.newFixedThreadPool(10);
            final String token = facebookService.getAppAccessToken();
            pageEntities.forEach(p -> executors.execute(new CrawlEventFromPageTask(p.getId()+"",token,eventService)));

            executors.shutdown();
            try {
                executors.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pageService.setPagesCrawled(pageEntities);
            logger.info("everything done (pages)");
        });
        mainCrawlThread.start();
    }

    public void crawlEverything(){
        setup();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<PageEntity> startup = pageService.getPagesLimitedWithCrawlStateFalse();
        if(startup == null || startup.isEmpty())
        {
            setup();
            startup = pageService.getPagesLimitedWithCrawlStateFalse();
        }
        if(startup != null && startup.size() > 0){
            CrawlingMachine crawlingMachine = new CrawlingMachine(facebookService.getAppAccessToken());
            crawlingMachine.setPageService(this.pageService);
            crawlingMachine.setEventService(this.eventService);
            crawlingMachine.addPagesToCrawl(startup);
            crawlingMachine.startCrawlingMachine(executorService);
        }
    }


    private void setup(){
        pageService.resetCrawlingInRepository();
    }

    @Autowired
    public void setFacebookService(FacebookService facebookService) {
        this.facebookService = facebookService;
    }

    @Autowired
    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}

final class CrawlEventFromPageTask implements Runnable{
    private String pageId;
    private String token;
    private EventService eventService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public CrawlEventFromPageTask(String pageId, String token, EventService eventService){
        this.pageId = pageId;
        this.token = token;
        this.eventService = eventService;
    }

    @Override
    public void run() {
        try{
            eventService.addEvents(eventService.getEventsById(pageId,token));
        }
        catch(UncategorizedApiException e){
            logger.info("ID: " + pageId + " NOT crawled!");
        }
    }
}
