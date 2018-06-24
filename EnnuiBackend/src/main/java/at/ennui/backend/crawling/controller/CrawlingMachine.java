package at.ennui.backend.crawling.controller;

import at.ennui.backend.events.EventService;
import at.ennui.backend.pages.PageService;
import at.ennui.backend.pages.model.PageEntity;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CrawlingMachine {
    private List<PageEntity> pageList = new ArrayList<>();
    //private volatile int tasksToBeExecuted = 0;
    private final int maxRuntime = 15; //In minutes
    private Queue<PageEntity> pagesToCrawl = new LinkedList<>();
    private PageService pageService;
    private EventService eventService;
    private final String fbToken;

    public CrawlingMachine(String fbToken){
        this.fbToken = fbToken;
    }

    public synchronized Queue<PageEntity> getPagesToCrawl() {
        notifyAll();
        return pagesToCrawl;
    }

    public synchronized PageEntity getAndRemove() {
        PageEntity p = pagesToCrawl.remove();
        // notifyAll();
        return p;
    }

    public synchronized void addPagesToCrawl(List<PageEntity> value) {
        pagesToCrawl.addAll(value);
        notifyAll();
    }

    public synchronized void addPagesToCrawlExceptSavedOnes(List<PageEntity> value){
        pagesToCrawl.addAll(value.stream().filter(dto -> pageService.checkIfPageAlreadyCrawled(dto.getId()) == null).collect(Collectors.toList()));
        notifyAll();
    }

    public void startCrawlingMachine(ExecutorService executorService){
        /*HashMap<Object,Object> crawl = new HashMap<>();
        pageList.add(crawl);*/
        System.out.println("started!");
        Thread mainCrawlThread = new Thread(() -> {
            long threadStart = System.currentTimeMillis();
            boolean running = true;
            synchronized(this){
                do{
                    if(this.getPagesToCrawl().size() > 0){
                        Runnable work = new PageCrawlTask(this.getAndRemove(),this);
                        executorService.execute(work);
                    }
                    else{
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(System.currentTimeMillis() > (threadStart+60*1000*maxRuntime)){
                        running = false;
                    }
                }while(running);
                //executorService.shutdownNow();
                try {
                    if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.out.println("task completed");
                    } else {
                        System.out.println("Forcing shutdown...");
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                }
                savePagesToDB();
                pageService.setPagesCrawled(pageList);
            }
        });
        mainCrawlThread.start();
    }

    public void crawlPage(PageEntity entity,CrawlingMachine crawlingMachine){
        if(!containsPage(entity)){
            putPage(entity);
            EventCrawlTask t = new EventCrawlTask(entity.getId().toString(),this.eventService,fbToken);
            t.run();
            List<PageEntity> likes = pageService.getPagesFromPage(entity.getId().toString(),fbToken);
            crawlingMachine.addPagesToCrawlExceptSavedOnes(likes);
        }
    }
    public boolean containsPage(PageEntity pageEntity){
        try{
            return pageList.contains(pageEntity);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return false;
    }
    public synchronized void putPage(PageEntity pageEntity){
        if (pageList.size() >=150){
            savePagesToDB();
            pageList.clear();
        }
        pageEntity.setCrawled(true);
        pageList.add(pageEntity);
        notifyAll();
    }
    public void savePagesToDB(){
        pageService.save(pageList);
    }
    /*public synchronized void taskFinished(){
        tasksToBeExecuted -=1;
    }

    public synchronized void taskWillBeExecuted(){
        tasksToBeExecuted +=1;
    }*/

    public void setPageService(PageService pageService){
        this.pageService = pageService;
    }

    public void setEventService(EventService eventService){
        this.eventService = eventService;
    }
}
