package com.tamr.field.matchdemo.controller;

import com.tamr.field.matchdemo.dao.PotentialMatchDao;
import com.tamr.field.service.RecordService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jellin on 5/15/15.
 */
@Controller
@RequestMapping("/match")
public class PendingMatchController {

    @Autowired
    PotentialMatchDao dao;

    @Autowired
    RecordService recordService;

    @Value("${matchsource}")
    private String matchsource;

    @Value("${masterSource}")
    private String masterSource;
    
    final static Logger logger = LoggerFactory.getLogger(PendingMatchController.class);


    @RequestMapping( value="/pendingmatch/{recordId}", method= RequestMethod.GET)
    public @ResponseBody Map getRecord(@PathVariable String recordId) {


        List<Map<String, String>> match = dao.getPotentialMatch(recordId);
        Map<String, String> matchRecord = flattenMap(match).get(0);
        String tamrId = matchRecord.get("tamrid");
        Map tamrRecord = recordService.getRecord(matchsource, tamrId);
        Map result = new HashMap();
        result.put("matchRecord", matchRecord);
        result.put("tamrRecord", tamrRecord);

        return result;
    }

    @RequestMapping( value="/pendingmatch/merge/{recordId}", method= RequestMethod.POST)
    public @ResponseBody String mergeRecord(@PathVariable String recordId, @RequestBody Map<String,String> body) {

        List<Map<String,String>> match = dao.getPotentialMatch(recordId);
        Map<String,String> matchRecord = flattenMap(match).get(0);
        String tamrid =  matchRecord.get("tamrid");

        logger.debug("merging record with tamrid {}", tamrid);

        //update from selections
        body.forEach((k,v)->{
            matchRecord.put(k,v);
        });
        logger.debug("updating record {} and deleting pending match {}",tamrid,recordId);
        recordService.updateRecord(matchRecord, masterSource,tamrid);
        dao.deletePotentialMatches(recordId);


        return "ok";
      }

    @RequestMapping( value="/pendingmatch/delete/{recordId}", method= RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteMatch(@PathVariable String recordId){
        logger.debug("discarding record with recordId {}", recordId);
        dao.deletePotentialMatches(recordId);
    }

    @RequestMapping( value="/pendingmatch/match/{recordId}", method= RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void createRecordFromMatch(@PathVariable String recordId){
        List<Map<String,String>> match = dao.getPotentialMatch(recordId);
        Map<String,String> matchRecord = flattenMap(match).get(0);
        String tamrid =  matchRecord.get("tamrid");
        logger.debug("updating record {} and deleting pending match {}",tamrid,recordId);
        recordService.updateRecord(matchRecord, masterSource,tamrid);
        dao.deletePotentialMatches(recordId);

    }

    @RequestMapping( value="/pendingmatch", method= RequestMethod.POST)
    public @ResponseBody
    Map getMatches() {
        Map m = new HashMap<>();
        List<Map<String,String>> results =   dao.getAllPotentialMatches();
        m.put("records",flattenMap(results));
        return m;
    }

    private List<Map> flattenMap(List<Map<String,String>> results){
        List out = new ArrayList();
        String prevEntityId = null;
        Map tmp = new HashMap();
        for(Map<String,String> m : results){

            String entityId = m.get("entity");
            if(entityId.equals(prevEntityId)){
                tmp.put(m.get("attribute"),m.get("value"));
                prevEntityId = entityId;
            }else{
                if(!tmp.isEmpty()){
                    out.add(tmp);
                }
                tmp = new HashMap();
                tmp.put(m.get("attribute"),m.get("value"));
                tmp.put("entity",m.get("entity"));
                tmp.put("tamrid",m.get("tamrid"));

                prevEntityId = entityId;
            }
        }
        if(out.isEmpty() && !tmp.isEmpty()){
            //only one record , we need to add it.
            out.add(tmp);
        }
        return out;
    }
}
