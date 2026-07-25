package com.example.ecommerce_backend.modules.chat.repository;

import com.example.ecommerce_backend.modules.chat.entity.ChatBotQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatBotQuestionRepository extends JpaRepository<ChatBotQuestion, Long> {

    List<ChatBotQuestion> findByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc();

    List<ChatBotQuestion> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);

    Optional<ChatBotQuestion> findByQuestionKey(String questionKey);
}
