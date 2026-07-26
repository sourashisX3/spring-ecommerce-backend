package com.example.ecommerce_backend.modules.chat.service;

import com.example.ecommerce_backend.modules.chat.entity.ChatBotQuestion;
import com.example.ecommerce_backend.modules.chat.exception.ChatQuestionNotFoundException;
import com.example.ecommerce_backend.modules.chat.repository.ChatBotQuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatBotServiceTest {

    @Mock
    private ChatBotQuestionRepository questionRepository;

    @InjectMocks
    private ChatBotService chatBotService;

    @Test
    void getRootQuestions_shouldReturnActiveRootsOrdered() {
        ChatBotQuestion q1 = ChatBotQuestion.builder()
                .id(1L).questionKey("greeting").questionText("Hello!")
                .sortOrder(1).isActive(true).build();
        ChatBotQuestion q2 = ChatBotQuestion.builder()
                .id(2L).questionKey("help").questionText("How can I help?")
                .sortOrder(2).isActive(true).build();
        when(questionRepository.findByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc())
                .thenReturn(List.of(q1, q2));

        List<ChatBotQuestion> result = chatBotService.getRootQuestions();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getQuestionKey()).isEqualTo("greeting");
        assertThat(result.get(1).getQuestionKey()).isEqualTo("help");
    }

    @Test
    void getRootQuestions_whenEmpty_shouldReturnEmptyList() {
        when(questionRepository.findByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc())
                .thenReturn(List.of());

        List<ChatBotQuestion> result = chatBotService.getRootQuestions();

        assertThat(result).isEmpty();
    }

    @Test
    void getNextQuestion_shouldReturnFirstChild() {
        ChatBotQuestion child = ChatBotQuestion.builder()
                .id(2L).parentId(1L).questionKey("order-status")
                .questionText("Check order status").sortOrder(1).isActive(true).build();
        when(questionRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(1L))
                .thenReturn(List.of(child));

        Optional<ChatBotQuestion> result = chatBotService.getNextQuestion(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getQuestionKey()).isEqualTo("order-status");
    }

    @Test
    void getNextQuestion_whenNoChildren_shouldReturnEmpty() {
        when(questionRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(1L))
                .thenReturn(List.of());

        Optional<ChatBotQuestion> result = chatBotService.getNextQuestion(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getQuestionByKey_shouldReturnQuestion() {
        ChatBotQuestion q = ChatBotQuestion.builder()
                .id(1L).questionKey("help").questionText("Help topic").build();
        when(questionRepository.findByQuestionKey("help")).thenReturn(Optional.of(q));

        Optional<ChatBotQuestion> result = chatBotService.getQuestionByKey("help");

        assertThat(result).isPresent();
        assertThat(result.get().getQuestionKey()).isEqualTo("help");
    }

    @Test
    void getQuestionByKey_whenNotFound_shouldReturnEmpty() {
        when(questionRepository.findByQuestionKey("nonexistent")).thenReturn(Optional.empty());

        Optional<ChatBotQuestion> result = chatBotService.getQuestionByKey("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void getNextQuestionByKey_shouldReturnMatchingChild() {
        ChatBotQuestion parent = ChatBotQuestion.builder().id(1L).questionKey("help").build();
        ChatBotQuestion child1 = ChatBotQuestion.builder()
                .id(2L).parentId(1L).questionKey("order-status")
                .isActive(true).sortOrder(1).build();
        ChatBotQuestion child2 = ChatBotQuestion.builder()
                .id(3L).parentId(1L).questionKey("return")
                .isActive(true).sortOrder(2).build();
        when(questionRepository.findByQuestionKey("help")).thenReturn(Optional.of(parent));
        when(questionRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(1L))
                .thenReturn(List.of(child1, child2));

        Optional<ChatBotQuestion> result = chatBotService.getNextQuestionByKey("help", "return");

        assertThat(result).isPresent();
        assertThat(result.get().getQuestionKey()).isEqualTo("return");
    }

    @Test
    void getNextQuestionByKey_whenParentNotFound_shouldReturnEmpty() {
        when(questionRepository.findByQuestionKey("nonexistent")).thenReturn(Optional.empty());

        Optional<ChatBotQuestion> result = chatBotService.getNextQuestionByKey("nonexistent", "option");

        assertThat(result).isEmpty();
    }

    @Test
    void toggleStatus_shouldToggle() {
        ChatBotQuestion q = ChatBotQuestion.builder()
                .id(1L).uuid("question-uuid").questionKey("help")
                .questionText("Help").isActive(true).build();
        when(questionRepository.findByUuid("question-uuid")).thenReturn(Optional.of(q));

        boolean result = chatBotService.toggleStatus("question-uuid", false);

        assertThat(result).isTrue();
        assertThat(q.isActive()).isFalse();
        verify(questionRepository).save(q);
    }

    @Test
    void toggleStatus_whenAlreadyInDesiredState_shouldReturnFalse() {
        ChatBotQuestion q = ChatBotQuestion.builder()
                .id(1L).uuid("question-uuid").questionKey("help")
                .questionText("Help").isActive(false).build();
        when(questionRepository.findByUuid("question-uuid")).thenReturn(Optional.of(q));

        boolean result = chatBotService.toggleStatus("question-uuid", false);

        assertThat(result).isFalse();
        verify(questionRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(questionRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatBotService.toggleStatus("nonexistent", true))
                .isInstanceOf(ChatQuestionNotFoundException.class);
    }

    @Test
    void getNextQuestionByKey_whenNoMatchingChild_shouldReturnEmpty() {
        ChatBotQuestion parent = ChatBotQuestion.builder().id(1L).questionKey("help").build();
        ChatBotQuestion child = ChatBotQuestion.builder()
                .id(2L).parentId(1L).questionKey("order-status")
                .isActive(true).sortOrder(1).build();
        when(questionRepository.findByQuestionKey("help")).thenReturn(Optional.of(parent));
        when(questionRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(1L))
                .thenReturn(List.of(child));

        Optional<ChatBotQuestion> result = chatBotService.getNextQuestionByKey("help", "nonexistent-option");

        assertThat(result).isEmpty();
    }
}
