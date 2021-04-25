package org.jretty.dubbo.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author zollty
 * @since 2021年4月25日
 */
class PrivateJson {
    private static JacksonCustomObjectMapper objectMapper = new JacksonCustomObjectMapper(true);
    /** 
     * 对象转换成JSON字符串，默认Long、Double等类型会转换成字符串类型，这是推荐的做法。
     */
    public static String toJSONString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    /** 
     * JSON字符串转换成对象，支持前面两个方法的逆转换
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
