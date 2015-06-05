package com.tamr.field.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.io.ByteSource;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jellin on 5/16/15.
 */
public class TamrRestClient<T> {

    private File createTempQuerySource(List<? extends Map<String, String>> queryRecords) {
        final ObjectMapper mapper = generateDefaultObjectMapper();
        final Path tmp;
        try {
            tmp = Files.createTempFile("Tamr", "json");
            try (final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tmp.toFile())))) {
                for (final Map<String, String> query : queryRecords) {
                    writer.write(mapper.writeValueAsString(query));
                }
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return tmp.toFile();
    }


    private void addAuthentication(String username, String password, RestTemplate template) {
        if (username == null) {
            return;
        }
        List<ClientHttpRequestInterceptor> interceptors = Collections
                .<ClientHttpRequestInterceptor> singletonList(new BasicAuthorizationInterceptor(
                        username, password));
        template.setRequestFactory(new InterceptingClientHttpRequestFactory(template.getRequestFactory(),
                interceptors));
    }

    private static class BasicAuthorizationInterceptor implements
            ClientHttpRequestInterceptor {

        private final String username;

        private final String password;

        public BasicAuthorizationInterceptor(String username, String password) {
            this.username = username;
            this.password = (password == null ? "" : password);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
           //System.err.println(request.getMethod());
           //System.err.println(new String(body));

            byte[] token = org.apache.commons.codec.binary.Base64.encodeBase64((this.username + ":" + this.password).getBytes());
            request.getHeaders().add("Authorization", "Basic " + new String(token));
            return execution.execute(request, body);
        }

    }



    private   ObjectMapper generateDefaultObjectMapper() {
        return new ObjectMapper().registerModules(
                new GuavaModule(),
                new Jdk7Module(),
                new Jdk8Module()
        );
    }

    private RequestCallback getRequestCallback(ByteSource byteSource){
        final RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void doWithRequest(final ClientHttpRequest request) throws IOException {
                request.getHeaders().add("Content-type", "application/json");
                IOUtils.copy(byteSource.openBufferedStream(), request.getBody());
            }
        };
        return requestCallback;
    }
    private RequestCallback getRequestCallback(Map source){
        ObjectMapper mapper = this.generateDefaultObjectMapper();

        final RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void doWithRequest(final ClientHttpRequest request) throws IOException {
                request.getHeaders().add("Content-type", "application/json");

                IOUtils.write(mapper.writeValueAsBytes(source), request.getBody());
            }
        };
        return requestCallback;
    }

    private RestTemplate getRestTemplate(String tamrUser,String tamrPassword){

        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        HttpClient defaultHttpClient = new DefaultHttpClient(connectionManager);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(defaultHttpClient);


        RestTemplate restTemplate = new RestTemplate(factory);
        addAuthentication(tamrUser, tamrPassword, restTemplate);
        return restTemplate;
    }

    public T doPostStream(Type type, String url, HttpMethod method, List<? extends Map<String, String>> list, String username,String password){

        File file = createTempQuerySource(list);

        ByteSource byteSource = com.google.common.io.Files.asByteSource(file);

        RestTemplate restTemplate = getRestTemplate(username, password);

        ObjectMapper mapper = this.generateDefaultObjectMapper();

        ResponseExtractor<T> responseExtractor =
                new ResponseExtractor(){

                    @Override
                    public T extractData(ClientHttpResponse clientHttpResponse) throws IOException {
                        final JavaType javaType = mapper.constructType(type);
                        return  mapper.readValue(clientHttpResponse.getBody(),javaType);

                    }
                };

        T result = restTemplate.execute(url, method, getRequestCallback(byteSource), responseExtractor);

        file.delete();

        return result;
    }


    public T doPost(Type type, String url, HttpMethod method,  Map<String, String> data, String username,String password){


        RestTemplate restTemplate = getRestTemplate(username, password);

        ObjectMapper mapper = this.generateDefaultObjectMapper();

        ResponseExtractor<T> responseExtractor =
                new ResponseExtractor(){

                    @Override
                    public T extractData(ClientHttpResponse clientHttpResponse) throws IOException {
                        final JavaType javaType = mapper.constructType(type);
                        return  mapper.readValue(clientHttpResponse.getBody(),javaType);

                    }
                };

        T result = restTemplate.execute(url, method, getRequestCallback(data), responseExtractor);

        return result;
    }




}
