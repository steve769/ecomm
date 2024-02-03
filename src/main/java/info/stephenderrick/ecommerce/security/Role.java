package info.stephenderrick.ecommerce.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public enum Role {
    USER(
            Set.of(
            new SimpleGrantedAuthority("READ_POSTINGS")
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
