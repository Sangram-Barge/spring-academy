package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .requestMatchers("/cashcards/**")
        .hasRole("CARD-OWNER")
        .and()
        .csrf().disable()
        .httpBasic();
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
    User.UserBuilder user = User.builder();
    UserDetails sangram = user.username("sangram")
        .password(passwordEncoder.encode("abc123"))
        .roles("CARD-OWNER")
        .build();
    UserDetails unAuthUser = user.username("test")
        .password(passwordEncoder.encode("test"))
        .roles("NON-CARD-OWNER")
        .build();
    UserDetails anushree = user.username("anushree")
        .password(passwordEncoder.encode("anu"))
        .roles("CARD-OWNER")
        .build();
    return new InMemoryUserDetailsManager(sangram, anushree, unAuthUser);
  }
}
