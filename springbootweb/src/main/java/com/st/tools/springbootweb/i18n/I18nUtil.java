package com.st.tools.springbootweb.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * <li>浏览器语言动态设置Locale区域: ref <a href="https://www.cnblogs.com/lori/p/17415892.html" >refs</a></li>
 * 国际化消息配置 LocalMessageConfig中配置了resources/i18n/messages_zh_CN.properties.
 * 通过 LocaleContextHolder.getLocale()获取客户端浏览器的语言环境，就是请求头中的Accept-Language的值，再根据它进行国际化消息的获取。
 * <li>国际化文件在resources/i18n目录，文件名是message_{语言}.properties</li>
 */
@Component
@RequiredArgsConstructor
public class I18nUtil {

    private final MessageSource messageSource;


    /**
     * 通过code 获取错误信息
     * @param code
     * @return
     */
    public String getMessage(String code) {
        return getMessage(code, null);
    }



    /**
     * 通过code 和参数获取错误信息
     * @param code
     * @return
     */
    public String getMessage(String code, Object[] args) {
        Locale locale = getCurrentLocale();
        return messageSource.getMessage(code, args, locale);
    }

    /**
     *  国际化消息配置 LocalMessageConfig中配置了resources/i18n/messages_zh_CN.properties.
     *  通过 LocaleContextHolder.getLocale()获取客户端浏览器的语言环境，就是请求头中的Accept-Language的值，再根据它进行国际化消息的获取。
     *
     * @return
     */
    public Locale getCurrentLocale() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getLocale(); // 根据请求头 Accept-Language 获取
        }

        //Java中的Locale.getDefault()获取的是操作系统的默认区域设置，如果需要获取客户端浏览器的区域设置，可以从HTTP头中获取"Accept-Language"的值来进行解析
        return Locale.getDefault();
    }
}
