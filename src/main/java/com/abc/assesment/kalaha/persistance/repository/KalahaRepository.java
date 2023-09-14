package com.abc.assesment.kalaha.persistance.repository;

import com.abc.assesment.kalaha.persistance.entity.KalahaGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * @author Karthiga
 * KalahaGameEntity entity repository
 */
@Repository
public interface KalahaRepository extends JpaRepository<KalahaGameEntity, Integer> {

}