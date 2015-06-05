package com.tamr.field.matchdemo.batch;


import com.tamr.field.matchdemo.dao.PotentialMatchDao;
import com.tamr.field.model.EntityDetails;
import com.tamr.field.model.MatchResults;
import com.tamr.field.model.Record;
import com.tamr.field.model.RecordPair;
import com.tamr.field.service.MatchService;
import com.tamr.field.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Batch Writer,  The writer processes the each chunk
 *
 * 1. Sends Match Query to Tamr.
 * 2. All distinct Matches are added as new Records to Tamr
 * 3. All matches are compared on a field by field basis if the fields
 *    match the record is thrown away.  If any of the fields differ the
 *    record is added to the db for review
 *
 * For simplicity sake a match is any record where a basic string compare
 * to each field is equal.  Any field that contains multiple values only
 * the first field is used.
 *
 */
@Component
public class MatchWriter implements ItemWriter<Map<String, String>> {

    final static Logger logger = LoggerFactory.getLogger(MatchWriter.class);

    public static final String UUID = "uuid";
    @Autowired
    MatchService matchService;

    @Autowired
    RecordService recordService;

    @Autowired
    PotentialMatchDao potentialMatchDao;

    @Value("#{'${ignoreAttributes}'.split(',')}")
    List ignoreAttributes;

    @Value("#{'${inputfieldnames}'.split(',')}")
    String[] fieldNames;

    @Value("${matchsource}")
    private String matchsource;

    @Value("${matchsource}")
    private String masterSource;
    @Override
    public void write(List<? extends Map<String, String>> list) throws Exception {

        logger.debug("Begining Write Processing of {} records", list.size());

        MatchResults matches = matchService.doMatches(list, matchsource, UUID);

        Map<String, Map> mapRequestsByUUID = new HashMap();

        for (Map<String, String> lItem : list) {
            mapRequestsByUUID.put(lItem.get(UUID), lItem);
        }

        handleMatches(matches.matches, matches.masterRecords, mapRequestsByUUID);


        List recordsToCreate = new ArrayList();

        for (Record record : matches.distinct) {
            recordsToCreate.add(mapRequestsByUUID.get(record.getName()));
        }

        createRecords(recordsToCreate);

        logger.info("writer finished");

    }

    private void handleMatches(List<RecordPair> matchList, List<EntityDetails> masterList, Map<String, Map> mapByDuplicateId) {

        Map<String, String> matchPairs = new HashMap();

        //create a map of pairs,  <Tamr>,<Duplicate>
        for (RecordPair recordPair : matchList) {
            String tamrId   = recordPair.master.getName();
            //we only care about matches from the desired source.
            if(matchsource.equals(recordPair.master.getSource().getName())){
                String duplicateId = recordPair.duplicate.getName();
                matchPairs.putIfAbsent( tamrId,duplicateId);
            }

        }

        //look for the correct record for the duplicate
        for (EntityDetails entity : masterList) {
            String tamrId = entity.href.getName();
            logger.debug("masterid of masterlist{}",tamrId);
            boolean recordsEqual = recordsEqual(mapByDuplicateId.get(matchPairs.get(tamrId)), entity.values);
            if (!recordsEqual) {
                writeRecord(tamrId, mapByDuplicateId.get(matchPairs.get(tamrId)));
            }
        }

    }

    /**
     * Write Record to the database
     * @param id
     * @param v
     */
    private void writeRecord(String id, Map<String, String> v) {

        logger.debug("Writing record to database: {} is not a duplicate of {}",id ,v.get(UUID));
        potentialMatchDao.insert(v, id);

    }

    /**
     * Do a field by field comparison of a potentialMatch and the record
     * from Tamr.  Exclude configured fields
     * @param potentialMatch
     * @param master
     * @return
     */
    protected boolean recordsEqual(Map potentialMatch, Map master) {

        boolean match = true;

        for(String field :fieldNames){

            if(!ignoreAttributes.contains(field)) {

                Object val1 = potentialMatch.get(field);
                Object val2 = master.get(field);
                if (val1 instanceof List) {
                    val1 = ((List) val1).get(0);
                }
                if (val2 instanceof List) {
                    val2 = ((List) val2).get(0);
                }


               match = checkEquals(val1,val2);

              logger.debug("Comparing {} to {} :{}", val1, val2,match);


            }

            if(match ==false){
                break;
            }
        }

        logger.debug("match results = {} for {}", match, potentialMatch.get("uuid"));
        return match;

    }

    /**
     * Create a new record in Tamr
     * @param distinct
     */
    private void createRecords(List<Map<String, String>> distinct) {

        logger.debug("creating new records in {}", masterSource);

        for (Map<String, String> m : distinct) {
            recordService.updateRecord(m, masterSource, m.get(UUID));
        }

    }

    private boolean checkEquals(Object a, Object b){

        boolean isStr0Empty = (a == null || a.equals(""));
        boolean isStr1Empty = (b == null || b.equals(""));

        if (isStr0Empty && isStr1Empty)
            return true;
        // at least one of them is not empty
        if (isStr0Empty)
            return false;
        if (isStr1Empty)
            return false;
        //none of them is empty
        return a.equals(b);

    }


}
