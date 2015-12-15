package org.soatexpert.bdbje.model;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Project {

    @PrimaryKey
    public Long id;

    public String name;
}
