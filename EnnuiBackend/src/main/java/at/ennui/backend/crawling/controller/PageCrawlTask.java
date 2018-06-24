package at.ennui.backend.crawling.controller;

import at.ennui.backend.pages.model.PageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageCrawlTask implements Runnable {
    private PageEntity i;
    private final CrawlingMachine crawlingMachine;
    public PageCrawlTask(PageEntity page, CrawlingMachine machine){
        i = page;
        crawlingMachine = machine;
        //machine.taskWillBeExecuted();
    }
    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info(Thread.currentThread().getName() + " started");
        try {
            crawlingMachine.crawlPage(i,crawlingMachine);
        } catch (Exception e) {
            System.out.println("Ex: " + e.getMessage());
        }
        logger.info(Thread.currentThread().getName() + " stopped!");
        //crawlingMachine.taskFinished();
        synchronized (crawlingMachine){
            crawlingMachine.notifyAll();
        }
    }
}
