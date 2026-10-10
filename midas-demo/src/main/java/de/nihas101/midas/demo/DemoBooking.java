package de.nihas101.midas.demo;

import de.nihas101.midas.commons.Source;

import java.math.BigDecimal;

public record DemoBooking(
        String comment,
        BigDecimal bigDecimal,
        Source source
) {
}
