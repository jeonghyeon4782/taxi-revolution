package com.wjd4782.taxiprojectrestapi.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Request Header 에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 로그인 및 회원가입, 택시 승강장 요청은 토큰 검증 skip
        if (isLoginOrSignUpRequest((HttpServletRequest) request)) {
            chain.doFilter(request, response);
            return;
        }

        // validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else { // 만료되거나 오류 발생할 경우
            sendErrorResponse((HttpServletResponse) response);
            return;
        }
        chain.doFilter(request, response);
    }

    // 에러 응답
    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // 검증 오류 코드 423
        ResponseDto<String> errorResponse = new ResponseDto<>(HttpStatus.LOCKED.value(), "토큰 검증 실패", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }

    // 예외 처리를 수행할 URL 패턴을 확인하는 메서드
    private boolean isLoginOrSignUpRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        boolean isGetRequestForApiPost = requestMethod.equalsIgnoreCase("GET") && requestURI.equals("/api/post");
        return requestURI.contains("/api/auth/login")
                || requestURI.contains("/api/auth/register")
                || requestURI.contains("/api/taxiStand")
                || requestURI.matches(".*/api/auth/check-memberId/[^/]+")
                || requestURI.matches(".*/api/auth/check-nickname/[^/]+")
                || isGetRequestForApiPost;
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
