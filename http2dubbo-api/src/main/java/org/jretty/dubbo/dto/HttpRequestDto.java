package org.jretty.dubbo.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jretty.dubbo.util.LinkedCaseInsensitiveMap;

/**
 * @author zollty
 * @since 2021年4月19日
 */
public class HttpRequestDto implements Serializable {
    private static final long serialVersionUID = -7473981587114404214L;
    private Map<String, Object> attributes;
    private String characterEncoding;
    private String contentType;
    private String method;
    private Map<String, String> headers;
    private String requestURI;
    private String requestedSessionId;
    private String bodyString;
    private Map<String, String[]> parameters;

    public HttpRequestDto() {
        super();
    }
    
    public HttpRequestDto(Map<String, Object> attributes, String characterEncoding, String contentType,
            String method, Map<String, String> headers, String requestURI, String requestedSessionId, String bodyString,
            Map<String, String[]> parameters) {
        super();
        this.attributes = new LinkedHashMap<>();
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
        this.characterEncoding = characterEncoding;
        this.contentType = contentType;
        this.method = method;
        this.headers = new LinkedCaseInsensitiveMap<>();
        if (headers != null) {
            this.headers.putAll(headers);
        }
        this.requestURI = requestURI;
        this.requestedSessionId = requestedSessionId;
        this.bodyString = bodyString;
        this.parameters = new LinkedHashMap<>();
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
    }
    

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public String getContentType() {
        return contentType;
    }
    
    public String getMethod() {
        return method;
    }

    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getBodyString() {
        return bodyString;
    }

    /**
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the parameters
     */
    public Map<String, String[]> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param characterEncoding the characterEncoding to set
     */
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @param requestURI the requestURI to set
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * @param requestedSessionId the requestedSessionId to set
     */
    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    /**
     * @param bodyString the bodyString to set
     */
    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }
}
