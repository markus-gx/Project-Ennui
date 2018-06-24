package at.ennui.backend.events.repository;

import at.ennui.backend.events.model.FavorizedEventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FavorizedEventsRepository extends JpaRepository<FavorizedEventsEntity,Long> {
    FavorizedEventsEntity findByUserIdAndEventId(Long userId, Long eventId);

    @Transactional
    void deleteByEventId(long id);

    @Query("SELECT m.eventId FROM FavorizedEventsEntity m WHERE m.userId = :userId")
    List<Long> findEventIdByUserId(@Param("userId") Long userId);
}
