package at.ennui.backend.crawling.api;

import at.ennui.backend.crawling.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/crawling")
@CrossOrigin
public class CrawlingApi {
    private CrawlingService crawlingService;

    @RequestMapping(value = "/events",method = RequestMethod.POST)
    public void crawlEventsFromPages(){
        crawlingService.crawlEventsFromPages();
    }

    @RequestMapping(value = "/all",method = RequestMethod.POST)
    public void crawlEverything(){
        crawlingService.crawlEverything();
    }

    @Autowired
    public void setCrawlingService(CrawlingService crawlingService){
        this.crawlingService = crawlingService;
    }
}
