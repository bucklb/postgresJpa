package com.example.postgresdemo.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "births")
public class BirthCaseEntity {

    @Id
    @GeneratedValue(generator = "birthCase_generator")
    @SequenceGenerator(
            name = "birthCase_generator",
            sequenceName = "birthCase_sequence",
            initialValue = 1000
    )
    private Long id;

    @Column
    private String name = null;
//    @Column(columnDefinition = "")
    private String birthday = null;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void   setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void   setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getBirthday() {
        return birthday;
    }



}
