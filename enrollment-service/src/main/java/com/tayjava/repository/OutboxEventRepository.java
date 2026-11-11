package com.tayjava.repository;

import com.tayjava.entity.OutboxEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

  List<OutboxEvent> findTop50ByStatusOrderByIdAsc(OutboxEvent.Status status);
}
