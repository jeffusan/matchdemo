package com.tamr.field.matchdemo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jellin on 5/15/15.
 */
@Component
public class PotentialMatchDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("#{'${inputfieldnames}'.split(',')}")
    private String[] fieldNames;
    
    final static Logger logger = LoggerFactory.getLogger(PotentialMatchDao.class);


    public void insert(Map<String, String> potentialMatch, String tamrID) {

        for (String name : fieldNames) {

            String uuid = potentialMatch.get("uuid");

            String sql = "insert into staging.potential_match values(?,?,?,?)";

            jdbcTemplate.update(sql, uuid, name, potentialMatch.get(name),tamrID);

        }
    }

    public void deletePotentialMatches(String matchId){

        String sql = "delete from staging.potential_match where entity = ?";

        jdbcTemplate.update(sql, matchId);

    }

    public List<Map<String,String>> getAllPotentialMatches() {

        List<Map<String,String>> results = jdbcTemplate.query("select p.entity, p.attribute, p.value, p.tamrid from staging.potential_match p order by p.entity;", new MatchRowMapper());
        return results;

    }

    public List<Map<String,String>> getPotentialMatch(String recordId){
    	

    	logger.debug("getting Potential Match {}",recordId);
    	
        List<Map<String,String>> results = jdbcTemplate.query("select p.entity, p.attribute, p.value, p.tamrid from staging.potential_match p  where p.entity =?;", new MatchRowMapper(),recordId);
        return results;
    }
}
    class MatchRowMapper implements RowMapper<Map<String,String>>
    {
        public Map<String,String> mapRow(ResultSet rs, int rowNum) throws SQLException {

            Map<String,String> m = new HashMap<>();
            m.put("entity",rs.getString("entity"));
            m.put("tamrid",rs.getString("tamrid"));
            m.put("attribute",rs.getString("attribute"));
            m.put("value", rs.getString("value"));
            return m;
        }

    }



