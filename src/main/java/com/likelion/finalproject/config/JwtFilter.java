package com.likelion.finalproject.config;

import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.service.UserService;
import com.likelion.finalproject.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // http header에 있는 AUTHORIZATION 정보를 받아옵니다.
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);


        // header에 있는 AUTHORIZATION이 없거나, AUTHORIZATION이 "Bearer"로 시작하지 않으면 block처리합니다.
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("authorization이 없습니다");
            filterChain.doFilter(request, response);
            return;
        }

        // AUTHORIZATION에 있는 Token을 꺼냅니다.
        String token;
        try {
            token = authorization.split(" ")[1];

            // Token의 만료 여부를 확인합니다.
            if (JwtUtil.isExpired(token, secretKey)) {
                log.info("Token이 만료 되었습니다.");
                filterChain.doFilter(request, response);
            }

            String userName = JwtUtil.getUserName(token, secretKey);
            log.info("userName = {}", userName);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
