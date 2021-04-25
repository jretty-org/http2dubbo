package org.jretty.dubbo.api;

import org.jretty.apibase.Request;
import org.jretty.apibase.Result;
import org.jretty.dubbo.dto.HttpRequestDto;

/**
 * 通用API：HTTP请求转Dubbo调用
 * @author zollty
 * @since 2021年4月19日
 */
public interface Http2DubboService {

    /**
     * http数据通过dubbo传输，返回json字符串
     */
    Result<String> route(Request<HttpRequestDto> request);

}
