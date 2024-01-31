package info.stephenderrick.ecommerce.security;

import info.stephenderrick.ecommerce.users.User;
import info.stephenderrick.ecommerce.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            Optional<User> userFromDBOpt = userRepository.findByEmail(username);
            User userFromDB = userFromDBOpt.get();

            return new org.springframework.security.core.userdetails.User(
                    userFromDB.getEmail(),
                    userFromDB.getPassword(),
                    userFromDB.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthoritiesFromRole(userFromDB.getRole().name())
            );
        }catch(Exception ex){
            throw new RuntimeException("Something went wrong "+ ex.getMessage());
        }
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFromRole(String name) {
        Role role = Role.valueOf(name);
        return role.getAuthorities();
    }
}

