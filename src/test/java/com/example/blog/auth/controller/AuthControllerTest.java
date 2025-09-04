package com.example.blog.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.blog.auth.dto.RegisterRequestDTO;
import com.example.blog.auth.service.AuthService;
import com.example.blog.domain.member.dto.MemberResponseDto;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.domain.refresh_token.RefreshTokenRepository;
import com.example.blog.mapper.MemberMapper;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerStandaloneTest {

  @Mock AuthService authService;
  @Mock RefreshTokenRepository refreshTokenRepository;
  @Mock MemberMapper memberMapper;

  @InjectMocks AuthController authController;

  MockMvc mockMvc;

  @BeforeEach
  void 초기화() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  @DisplayName("회원가입 성공 시 200")
  void 회원가입_성공시_200() throws Exception {
    MemberResponseDto dummy = new MemberResponseDto(
        UUID.randomUUID(), Instant.now(), null, "email@test.com", "nick", null, MemberStatus.ACTIVE, null, "name", MemberRole.USER
    );
    given(authService.register(any(), any(RegisterRequestDTO.class))).willReturn(dummy);

    String body = """
      {"email":"email@test.com","password":"pw","nickname":"nick","name":"name"}
      """;

    mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk());

    verify(authService).register(any(), any(RegisterRequestDTO.class));
  }

  @Test
  @DisplayName("refresh 성공 시 200과 토큰 반환")
  void refresh_성공시_200() throws Exception {
    given(authService.refresh(any(), any())).willReturn(Map.of("accessToken","acc","refreshToken","ref"));

    mockMvc.perform(post("/api/auth/refresh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("acc"))
        .andExpect(jsonPath("$.refreshToken").value("ref"));

    verify(authService).refresh(any(), any());
  }

  @Test
  @DisplayName("refresh 실패 시 401")
  void refresh_실패시_401() throws Exception {
    given(authService.refresh(any(), any())).willThrow(new RuntimeException("boom"));
    mockMvc.perform(post("/api/auth/refresh")).andExpect(status().isUnauthorized());
  }
}
