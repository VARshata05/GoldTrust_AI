package com.goldtrust.jeweller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JewellerRepository extends JpaRepository<Jeweller, Long> {
    Optional<Jeweller> findByUserId(Long userId);
    Optional<Jeweller> findByJewellerCode(String jewellerCode);
}

