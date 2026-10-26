
package com.fulfillx.notification_service.repository;

import com.fulfillx.notification_service.domain.model.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    boolean existsByEventId(UUID eventId);

    Optional<Notification> findByEventId(UUID eventId);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO notifications (
                event_id,
                recipient,
                channel,
                subject,
                message,
                status,
                created_at,
                attempts
            )
            VALUES (
                :eventId,
                :recipient,
                :channel,
                :subject,
                :message,
                'PENDING',
                CURRENT_TIMESTAMP,
                0
            )
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("eventId") UUID eventId,
            @Param("recipient") String recipient,
            @Param("channel") String channel,
            @Param("subject") String subject,
            @Param("message") String message
    );

    @Modifying
    @Query(value = """
            UPDATE notifications
            SET status = 'PROCESSING',
                attempts = attempts + 1,
                processing_token = :token,
                lease_until = :leaseUntil,
                last_error = NULL
            WHERE event_id = :eventId
              AND attempts < :maxAttempts
              AND (
                    status IN ('PENDING', 'FAILED')
                    OR (
                        status = 'PROCESSING'
                        AND lease_until <= :now
                    )
              )
            """, nativeQuery = true)
    int claimForProcessing(
            @Param("eventId") UUID eventId,
            @Param("token") UUID token,
            @Param("leaseUntil") Instant leaseUntil,
            @Param("now") Instant now,
            @Param("maxAttempts") int maxAttempts
    );


    @Modifying
    @Transactional
    @Query(value = """
            UPDATE notifications
            SET status = 'SENT',
                sent_at = CURRENT_TIMESTAMP,
                last_error = NULL,
                lease_until = NULL,
                processing_token = NULL
            WHERE event_id = :eventId
              AND status = 'PROCESSING'
              AND processing_token = :token
            """, nativeQuery = true)
    int markSentIfTokenMatches(
            @Param("eventId") UUID eventId,
            @Param("token") UUID token
    );

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE notifications
            SET status = 'FAILED',
                last_error = :error,
                sent_at = NULL,
                lease_until = NULL,
                processing_token = NULL
            WHERE event_id = :eventId
              AND status = 'PROCESSING'
              AND processing_token = :token
            """, nativeQuery = true)
    int markFailedIfTokenMatches(
            @Param("eventId") UUID eventId,
            @Param("token") UUID token,
            @Param("error") String error
    );

    @Query(value = """
        SELECT event_id
        FROM notifications
        WHERE attempts < :maxAttempts
          AND (
                status IN ('PENDING', 'FAILED')
                OR (
                    status = 'PROCESSING'
                    AND lease_until <= :now
                )
          )
        ORDER BY created_at ASC
        LIMIT 100
        """, nativeQuery = true)
    List<UUID> findRetryableEventIds(
            @Param("now") Instant now,
            @Param("maxAttempts") int maxAttempts
    );

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE notifications
        SET status = 'FAILED',
            last_error = 'Processing lease expired after maximum attempts',
            lease_until = NULL,
            processing_token = NULL
        WHERE status = 'PROCESSING'
          AND lease_until <= :now
          AND attempts >= :maxAttempts
        """, nativeQuery = true)
    int failExpiredProcessingAfterMaxAttempts(
            @Param("now") Instant now,
            @Param("maxAttempts") int maxAttempts
    );

}
