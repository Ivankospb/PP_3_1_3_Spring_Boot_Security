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
    //Метод настраивает аутентификацию
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN") //доступ к этой странице возможен только с ролью ADMIN
                .antMatchers("/", "/index/**", "/error/**").permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN")
                .and()
                .formLogin() // если мы не направляем на кастомную страницу логина и пароля, то Spring генерит стандартную
//                .loginPage("/login") //внес допом
                .loginProcessingUrl("/authentication/login/check")
                .successHandler(new SuccessUserHandler())
//                .defaultSuccessUrl("/user") //внес допом
                .and()
                .logout().logoutSuccessUrl("/"); // если делаем логаут, то направляем в корень

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}