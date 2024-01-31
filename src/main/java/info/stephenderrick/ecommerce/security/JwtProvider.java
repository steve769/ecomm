package info.stephenderrick.ecommerce.security;

import info.stephenderrick.ecommerce.users.User;
import info.stephenderrick.ecommerce.users.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtProvider {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    private final String JWT_SECRET = "655368566D5971337436773979244226452948404D635166546A576E5A723475";

    public String generateJwtToken(String username) {
        //My username is email
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = userDetailsService.loadUserByUsername(username).getAuthorities();
        User user = userRepository.findByEmail(username).orElse(null);
        StringBuilder authStringBuilder = new StringBuilder();

        for(GrantedAuthority auth: authorities){
            authStringBuilder.append(auth.getAuthority());
            authStringBuilder.append(",");
        }

        String authString = authStringBuilder.deleteCharAt(authStringBuilder.length() - 1).toString();
        String formattedAuthString = String.format("ROLE_%s", user.getRole().name());

        claims.put("roles", formattedAuthString);

        return createToken(claims, username);
    }

    public String createToken(Map<String, Object> claims, String username) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24*21))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String usernameStr) {
        String username = extractUsername(token);

        return (username.equals(usernameStr)) && isTokenExpired(token);
    }
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return new Date(System.currentTimeMillis()).before(extractExpirationDate(token));
    }

    public Date extractExpirationDate(String token) {

        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
