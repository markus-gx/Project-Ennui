package at.ennui.backend.events.repository;

import at.ennui.backend.events.model.UserClickEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserClicksRepository extends JpaRepository<UserClickEntity,Long> {
    UserClickEntity findByUserIdAndEventId(long userId, long eventId);

    List<UserClickEntity> findByUserId(long userId);
}
