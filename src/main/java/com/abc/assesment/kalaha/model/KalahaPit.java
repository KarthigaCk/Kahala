package com.abc.assesment.kalaha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class KalahaPit {
    private Integer pitId;
    private Integer stones;
}
