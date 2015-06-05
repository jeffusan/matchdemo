package com.tamr.field.model;

/**
 * Created by jellin on 5/14/15.
 */


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;


@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityDetails {

    public final RecordHref href;
    public final HashMap values;

    @JsonCreator
    public EntityDetails(@JsonProperty("href") final RecordHref href,
                         @JsonProperty("values") final HashMap values) {
        this.href = href;
        this.values = values;
    }

    public RecordHref getRecord() {
        return href;
    }
}