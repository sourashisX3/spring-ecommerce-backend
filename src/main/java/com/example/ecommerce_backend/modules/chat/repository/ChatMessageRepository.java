package com.example.ecommerce_backend.modules.chat.repository;

import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId, Pageable pageable);

    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    long countByRoomIdAndReadAtIsNull(Long roomId);
}
