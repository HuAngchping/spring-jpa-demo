package com.example.demo.repositories;

import com.example.demo.dto.Country;
import com.example.demo.dto.Customer;
import com.example.demo.dto.CustomerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

/**
 * @author huang
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query(value = "select * from country", nativeQuery = true)
    List<Country> findByOther();

    @Query("select c from Country c")
    List<Country> findByOthers();

    @Query(value = "select * from country c order by c.id desc", nativeQuery = true)
    List<Country> findByOrder();

}