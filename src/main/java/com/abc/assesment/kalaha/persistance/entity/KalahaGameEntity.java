package com.abc.assesment.kalaha.persistance.entity;

import com.abc.assesment.kalaha.model.Player;
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

/**
 * @author Karthiga
 * Entity class for Kalaha game
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KalahaGameEntity {
    @Id
    private Integer gameId;
    private Player playersTurn;
    private String gameStatus;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private List<KalahaPitEntity> kalahaPits;

    public KalahaPitEntity getPitDetailsById(Integer pitId) {
        return this.kalahaPits.get(pitId - 1);
    }
}