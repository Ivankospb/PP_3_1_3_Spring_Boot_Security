package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.services.UserService;

@EnableWebSecurity //аннотация означает, что это Конфигурационный класс Spring Security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // Главный класс где мы настраиваем Spring Security (авторизацию, ...)
    private final SuccessUserHandler successUserHandler;
    private final UserService userService;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserService userService) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
    }

    @Override
    //Метод настраивает авторизацию
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN") //доступ к этой странице возможен только с ролью ADMIN
                .antMatchers("/user").hasRole("USER") //доступ к этой странице возможен только с ролью USER
                .anyRequest().hasAnyRole("USER", "ADMIN") // ко всем остальным страницам доступ получают ADMIN и USER
                .and()
                .formLogin().permitAll() // если мы не направляем на кастомную страницу логина и пароля, то Spring генерит стандартную
                .loginProcessingUrl("/authentication/login/check")
                .successHandler(new SuccessUserHandler())
                .and()
                .logout().logoutSuccessUrl("/login"); // если делаем логаут, то направляем в /login
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}