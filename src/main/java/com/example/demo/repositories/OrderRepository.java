package com.example.demo.repositories;

import com.example.demo.dto.Customer;
import com.example.demo.dto.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author huang
 */
@Repository
public interface OrderRepository extends JpaRepository<MyOrder, Long> {

}