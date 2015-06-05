package com.tamr.field.service;

import com.google.common.io.ByteSource;
import com.tamr.field.model.Job;
import com.tamr.field.model.MatchResults;
import com.tamr.field.model.RecordUpdate;
import com.tamr.field.model.ResponseWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by jellin on 5/14/15.
 */
@Component
public class RecordService extends AbstractTamrService {


    @Value("${tamr.user}")
    String tamrUser;

    @Value("${tamr.password}")
    String tamrPassword;

    @Value("${tamr.url}")
    String tamrUrl;

    public Job updateRecord(Map<String,String> recordUpdate, String sourceId, String recordId) {

        TamrRestClient<ResponseWrapper<Job>> tamrRestClient = new TamrRestClient<>();

        ParameterizedTypeReference tr = new ParameterizedTypeReference<ResponseWrapper<Job>>(){};

        Type t = tr.getType();

        String url =  tamrUrl+"/api/source/record?sourceId="+sourceId+"&recordId="+recordId;

        ResponseWrapper<Job>  response = tamrRestClient.doPost(t, url, HttpMethod.PUT, recordUpdate, tamrUser, tamrPassword);



        return response.get();
    }


    public Map getRecord(String sourceId, String recordId) {

        TamrRestClient<Map<String,String>> tamrRestClient = new TamrRestClient<>();

        ParameterizedTypeReference tr = new ParameterizedTypeReference<Map>(){};

        Type t = tr.getType();

        String url =  tamrUrl+"/api/source/record?sourceId="+sourceId+"&recordId="+recordId;

        Map  response = tamrRestClient.doPost(t, url, HttpMethod.GET, null, tamrUser, tamrPassword);


        return response;

    }


}
