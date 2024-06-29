package hello.exception;

import hello.exception.filter.LogFilter;
import hello.exception.interceptor.LogInterceptor;
import hello.exception.resolver.MyHandlerExceptionResolver;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**", "/*.ico",
                        "/error", "/error-page/**" // 오류페이지 경로
                );
        // 인터셉터는 신이다. 필터보다 훨씬 간단하군..
        // 그저 인터셉터를 적용하고 싶지 않다면, 그냥 제외 url에 추가하면 된다..
    }

    //@Bean
    public FilterRegistrationBean logFilter(){
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();

        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");

        // 이렇게 두가지를 모두 넣으면, 클라이언트 요청은 물론이고, 오류페이지 요청에서도 필터가 호출된다!!
        // 아무것도 넣지 않으면, 기본값이 DispatcherType.REQUEST이다.
        // 즉, 클라이언트 요청이 있는 경우에만, 필터가 적용된다.

        // 특별히 오류페이지 경로도 필터로 적용할 것이 아니라면, 기본값 그대로 사용해도 됨.
        filterFilterRegistrationBean.setDispatcherTypes
                (DispatcherType.REQUEST
                 /*, DispatcherType.ERROR*/);
            // 만약, 디폴트값으로 한다면, 에러 발생 시, 로그필터에서 REQUEST만 필터링 해주고, 에러는 안해주는 것 확인 가능..
        // 물론, 오류페이지 요청 전용 필터를 적용하고 싶다면, DispatcherType.ERROR도 지정해주면 됨.

        return filterFilterRegistrationBean;
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new MyHandlerExceptionResolver());
    }
}
