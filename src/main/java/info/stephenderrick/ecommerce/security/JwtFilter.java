package info.stephenderrick.ecommerce.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private  final JwtProvider jwtProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = String.valueOf(request.getHeader("Authorization"));
        String jwtToken = "";
        String username = "";

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            jwtToken = bearerToken.substring(7);
            log.info(jwtToken);
            //extract username from token
            try{
                username = jwtProvider.extractUsername(jwtToken);
                log.info(username);
            }catch(Exception ex){
                log.info(ex.getMessage());
                throw new RuntimeException("Something went wrong");
            }

        }

        if(!username.isEmpty() && !jwtToken.isEmpty()){
            try{
                //Check if token is valid
                if(jwtProvider.validateToken(jwtToken, username)){
                    log.info("TOKEN - "+jwtToken);
                    log.info("USERNAME - "+username);
                    //extract authorities
                    Claims body = jwtProvider.extractAllClaims(jwtToken);
                    log.info("CLAIMS EXTRACTED SUCCESSFULLY - "+ body);
                    Collection<? extends GrantedAuthority> authorities = extractAuthorities(body);

                    //If token is valid set the security context
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Successfully added to the security context");
                }
            }catch(Exception ex){
                throw new ServletException("Check JWT Validation or Roles or Sec Context");
            }
        }

        log.info("REQ now to be passed to the next filter");
        filterChain.doFilter(request, response);

    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Claims body) {
        // Extract roles or authorities from custom claims, e.g., "roles" claim
        String roles = (String) body.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            if(roles.contains(",")){
                String[] roleArr = roles.split(",");
                for (String s : roleArr) {
                    authorities.add(new SimpleGrantedAuthority(s));
                }
            }else{
                authorities.add(new SimpleGrantedAuthority(roles));
            }

        }

        return authorities;
    }
}
