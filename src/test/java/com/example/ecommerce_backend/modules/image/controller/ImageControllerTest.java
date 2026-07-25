package com.example.ecommerce_backend.modules.image.controller;

import com.example.ecommerce_backend.core.aspect.AuthorizationAspect;
import com.example.ecommerce_backend.modules.image.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.image.service.ImageService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private AuthorizationAspect authorizationAspect;

    @BeforeEach
    void setUpAuth() throws Throwable {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ProceedingJoinPoint pjp = invocation.getArgument(0);
                try {
                    return pjp.proceed();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).when(authorizationAspect).checkPermission(any(), any());
    }

    @Test
    void addImage_shouldReturnCreated() throws Exception {
        ImageResponse response = ImageResponse.builder()
                .uuid("img-uuid").imageUrl("http://example.com/img.jpg")
                .isPrimary(true).sortOrder(1).build();
        when(imageService.addImage(eq("product-uuid"), any())).thenReturn(response);

        mockMvc.perform(post("/products/{productUuid}/images", "product-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageUrl\":\"http://example.com/img.jpg\",\"isPrimary\":true,\"sortOrder\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.uuid").value("img-uuid"))
                .andExpect(jsonPath("$.response.imageUrl").value("http://example.com/img.jpg"))
                .andExpect(jsonPath("$.message").value("Image added successfully"));
    }

    @Test
    void addImage_withoutUrl_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/products/{productUuid}/images", "product-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteImage_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/images/{imageUuid}", "img-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Image deleted successfully"));
    }
}
