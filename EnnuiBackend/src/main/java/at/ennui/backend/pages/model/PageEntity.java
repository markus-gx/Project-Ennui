package at.ennui.backend.pages.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pages")
public class PageEntity {
    @Id
    private Long id;
    private String name;
    private boolean crawled;

    public PageEntity(){
    }

    public PageEntity(Long id, String name){
        this.id =id;
        this.name = name;
    }

    public boolean isCrawled() {
        return crawled;
    }

    public void setCrawled(boolean crawled) {
        this.crawled = crawled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
