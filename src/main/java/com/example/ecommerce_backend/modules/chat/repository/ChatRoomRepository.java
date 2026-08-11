package com.example.ecommerce_backend.modules.chat.repository;

import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUuid(String uuid);

    List<ChatRoom> findByUserId(Long userId);

    Page<ChatRoom> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<ChatRoom> findByStatusOrderByCreatedAtAsc(String status, Pageable pageable);

    Page<ChatRoom> findByAgentIdOrderByCreatedAtDesc(Long agentId, Pageable pageable);

    long countByStatus(String status);
}
