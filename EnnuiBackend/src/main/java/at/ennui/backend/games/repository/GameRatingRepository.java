package at.ennui.backend.games.repository;

import at.ennui.backend.games.model.GameRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRatingRepository extends JpaRepository<GameRatingEntity,Long> {
    List<GameRatingEntity> findByGameId(long gameId);
    GameRatingEntity findByGameIdAndUserId(long gameId, long userId);
}
