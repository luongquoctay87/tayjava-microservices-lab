package com.tayjava.messaging;

import com.tayjava.entity.InboxEvent;
import com.tayjava.repository.InboxEventRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumer-side deduplication: insert event_id into inbox; duplicate key means already processed.
 */
@Component
@RequiredArgsConstructor
@Slf4j(topic = "InboxGuard")
public class InboxGuard {

  private final InboxEventRepository inboxEventRepository;

  /**
   * @return true if this is the first time we see the event (caller should process); false if
   *     duplicate.
   */
  @Transactional
  public boolean claim(String eventId, String eventType) {
    if (inboxEventRepository.existsById(eventId)) {
      log.info("Inbox duplicate skipped eventId={}", eventId);
      return false;
    }
    try {
      inboxEventRepository.saveAndFlush(
          InboxEvent.builder()
              .eventId(eventId)
              .eventType(eventType)
              .receivedAt(Instant.now())
              .build());
      return true;
    } catch (DataIntegrityViolationException ex) {
      log.info("Inbox race duplicate skipped eventId={}", eventId);
      return false;
    }
  }
}
