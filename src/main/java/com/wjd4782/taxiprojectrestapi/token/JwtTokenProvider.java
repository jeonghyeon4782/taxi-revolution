package com.wjd4782.taxiprojectrestapi.token;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.repository.MemberRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 비밀키
    @Value("${jwt.secretKey}")
    private String secretKey;

    // 토큰 시간
    private long tokenValidTime = 1000L * 60 * 30; // 30분

    private final MemberRepository memberRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 생성
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰으로 인증 객체 얻기
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        UserDetails userDetails = new User(member.getUsername(), member.getPassword(), authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    // 토큰으로 유저 아이디 찾기
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    // 토큰을 사용하기 위해 실제로 Header에서 꺼내오는 메소드
    public String resolveToken(HttpServletRequest req) {
        return req.getHeader("X-AUTH-TOKEN");
    }

    // 토큰이 만료되었는 지를 확인
    public boolean validateTokenExceptExpiration(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch(Exception e) {
            return false;
        }
    }
}
