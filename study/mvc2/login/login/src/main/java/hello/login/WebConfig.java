package hello.login;

import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //@Bean
    public FilterRegistrationBean logFilter(){

        FilterRegistrationBean<Filter> filterFilterRegistrationBean =
                new FilterRegistrationBean<>();

        // 등록할 필터 지정
        filterFilterRegistrationBean.setFilter(new LogFilter());

        // 필터는 체인으로 동작한다. 따라서 순서 필요. 낮을수록 먼저 동작
        filterFilterRegistrationBean.setOrder(1);

        // 필터를 적용할 URL 패턴 지정.
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }

    // 로그인 체크 필터설정 추가 !
    //@Bean
    public FilterRegistrationBean loginCheckFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.setOrder(2); // 이번엔 2번! -- 로그 필터 다음에, 로그인 필터 적용됨
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    // 스프링 -- interceptor 설정 추가 !
        // 1. implements WebMvcConfigurer
        // 2. addInterceptors 메서드 오버라이드

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1) // -- 인터셉터 호출순서 지정
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");
        // addPathPatterns -- 인터셉터 적용할 URL 패턴
        // excludePathPatterns -- 인터셉터에서 제외할 패턴 (인증을 거칠 필요가 없는,)

        // 스프링 -- interceptor 추가22 -- 로그인체크
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/members/add", "/login", "/logout",
                        "/css/**", "/*.ico", "/error"
                );
        // excludePathPatterns로 LoginCheckFilter에서는 따로 메서드(isLoginCheckPath) 만들어야 했던 걸 훨씬 간단하게!
    }
}
