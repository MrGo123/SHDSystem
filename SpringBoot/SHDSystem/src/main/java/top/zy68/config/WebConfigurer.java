package top.zy68.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName WebConfigurer
 * @Description TODO
 * @Author Sustart
 * @Date2021/12/28 16:09
 * @Version 1.0
 **/

@ComponentScan
@Configuration
public class WebConfigurer extends WebMvcConfigurerAdapter {
    @Value("${file-path}")
    String filePath;
// 把本地文件目录作为服务器映射，展示图片
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("file:///" + filePath);
    }
}