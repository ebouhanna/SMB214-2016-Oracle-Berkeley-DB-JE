package org.soatexpert.bdbje.model;

import com.sleepycat.persist.model.*;

import java.util.Set;

@Entity
public class Employee {

    @PrimaryKey
    public Integer id;
    public String name;
    public String forname;

    @SecondaryKey(relate = Relationship.MANY_TO_MANY,
            relatedEntity = Project.class,
            onRelatedEntityDelete = DeleteAction.NULLIFY)
    public Set<Long> projects;

    public Employee(Integer id, String name, String forname, Set<Long> projects){
        this.id = id;
        this.name = name;
        this.forname = forname;
        this.projects = projects;
    }

    public Employee(){
    }
}
