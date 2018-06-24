package at.ennui.backend.pages;

import at.ennui.backend.pages.controller.PageController;
import at.ennui.backend.pages.model.PageEntity;
import at.ennui.backend.pages.model.PageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageService {

    private PageController pageController;

    public PageHolder crawlPagesFromUser(){
        return pageController.crawlPagesFromUser();
    }

    public void save(List<PageEntity> pageEntities){
        pageController.save(pageEntities);
    }

    public List<PageEntity> getPages(){
        return pageController.getPages();
    }

    public List<PageEntity> getPagesLimitedWithCrawlStateFalse(){
        return pageController.getPagesLimitedWithCrawlStateFalse();
    }

    public void setPagesCrawled(List<PageEntity> pages){
        pageController.setPagesCrawled(pages);
    }

    public List<PageEntity> getPagesFromUser(String token){
        return pageController.getPagesFromUser(token);
    }

    public List<PageEntity> getPagesFromPage(String id, String token){
        List<PageEntity> pageEntities = pageController.getPagesFromPage(id,token);
        return pageEntities;
    }

    public PageEntity checkIfPageAlreadyCrawled(Long id){
        PageEntity entity = pageController.checkPageAlreadyCrawled(id);
        return entity;
    }

    public void resetCrawlingInRepository(){
        pageController.resetCrawlingInRepository();
    }

    @Autowired
    public void setPageController(PageController pageController){
        this.pageController = pageController;
    }
}
