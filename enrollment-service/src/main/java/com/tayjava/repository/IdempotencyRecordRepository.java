package com.tayjava.repository;

import com.tayjava.entity.IdempotencyRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {

  Optional<IdempotencyRecord> findByScopeAndIdempotencyKey(String scope, String idempotencyKey);
}
