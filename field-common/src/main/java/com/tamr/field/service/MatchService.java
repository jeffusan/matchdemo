package com.tamr.field.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.io.ByteSource;
import com.gs.collections.api.multimap.list.ListMultimap;
import com.tamr.field.model.MatchResults;
import com.tamr.field.model.ResponseWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.*;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.*;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jellin on 5/14/15.
 */
@Component
public class MatchService<T>  extends AbstractTamrService{

    @Value("${tamr.user}")
    String tamrUser;

    @Value("${tamr.password}")
    String tamrPassword;

    @Value("${tamr.url}")
    String tamrUrl;


    public MatchResults doMatch( Map<String, String> queryRecords,String matchSource, String idfield){

        TamrRestClient<ResponseWrapper<MatchResults>> tamrRestClient = new TamrRestClient<ResponseWrapper<MatchResults>>();



        String url =  tamrUrl+"/api/source/query?sourceId="+matchSource+"&idField="+idfield+"&returnMasterRecords=true";

        ResponseWrapper<MatchResults> result = tamrRestClient.doPost(getType(), url,HttpMethod.POST, queryRecords, tamrUser, tamrPassword);


        return result.get();

    }



    public MatchResults doMatches(List<? extends Map<String, String>> list,String matchSource, String idfield) throws Exception {

        TamrRestClient<ResponseWrapper<MatchResults>> tamrRestClient = new TamrRestClient<ResponseWrapper<MatchResults>>();

        String url =  tamrUrl+"/api/source/query?sourceId="+matchSource+"&idField="+idfield+"&returnMasterRecords=true";

        ResponseWrapper<MatchResults> result = tamrRestClient.doPostStream(getType(), url, HttpMethod.POST, list, tamrUser, tamrPassword);

        return result.get();

    }


    public Type getType() {
        ParameterizedTypeReference tr = new ParameterizedTypeReference<ResponseWrapper<MatchResults>>(){};

        Type t = tr.getType();
    return t;
    }
}
