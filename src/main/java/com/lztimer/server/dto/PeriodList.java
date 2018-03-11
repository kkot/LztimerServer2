package com.lztimer.server.dto;

import com.lztimer.server.entity.Period;
import lombok.*;

import java.util.List;

/**
 * DTO received by {@link com.lztimer.server.webapi.PeriodController}.
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PeriodList {

    @Singular
    @Getter
    private List<Period> periods;
}
