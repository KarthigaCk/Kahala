package com.abc.assesment.kalaha.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author Karthiga
 * Entity class for Kalaha game pits
 */
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KalahaPitEntity {
    @Id
    @Column
    private Integer pitId;
    @Column
    private Integer stones;

    public void clear() {
        this.stones = 0;
    }

    public void addStones(Integer stone) {
        this.stones += stone;
    }

    public boolean isEmpty() {
        return this.stones == 0;
    }
}