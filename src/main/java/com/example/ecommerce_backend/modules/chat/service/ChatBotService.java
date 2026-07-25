package com.example.ecommerce_backend.modules.chat.service;

import com.example.ecommerce_backend.modules.chat.entity.ChatBotQuestion;
import com.example.ecommerce_backend.modules.chat.repository.ChatBotQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChatBotService {

    private final ChatBotQuestionRepository questionRepository;

    public ChatBotService(ChatBotQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<ChatBotQuestion> getRootQuestions() {
        return questionRepository.findByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public Optional<ChatBotQuestion> getNextQuestion(Long parentId) {
        List<ChatBotQuestion> children = questionRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(parentId);
        return children.isEmpty() ? Optional.empty() : Optional.of(children.get(0));
    }

    @Transactional(readOnly = true)
    public Optional<ChatBotQuestion> getQuestionByKey(String questionKey) {
        return questionRepository.findByQuestionKey(questionKey);
    }

    @Transactional(readOnly = true)
    public Optional<ChatBotQuestion> getNextQuestionByKey(String currentQuestionKey, String selectedOption) {
        return questionRepository.findByQuestionKey(currentQuestionKey)
                .flatMap(q -> {
                    List<ChatBotQuestion> children = questionRepository
                            .findByParentIdAndIsActiveTrueOrderBySortOrderAsc(q.getId());
                    return children.stream()
                            .filter(c -> c.getQuestionKey().equals(selectedOption))
                            .findFirst();
                });
    }
}
