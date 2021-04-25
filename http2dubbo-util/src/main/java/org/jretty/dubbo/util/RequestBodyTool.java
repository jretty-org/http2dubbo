package org.jretty.dubbo.util;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * 
 * @author zollty
 * @since 2021年4月25日
 */
class RequestBodyTool {
    // 除POST之外，其他三种属于Spring的特殊扩展。
    // private static final List<String> FORM_METHODS = Arrays.asList("POST", "PUT", "PATCH", "DELETE");
    // 暂不支持DELETE、GET传递Body数据，根据 RFC7231规范（https://tools.ietf.org/html/rfc7231）的建议。
    // 其他几个特殊用途的方法，当然也不需要解析Body，包括：HEAD  OPTIONS CONNECT TRACE
    // 剩下的常用就只有： POST PUT，不常用的方法：PATCH, LINK, UNLINK （参见rfc2616），目前支持 PATCH
    private static final List<String> INCLUDE_METHODS = Arrays.asList("POST", "PUT", "PATCH");
    public static final String BODY_NO_PARSE_FLAG = "[Not Support Parsing]";

    public static boolean shouldParseBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        String method = request.getMethod();
        if (StringUtils.hasLength(contentType) && INCLUDE_METHODS.contains(method)) {
            try {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                return MediaType.APPLICATION_JSON.includes(mediaType)
                        || MediaType.APPLICATION_XML.includes(mediaType)
                        || MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
            } catch (IllegalArgumentException ex) {
            }
        }
        return false;
    }
}
