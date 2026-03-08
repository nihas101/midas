package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.List;

@RequiredArgsConstructor
public class DefaultBookings implements Bookings {
    private final List<Booking> bookings;

    @Override
    public MoneyAmount initialBalance() {
        return bookings.stream()
                .filter(b -> b.getType() == BookingType.SALDOVORTRAG)
                .map(Booking::getAmount)
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
    }

    @Override
    public List<Booking> groupBookingsByMonth(final Month month) {
        return bookings.stream()
                .filter(b -> b.getType() != BookingType.SALDOVORTRAG && month.equals(b.getDate().getMonth()))
                .toList();
    }
}
