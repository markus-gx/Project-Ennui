package at.ennui.backend.user.repository;

import at.ennui.backend.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    @Query("SELECT u FROM UserEntity u WHERE fbId = :fbid")
    UserEntity getUserEntityByFbId(@Param("fbid") String fbId);
}
