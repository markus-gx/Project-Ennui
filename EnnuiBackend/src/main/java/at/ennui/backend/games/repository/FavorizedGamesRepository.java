package at.ennui.backend.games.repository;

import at.ennui.backend.games.model.FavorizedGameEntity;
import at.ennui.backend.games.model.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FavorizedGamesRepository extends JpaRepository<FavorizedGameEntity,Long>{
    FavorizedGameEntity findByUserIdAndGameId(long userId, long gameId);

    @Query("SELECT g.gameId FROM FavorizedGameEntity g WHERE g.userId = :userId")
    List<Long> findGameIdByUserId(@Param("userId") Long userId);

    @Transactional
    void deleteByGameId(long gameId);
}
