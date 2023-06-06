package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findById(Long itemId);

    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT it FROM Item it WHERE it.available = true AND (upper(it.name) LIKE upper(CONCAT('%',:text,'%')) " +
            "OR upper(it.description) LIKE upper(CONCAT('%',:text,'%')))")
    List<Item> searchItems(@Param("text") String text);

    List<Item> findAllByRequestId(Long requestId);
}
