package com.likelion.finalproject.config;

import com.likelion.finalproject.exception.ErrorCode;
import com.likelion.finalproject.exception.SNSAppException;
import com.likelion.finalproject.service.UserService;
import com.likelion.finalproject.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
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
    public static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // http header에 있는 AUTHORIZATION 정보를 받아옵니다.
        final String token = getToken(request);
        log.info("token : {}", token);

        // AUTHORIZATION에 있는 Token을 꺼냅니다.
        try {
            String userName = JwtUtil.getUserName(token, secretKey);
            log.info("userName = {}", userName);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            request.setAttribute("exception", ErrorCode.INVALID_PERMISSION.name());
        } catch (Exception e) {
            log.error("JwtFilter - doFilterInternal() 오류발생");
            log.error("token : {}", token);
            log.error("Exception Message : {}", e.getMessage());
            log.error("Exception StackTrace : {");
            e.printStackTrace();
            log.error("}");
            request.setAttribute("exception", ErrorCode.UNKNOWN_ERROR.name());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        return null;
    }
}