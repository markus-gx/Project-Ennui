package at.ennui.backend.events.repository;

import at.ennui.backend.events.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity,Long> {
    List<EventEntity> findByOwnerId(Long ownerId);

    @Query("SELECT COUNT(e) AS ct,e.country FROM EventEntity e GROUP BY e.country ORDER BY ct DESC")
    List<Object> getCountriesWithEventCount();

    @Query("SELECT e FROM EventEntity e WHERE activated = false")
    List<EventEntity> getNotActivatedEvents();

    @Query("SELECT e FROM EventEntity e WHERE eventId = :eventId")
    EventEntity getEntityByFbId(@Param("eventId") Long eventId);

    @Query("SELECT e FROM EventEntity e ")
    List<EventEntity> getAllEvents();

    List<EventEntity> findByIdIn(List<Long> ids);

    @Query("SELECT e FROM EventEntity e WHERE e.latitude = :latitude AND e.longitude = :longitude AND e.starttime > curdate()")
    List<EventEntity> findByLatitudeAndLongitude(@Param("latitude")Double latitude,@Param("longitude") Double longitude);

    /*@Query("SELECT e FROM EventEntity e WHERE (6371 * (" +
            "2 * ATAN2(" +
            "SQRT(" +
            "SIN(RADIANS((:lat-latitude))/2) * SIN(RADIANS((:lat-latitude)/2)) +" +
            "COS(RADIANS(latitude)) * COS(RADIANS(:lat)) * SIN(RADIANS((:long-longitude)/2)) *" +
            "SIN(RADIANS((:long-longitude)/2))" +
            "),SQRT(" +
            "1-(SIN(RADIANS((:lat-latitude))/2) * SIN(RADIANS((:lat-latitude)/2)) +" +
            "COS(RADIANS(latitude)) * COS(RADIANS(:lat)) * SIN(RADIANS((:long-longitude)/2)) *" +
            "SIN(RADIANS((:long-longitude)/2)))" +
            ")" +
            ")" +
            ")) < :radius")*/
    @Query("SELECT e FROM EventEntity e WHERE e.starttime > curdate() and (ST_DISTANCE(POINT(e.latitude, e.longitude ), POINT(:lat, :long)) * 100) < :radius ORDER BY starttime, (ST_DISTANCE(POINT(e.latitude, e.longitude ), POINT(:lat, :long)) * 100)")
    List<EventEntity> getEntitiesByRadius(@Param("radius") int radius, @Param("long") Double longitude, @Param("lat") Double latitude);
}
