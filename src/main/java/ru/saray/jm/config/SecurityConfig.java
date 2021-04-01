package ru.saray.jm.config;

/*
Задание:
1. Перенесите классы и зависимости из примера в свое MVC приложение из предыдущей задачи.
2. Создайте класс Role и свяжите User с ролями так, чтобы юзер мог иметь несколько ролей.
3. Имплементируйте модели Role и User интерфейсами GrantedAuthority и UserDetails соответственно.
 Измените настройку секьюрности с inMemory на userDetailService.
4. Все CRUD-операции и страницы для них должны быть доступны только пользователю с ролью admin по url: /admin/.
5. Пользователь с ролью user должен иметь доступ только к своей домашней странице /user, где выводятся его данные.
Доступ к этой странице должен быть только у пользователей с ролью user и admin. Не забывайте про несколько ролей у пользователя!
6. Настройте logout с любой страницы с использованием возможностей thymeleaf.
7. Настройте LoginSuccessHandler так, чтобы админа после логина направляло на страницу /admin, а юзера на его страницу.
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.saray.jm.config.handler.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("ru.saray.jm")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final LoginSuccessHandler successHandler;

    @Autowired
    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                          @Qualifier("successHandler") LoginSuccessHandler loginSuccessHandler
    ) {
        this.userDetailsService = userDetailsService;
        this.successHandler = loginSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                // cross site request forgery - межсайтовая подделка запроса
                // better to able this when our app is working with real clients/users
                .authorizeRequests()
                // order of antMatchers DOES matter !
                .antMatchers("/").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN") // access by role
                .antMatchers("/user").hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // customized login form
                .permitAll()
                .successHandler(successHandler)
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // we can specify the strength of encoding process
    }
}