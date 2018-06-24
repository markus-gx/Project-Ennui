package at.ennui.backend.pages.controller;

import at.ennui.backend.facebook.FacebookService;
import at.ennui.backend.pages.model.PageEntity;
import at.ennui.backend.pages.model.PageHolder;
import at.ennui.backend.pages.repository.PageRepository;
import at.ennui.backend.user.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class PageController {
    private PageRepository pageRepository;
    private FacebookService facebookService;
    private UserService userService;
    private Cache<String,Boolean> crawledFromPagesCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    public PageHolder crawlPagesFromUser(){
        Boolean present = crawledFromPagesCache.getIfPresent(userService.getUserToken());
        PageHolder pageHolder = new PageHolder();
        if(present == null || !present){
            pageHolder.setSuccess(true);
            List<PageEntity> pageEntities = getPagesFromUser(userService.getUserToken());
            pageHolder.setUserPagesCount(pageEntities.size());
            List<Long> pageIds = pageEntities.stream().filter(p -> p.getId() != null).map(PageEntity::getId).collect(Collectors.toList());
            List<PageEntity> foundPages = pageRepository.findAll(pageIds);
            Set<Long> foundPageIds = foundPages.stream().filter(p -> p.getId() != null).map(PageEntity::getId).collect(Collectors.toSet());
            if(foundPages.size() < pageEntities.size()){
                List<PageEntity> toSave = pageEntities.stream().filter(p -> !foundPageIds.contains(p.getId())).collect(Collectors.toList());
                pageRepository.save(toSave);
                pageHolder.setUserPagesSavedCount(toSave.size());
            }
            return pageHolder;
        }
        else{
            pageHolder.setSuccess(false);
            pageHolder.setMsg("Already crawled within the last hour!");
            return pageHolder;
        }
    }

    public void save(List<PageEntity> pageEntities){
        pageRepository.save(pageEntities);
    }

    public void resetCrawlingInRepository(){
        pageRepository.resetCrawling(false);
    }

    public List<PageEntity> getPagesLimitedWithCrawlStateFalse(){
        return pageRepository.findTop50ByCrawled(false);
    }

    public void setPagesCrawled(List<PageEntity> pages){
        pages.forEach(p -> p.setCrawled(true));
        pageRepository.save(pages);
    }

    public List<PageEntity> getPages(){
        return pageRepository.findAll();
    }

    public List<PageEntity> getPagesFromUser(String token){
        Facebook facebook = new FacebookTemplate(token,"atennui");
        PagingParameters pagingParameters = new PagingParameters(100,null,null,null);
        PagedList<Page> pages = facebook.likeOperations().getPagesLiked(pagingParameters);
        PagedList<Page> tmp = pages;
        while(tmp.getNextPage() != null){
            tmp = facebook.likeOperations().getPagesLiked(tmp.getNextPage());
            pages.addAll(tmp);
        }
        return pages.stream().map(p -> new PageEntity(Long.parseLong(p.getId()),p.getName())).collect(Collectors.toList());
    }

    public PageEntity checkPageAlreadyCrawled(Long id){
        PageEntity p = pageRepository.getPageAlreadyCrawled(id);
        return p;
    }

    public List<PageEntity> getPagesFromPage(String id, String token){
        Facebook facebook = new FacebookTemplate(token,"atennui");
        PagingParameters pagingParameters = new PagingParameters(100,null,null,null);
        PagedList<Page> pages = facebook.likeOperations().getPagesLiked(id,pagingParameters);
        PagedList<Page> tmp = pages;
        while(tmp.getNextPage() != null){
            tmp = facebook.likeOperations().getPagesLiked(id,tmp.getNextPage());
            pages.addAll(tmp);
        }
        return pages.stream().map(p -> new PageEntity(Long.parseLong(p.getId()),p.getName())).collect(Collectors.toList());
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setFacebookService(FacebookService facebookService){
        this.facebookService = facebookService;
    }

    @Autowired
    public void setPageRepository(PageRepository pageRepository){
        this.pageRepository = pageRepository;
    }
}
