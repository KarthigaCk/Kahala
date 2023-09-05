package com.abc.assesment.kalaha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import java.util.List;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KalahaGame {

    @Id
    private int gameId;

    private Player playersTurn;

    private String gameStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private List<KalahaPit> kalahaPits;

    public KalahaPit getPitDetailsById(Integer pitId) {

        return this.kalahaPits.get(pitId - 1);
    }

}
