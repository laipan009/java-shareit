package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequestor_Id(Integer requestor);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id <> :userId ORDER BY ir.created DESC")
    Slice<ItemRequest> findAllItemRequestsSortedByCreatedDesc(Pageable pageable, @Param("userId") Integer userId);
}