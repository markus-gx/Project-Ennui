package at.ennui.backend.crawling.scheduled;

import at.ennui.backend.crawling.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledCrawling {
    private CrawlingService crawlingService;

    @Scheduled(cron = "0 0 6 * * *")  //Cronjob for Tasks = second minute hour day month weekday
    public void crawlsEventsFromPages(){
        crawlingService.crawlEverythingAsAdmin();
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void crawlOnlyEvents(){
        crawlingService.crawlEventsFromPagesAsAdmin();
    }

    @Autowired
    public void setCrawlingService(CrawlingService crawlingService){
        this.crawlingService = crawlingService;
    }
}
