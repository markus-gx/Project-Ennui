package at.ennui.backend.pages.api;

import at.ennui.backend.pages.PageService;
import at.ennui.backend.pages.model.PageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pages")
@CrossOrigin
public class PageApi {
    private PageService pageService;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public PageHolder crawlPagesFromUser(){
        return pageService.crawlPagesFromUser();
    }

    @Autowired
    public void setPageService(PageService pageService){
        this.pageService = pageService;
    }
}
