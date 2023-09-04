package com.abc.assesment.kalaha.repository;

import com.abc.assesment.kalaha.model.KalahaGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KalahaRepository extends JpaRepository<KalahaGame,Integer> {

}
