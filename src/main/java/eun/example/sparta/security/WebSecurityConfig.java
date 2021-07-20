package eun.example.sparta.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.httpBasic();

        http.authorizeRequests()
                .antMatchers("/images/**").permitAll() // image 폴더를 login 없이 허용
                .antMatchers("/css/**").permitAll() // css 폴더를 login 없이 허용
                .antMatchers("/user/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated() // 그 외 모든 요청은 인증과정 필요
                .and()
                .formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

}
