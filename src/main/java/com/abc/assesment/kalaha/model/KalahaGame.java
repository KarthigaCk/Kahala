package com.abc.assesment.kalaha.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abc.assesment.kalaha.constants.GameConstants.*;


@Getter
@Setter
@Entity
public class KalahaGame {

    private static final AtomicInteger count = new AtomicInteger(0);

    @Id
    private int gameId;

    private Player playersTurn;

    private String gameStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private List<KalahaPit> kalahaPits;

    public KalahaGame(){
        this(DEFAULT_STONES);
    }

    public KalahaGame(int stones) {
        this.gameId = count.incrementAndGet();
        List<KalahaPit> kalahaPits = IntStream.rangeClosed(1, TOTAL_PITS)
                .mapToObj(i -> {
                    if (i == 7 || i == 14) {//player 1 & 2 kalaha pit
                        return new KalahaPit(i, 0);
                    } else {
                        return new KalahaPit(i, stones);
                    }
                })
                .collect(Collectors.toList());
    }

}
