package blog.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // 用于添加跨源资源共享（CORS）的映射
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有的请求路径（  /**  ）应用 CORS 配置。
                .allowedOrigins("*") // 许所有来源（  *  ）的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"); // 允许上述 HTTP 方法。
    }

}
