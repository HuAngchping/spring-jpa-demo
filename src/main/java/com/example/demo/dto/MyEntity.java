package com.example.demo.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author huang
 * @create 2020/11/6
 */
@Entity
@Table(name = "my_entity")
public class MyEntity {

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }
}
