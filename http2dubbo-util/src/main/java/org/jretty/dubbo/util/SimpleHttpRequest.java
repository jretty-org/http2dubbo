package org.jretty.dubbo.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jretty.dubbo.dto.HttpRequestDto;
import org.jretty.util.Const;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StreamUtils;

/**
 * 
 * @author zollty
 * @since 2021年4月15日
 */
public class SimpleHttpRequest implements HttpRequest {
    private static final String CHARSET_PREFIX = "charset=";
    private Map<String, Object> attributes = new LinkedHashMap<>();
    private String characterEncoding;
    private String contentType;
    private String method;
    private Map<String, String> headers = new LinkedCaseInsensitiveMap<>();
    private String requestURI;
    private String requestedSessionId;
    private String bodyString;
    private Map<String, String[]> parameters = new LinkedHashMap<>(16);
    
    public HttpRequestDto toDto() {
        return new HttpRequestDto(attributes, characterEncoding, contentType, method, headers, requestURI,
                requestedSessionId, bodyString, parameters);
    }
    
    public SimpleHttpRequest() {
        super();
    }
    
    @SuppressWarnings("unchecked")
    public SimpleHttpRequest(HttpServletRequest req) {
        this.characterEncoding = req.getCharacterEncoding();
        this.contentType = req.getContentType();
        this.method = req.getMethod();
        
        // set this.headers
        Enumeration<String> ht = req.getHeaderNames();
        String tn;
        while(ht.hasMoreElements()) {
            tn = ht.nextElement();
            this.setHeader(tn, req.getHeader(tn));
        }
        
        // set attributes
        ht = req.getAttributeNames();
        while(ht.hasMoreElements()) {
            tn = ht.nextElement();
            if(tn.startsWith("app_")) {
                this.setAttribute(tn.substring(4), req.getAttribute(tn));
            }
        }
        
        this.requestURI = req.getRequestURI();
        this.requestedSessionId = req.getRequestedSessionId();
        
        if(RequestBodyTool.shouldParseBody(req)) {
            String enc = req.getCharacterEncoding();
            enc = (enc != null ? enc : Const.UTF_8);
            try {
                this.bodyString = StreamUtils.copyToString(req.getInputStream(), Charset.forName(enc));
            } catch (IOException e) {
                throw new IllegalStateException("can not get http body", e);
            }
        }
        
        this.parameters = new LinkedHashMap<>(req.getParameterMap());
    }
    
    public SimpleHttpRequest(HttpRequestDto dto) {
        this.attributes = dto.getAttributes();
        this.characterEncoding = dto.getCharacterEncoding();
        this.contentType = dto.getContentType();
        this.method = dto.getMethod();
        this.headers = dto.getHeaders();
        this.requestURI = dto.getRequestURI();
        this.requestedSessionId = dto.getRequestedSessionId();
        this.bodyString = dto.getBodyString();
        this.parameters = dto.getParameters();
    }
    
    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(new LinkedHashSet<>(this.attributes.keySet()));
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getParameter(String name) {
//        Assert.notNull(name, "Parameter name must not be null");
        String[] arr = this.parameters.get(name);
        return (arr != null && arr.length > 0 ? arr[0] : null);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(this.headers.keySet());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headers.keySet());
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getRequestedSessionId() {
        return requestedSessionId;
    }
    
    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public String getBodyString() {
        return bodyString;
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
        if (contentType != null) {
            try {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                if (mediaType.getCharset() != null) {
                    this.characterEncoding = mediaType.getCharset().name();
                }
            }
            catch (IllegalArgumentException ex) {
                // Try to get charset value anyway
                int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
                if (charsetIndex != -1) {
                    this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
                }
            }
            // updateContentTypeHeader();
        }
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
    
    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * Set a single value for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, they will be replaced.
     */
    public void setParameter(String name, String value) {
        setParameter(name, new String[] {value});
    }

    /**
     * Set an array of values for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, they will be replaced.
     */
    public void setParameter(String name, String... values) {
        Assert.notNull(name, "Parameter name must not be null");
        this.parameters.put(name, values);
    }
    
    public void setParameters(Map<String, ?> params) {
        Assert.notNull(params, "Parameter map must not be null");
        params.forEach((key, value) -> {
            if (value instanceof String) {
                setParameter(key, (String) value);
            }
            else if (value instanceof String[]) {
                setParameter(key, (String[]) value);
            }
            else {
                throw new IllegalArgumentException(
                        "Parameter map value must be single value " + " or array of type [" + String.class.getName() + "]");
            }
        });
    }
    
//    @Override
    public void setAttribute(String name, Object value) {
//        Assert.notNull(name, "Attribute name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        }
        else {
            this.attributes.remove(name);
        }
    }

//    @Override
    public void removeAttribute(String name) {
//        Assert.notNull(name, "Attribute name must not be null");
        this.attributes.remove(name);
    }
    
    /**
     * Clear all of this request's attributes.
     */
    public void clearAttributes() {
        this.attributes.clear();
    }
    
}
