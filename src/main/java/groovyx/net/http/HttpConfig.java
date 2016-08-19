/**
 * Copyright (C) 2016 David Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovyx.net.http;

import groovy.lang.Closure;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Provides the public interface used for configuring the HTTP Builder NG client.
 */
public interface HttpConfig {

    /**
     * Defines the allowed values of the response status.
     */
    enum Status { SUCCESS, FAILURE };

    /**
     * Defines the allowed values of the HTTP authentication type.
     */
    enum AuthType { BASIC, DIGEST };

    /**
     *  Defines the accessible HTTP authentication information.
     */
    interface Auth {

        /**
         * Retrieve the authentication type for the request.
         *
         * @return the AuthType for the Request
         */
        AuthType getAuthType();

        /**
         * Retrieve the user information from the request.
         *
         * @return the user information
         */
        String getUser();

        /**
         * Retrieve the password information from the request.
         *
         * @return the password information
         */
        String getPassword();

        default void basic(String user, String password) {
            basic(user, password, false);
        }
        
        void basic(String user, String password, boolean preemptive);

        default void digest(String user, String password) {
            digest(user, password, false);
        }
        
        void digest(String user, String password, boolean preemptive);
    }

    /**
     * Defines the accessible HTTP request information.
     */
    interface Request {
        Auth getAuth();
        void setContentType(String val);
        void setCharset(String val);
        void setCharset(Charset val);
        
        UriBuilder getUri();
        void setUri(String val) throws URISyntaxException;
        void setUri(URI val);
        void setUri(URL val) throws URISyntaxException;

        Map<String,String> getHeaders();
        void setHeaders(Map<String,String> toAdd);

        void setAccept(String[] values);
        void setAccept(List<String> values);
        void setBody(Object val);

        default void cookie(String name, String value) {
            cookie(name, value, null);
        }
        
        void cookie(String name, String value, Date expires);

        void encoder(String contentType, BiConsumer<ChainedHttpConfig,ToServer> val);
        void encoder(List<String> contentTypes, BiConsumer<ChainedHttpConfig,ToServer> val);
        BiConsumer<ChainedHttpConfig,ToServer> encoder(String contentType);
    }

    /**
     * Defines the accessible HTTP response information.
     */
    interface Response {
        void when(Status status, Closure<Object> closure);
        void when(Integer code, Closure<Object> closure);
        void when(String code, Closure<Object> closure);
        Closure<Object> when(Integer code);

        void success(Closure<Object> closure);
        void failure(Closure<Object> closure);

        void parser(String contentType, BiFunction<ChainedHttpConfig,FromServer,Object> val);
        void parser(List<String> contentTypes, BiFunction<ChainedHttpConfig,FromServer,Object> val);
        BiFunction<ChainedHttpConfig,FromServer,Object> parser(String contentType);
    }

    void context(String contentType, Object id, Object obj);
    
    default void context(final List<String> contentTypes, final Object id, final Object obj) {
        for(String contentType : contentTypes) {
            context(contentType, id, obj);
        }
    }

    /**
     * Used to retrieve information about the HTTP request.
     *
     * @return the HTTP request
     */
    Request getRequest();

    /**
     * Used to retrieve information about the HTTP response.
     *
     * @return the HTTP response
     */
    Response getResponse();
}
