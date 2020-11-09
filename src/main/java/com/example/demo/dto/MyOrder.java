package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
@Entity
@Data
@ToString
@Table(name = "my_order")
@EntityListeners(TenantEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class MyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private Long cId;
    @Column(name = "tenant_id")
    private String tenantId;
    private BigDecimal total;

    //实体映射重复列必须设置：insertable = false,updatable = false
    @OneToOne
    @JoinColumn(name = "cId", insertable = false, updatable = false)
    private Customer customer;
}
