package at.ennui.backend.games.repository;

import at.ennui.backend.games.model.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Repository
public interface GameRepository extends JpaRepository<GameEntity,Long>{
    List<GameEntity> findByActivated(boolean activated);

    List<GameEntity> findByOwnerId(Long ownerId);
}
