package com.example.ecommerce_backend.modules.chat.repository;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId, Pageable pageable);

    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    long countByRoomIdAndReadAtIsNull(Long roomId);

    @Query("SELECT m FROM ChatMessage m WHERE m.createdAt = (SELECT MAX(m2.createdAt) FROM ChatMessage m2 WHERE m2.roomId = m.roomId)")
    List<ChatMessage> findLastMessageForEachRoom();
}
