package at.ennui.backend.games.repository;

import at.ennui.backend.games.model.GameCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface GameCategoriesRepository extends JpaRepository<GameCategoryMapping,Long> {
    List<GameCategoryMapping> findByGameId(long gameId);

    @Transactional
    void deleteByGameId(long gameId);
}
