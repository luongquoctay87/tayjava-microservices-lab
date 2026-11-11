package com.tayjava.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inbox_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboxEvent {

  @Id
  @Column(name = "event_id", length = 64)
  private String eventId;

  @Column(name = "event_type", nullable = false, length = 64)
  private String eventType;

  @Column(name = "received_at", nullable = false)
  private Instant receivedAt;
}
