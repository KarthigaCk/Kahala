package com.abc.assesment.kalaha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KalahaPit {

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

    @JsonIgnore
    public boolean isEmpty() {
        return this.stones == 0;
    }
}
