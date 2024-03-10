package hms.cpaas.sampleapp.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Autowired
    fun configureGlobal(
        auth: AuthenticationManagerBuilder,
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.ignoringRequestMatchers("sdk/*") }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/css/**", "/js/**", "/signup", "/assets/**", "sdk/*", "/sdk").permitAll()
                    .anyRequest().authenticated()
            }.formLogin { formLogin ->
                formLogin
                    .loginPage("/login")
                    .failureHandler { request, response, exception ->
                        var errorMessage = exception.localizedMessage + " or user not active!"
                        request.session.setAttribute("loginError", errorMessage)
                        val contextPath = request.contextPath
                        response.sendRedirect("$contextPath/login?error=true")
                    }
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .deleteCookies("JSESSIONID").permitAll()
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .permitAll()
            }
            .rememberMe { rememberMe ->
                rememberMe
                    .key("uniqueAndSecret")
                    .tokenValiditySeconds(86400)
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .accessDeniedPage("/access-denied")
            }
        return http.build()
    }

}