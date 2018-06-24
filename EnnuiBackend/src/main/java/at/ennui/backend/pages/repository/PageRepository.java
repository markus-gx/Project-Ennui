package at.ennui.backend.pages.repository;

import at.ennui.backend.pages.model.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageEntity,Long>{
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE pages p SET p.crawled = :crawled",nativeQuery = true)
    void resetCrawling(@Param("crawled") boolean crawled);

    List<PageEntity> findTop50ByCrawled(boolean crawled);

    @Query("SELECT p FROM PageEntity p WHERE p.id = :id AND p.crawled = 0")
    PageEntity getPageAlreadyCrawled(@Param("id") Long pageId);
}
