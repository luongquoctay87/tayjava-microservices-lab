package com.tayjava.repository;
import com.tayjava.entity.SagaInstance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {
  Optional<SagaInstance> findByIdempotencyKey(String idempotencyKey);
}
