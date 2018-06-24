package at.ennui.backend.crawling;

import at.ennui.backend.crawling.controller.CrawlingController;
import at.ennui.backend.crawling.exception.CrawlingException;
import at.ennui.backend.pages.model.PageEntity;
import at.ennui.backend.user.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrawlingService {
    private UserService userService;
    private CrawlingController crawlingController;

    public void crawlEventsFromPages() {
        if (userService.getCurrentUser() != null){
            if (userService.getCurrentUser().isAdmin()) {
                crawlingController.crawlEventsFromPages();
            } else {
                throw new CrawlingException("You dont have permission to perform this command!");
            }
        }
    }

    public void crawlEventsFromPagesAsAdmin(){
        crawlingController.crawlEventsFromPages();
    }

    public void crawlEverythingAsAdmin(){
        crawlingController.crawlEverything();
    }

    public void crawlEverything(){
        if(userService.getCurrentUser() != null && userService.getCurrentUser().isAdmin()){
            crawlingController.crawlEverything();
        }
        else{
            throw new CrawlingException("You dont have permission to perform this command!");
        }
    }

    public void crawlEventsFromPages(List<PageEntity> pageEntities){
        if(pageEntities != null && pageEntities.size() > 0){
            crawlingController.crawlEventsFromPages(pageEntities);
        }
    }

    @Autowired
    public void setCrawlingController(CrawlingController crawlingController) {
        this.crawlingController = crawlingController;
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }
}
