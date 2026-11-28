package de.nihas101.midas.api.bookings;

import de.nihas101.midas.api.openingbalance.OpeningBalance;

import java.time.Month;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface Bookings {
    OpeningBalance openingBalance();

    FilteredBookings bookingsInMonth(final Month month);

    <T> T reduce(
            T identity,
            BiFunction<T, ? super Booking, T> accumulator,
            BinaryOperator<T> combiner
    );

    <T> List<T> map(Function<Booking, T> mapper);

    FilteredBookings filter(Function<Booking, Boolean> condition);

    boolean isEmpty();
}
