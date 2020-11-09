package com.example.demo.repositories;

import com.example.demo.dto.Country;
import com.example.demo.dto.Customer;
import com.example.demo.dto.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huang
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "select customer.* from customer left join country on customer.country_id = country.id order by customer.id desc", nativeQuery = true)
    List<Customer> findByLeftJoin();

    @Query(value = "select customer.* from customer right join country on customer.country_id = country.id group by customer.id", nativeQuery = true)
    List<Customer> findByRightJoin();

    @Query(value = "select * from customer where country_id in (select id from country)", nativeQuery = true)
    List<Customer> findByInCountryId();

    @Query(value = "select cu.first_name from customer cu, country co where cu.country_id = co.id", nativeQuery = true)
    List<String> findFirstNameByT();

    @Query(value = "select * from customer where country_id = (select id from country)", nativeQuery = true)
    List<Customer> findByCountryId();

}