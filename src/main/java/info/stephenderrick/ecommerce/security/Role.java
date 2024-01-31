package info.stephenderrick.ecommerce.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public enum Role {
    USER(
            Set.of(
            new SimpleGrantedAuthority("READ_POSTINGS")
            )
                    ),
    POSTER(
            Set.of(
            new SimpleGrantedAuthority("READ_POSTINGS"),
                    new SimpleGrantedAuthority("READ_USERS_APPLIED"),
                    new SimpleGrantedAuthority("WRITE_POSTINGS")
            )
                    ),
    ADMIN(
            Set.of(
            new SimpleGrantedAuthority("READ_POSTINGS"),
                    new SimpleGrantedAuthority("WRITE_POSTINGS"),
                    new SimpleGrantedAuthority("READ_USERS_ALL"),
                    new SimpleGrantedAuthority("WRITE_USERS_ALL")
            )
                    );


    private Set<GrantedAuthority> authorities;

    Role(Set<GrantedAuthority> authorities){
        this.authorities = authorities;
    }

    public Set<GrantedAuthority> getAuthorities(){
        return authorities;
    }
}
