package com.abc.assesment.kalaha.model;

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
}
