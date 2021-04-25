package org.jretty.dubbo.util;

import java.util.Enumeration;
import java.util.Map;

/**
 * 
 * @author zollty
 * @since 2021年4月14日
 */
public interface HttpRequest {

    /**
     * Returns the value of the named attribute as an <code>Object</code>, or
     * <code>null</code> if no attribute of the given name exists.
     *
     * @param name
     *            a <code>String</code> specifying the name of the attribute
     * @return an <code>Object</code> containing the value of the attribute, or
     *         <code>null</code> if the attribute does not exist
     */
    public Object getAttribute(String name);

    /**
     * Returns an <code>Enumeration</code> containing the names of the
     * attributes available to this request. This method returns an empty
     * <code>Enumeration</code> if the request has no attributes available to
     * it.
     *
     * @return an <code>Enumeration</code> of strings containing the names of the
     *         request's attributes
     */
    public Enumeration<String> getAttributeNames();

    /**
     * Returns the name of the character encoding used in the body of this
     * request. This method returns <code>null</code> if the no character
     * encoding has been specified. The following priority order is used to
     * determine the specified encoding:
     * <ol>
     * <li>per request</li>
     * <li>web application default via the deployment descriptor or
     *     {@link ServletContext#setRequestCharacterEncoding(String)}</li>
     * <li>container default via container specific configuration</li>
     * </ol>
     *
     * @return a <code>String</code> containing the name of the character
     *         encoding, or <code>null</code> if the request does not specify a
     *         character encoding
     */
    public String getCharacterEncoding();

    /**
     * Returns the MIME type of the body of the request, or <code>null</code> if
     * the type is not known. For HTTP servlets, same as the value of the CGI
     * variable CONTENT_TYPE.
     *
     * @return a <code>String</code> containing the name of the MIME type of the
     *         request, or null if the type is not known
     */
    public String getContentType();

    /**
     * Returns the value of a request parameter as a <code>String</code>, or
     * <code>null</code> if the parameter does not exist. Request parameters are
     * extra information sent with the request. For HTTP servlets, parameters
     * are contained in the query string or posted form data.
     * <p>
     * You should only use this method when you are sure the parameter has only
     * one value. If the parameter might have more than one value, use
     * {@link #getParameterValues}.
     * <p>
     * If you use this method with a multivalued parameter, the value returned
     * is equal to the first value in the array returned by
     * <code>getParameterValues</code>.
     * <p>
     * If the parameter data was sent in the request body, such as occurs with
     * an HTTP POST request, then reading the body directly via
     * {@link #getInputStream} or {@link #getReader} can interfere with the
     * execution of this method.
     *
     * @param name
     *            a <code>String</code> specifying the name of the parameter
     * @return a <code>String</code> representing the single value of the
     *         parameter
     * @see #getParameterValues
     */
    public String getParameter(String name);

    /**
     * Returns an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of the parameters contained in this request. If the
     * request has no parameters, the method returns an empty
     * <code>Enumeration</code>.
     *
     * @return an <code>Enumeration</code> of <code>String</code> objects, each
     *         <code>String</code> containing the name of a request parameter;
     *         or an empty <code>Enumeration</code> if the request has no
     *         parameters
     */
    public Enumeration<String> getParameterNames();

    /**
     * Returns an array of <code>String</code> objects containing all of the
     * values the given request parameter has, or <code>null</code> if the
     * parameter does not exist.
     * <p>
     * If the parameter has a single value, the array has a length of 1.
     *
     * @param name
     *            a <code>String</code> containing the name of the parameter
     *            whose value is requested
     * @return an array of <code>String</code> objects containing the parameter's
     *         values
     * @see #getParameter
     */
    public String[] getParameterValues(String name);

    /**
     * Returns a java.util.Map of the parameters of this request. Request
     * parameters are extra information sent with the request. For HTTP
     * servlets, parameters are contained in the query string or posted form
     * data.
     *
     * @return an immutable java.util.Map containing parameter names as keys and
     *         parameter values as map values. The keys in the parameter map are
     *         of type String. The values in the parameter map are of type
     *         String array.
     */
    public Map<String, String[]> getParameterMap();
    
    
    /**
     * Returns the value of the specified request header as a
     * <code>String</code>. If the request did not include a header of the
     * specified name, this method returns <code>null</code>. If there are
     * multiple headers with the same name, this method returns the first head
     * in the request. The header name is case insensitive. You can use this
     * method with any request header.
     *
     * @param name
     *            a <code>String</code> specifying the header name
     * @return a <code>String</code> containing the value of the requested
     *         header, or <code>null</code> if the request does not have a
     *         header of that name
     */
    public String getHeader(String name);
    
    /**
     * Returns all the values of the specified request header as an
     * <code>Enumeration</code> of <code>String</code> objects.
     * <p>
     * Some headers, such as <code>Accept-Language</code> can be sent by clients
     * as several headers each with a different value rather than sending the
     * header as a comma separated list.
     * <p>
     * If the request did not include any headers of the specified name, this
     * method returns an empty <code>Enumeration</code>. The header name is case
     * insensitive. You can use this method with any request header.
     *
     * @param name
     *            a <code>String</code> specifying the header name
     * @return an <code>Enumeration</code> containing the values of the requested
     *         header. If the request does not have any headers of that name
     *         return an empty enumeration. If the container does not allow
     *         access to header information, return null
     */
    public Enumeration<String> getHeaders(String name);

    
    /**
     * Returns an enumeration of all the header names this request contains. If
     * the request has no headers, this method returns an empty enumeration.
     * <p>
     * Some servlet containers do not allow servlets to access headers using
     * this method, in which case this method returns <code>null</code>
     *
     * @return an enumeration of all the header names sent with this request; if
     *         the request has no headers, an empty enumeration; if the servlet
     *         container does not allow servlets to use this method,
     *         <code>null</code>
     */
    public Enumeration<String> getHeaderNames();
    
    /**
     * Returns the name of the HTTP method with which this request was made, for
     * example, GET, POST, or PUT. Same as the value of the CGI variable
     * REQUEST_METHOD.
     *
     * @return a <code>String</code> specifying the name of the method with
     *         which this request was made
     */
    public String getMethod();
    
    /**
     * Returns the session ID specified by the client. This may not be the same
     * as the ID of the current valid session for this request. If the client
     * did not specify a session ID, this method returns <code>null</code>.
     *
     * @return a <code>String</code> specifying the session ID, or
     *         <code>null</code> if the request did not specify a session ID
     * @see #isRequestedSessionIdValid
     */
    public String getRequestedSessionId();

    /**
     * Returns the part of this request's URL from the protocol name up to the
     * query string in the first line of the HTTP request. The web container
     * does not decode this String. For example:
     * <table>
     * <caption>Examples of Returned Values</caption>
     * <tr>
     * <th>First line of HTTP request</th>
     * <th>Returned Value</th>
     * <tr>
     * <td>POST /some/path.html HTTP/1.1
     * <td>
     * <td>/some/path.html
     * <tr>
     * <td>GET http://foo.bar/a.html HTTP/1.0
     * <td>
     * <td>/a.html
     * <tr>
     * <td>HEAD /xyz?a=b HTTP/1.1
     * <td>
     * <td>/xyz
     * </table>
     * <p>
     * To reconstruct a URL with a scheme and host, use
     * {@link #getRequestURL}.
     *
     * @return a <code>String</code> containing the part of the URL from the
     *         protocol name up to the query string
     * @see #getRequestURL
     */
    public String getRequestURI();
    
    
    /**
     * get http body from ServletInputStream
     * @return http body string
     */
    public String getBodyString();
    
}
