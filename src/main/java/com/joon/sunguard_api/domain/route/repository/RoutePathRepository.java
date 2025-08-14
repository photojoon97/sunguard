package com.joon.sunguard_api.domain.route.repository;


import com.joon.sunguard_api.domain.route.entity.RoutePath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutePathRepository extends JpaRepository<RoutePath, Integer> {

    List<RoutePath> findByLineIdOrderBySequenceAsc(String lineId);

}