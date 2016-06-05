package io.github.tjheslin1.eventsourcedbanking.events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DepositFundsBalanceEvent implements BalanceEvent {

    private final int amount;

    private LocalDateTime timeOfEvent;

    private DepositFundsBalanceEvent(int amount, LocalDateTime timeOfEvent) {
        this.amount = amount;
        this.timeOfEvent = timeOfEvent;
    }

    public int amount() {
        return amount;
    }

    public static DepositFundsBalanceEvent depositFundsEvent(int amount, LocalDateTime timeOfEvent) {
        return new DepositFundsBalanceEvent(amount, timeOfEvent);
    }

    @Override
    public String timeOfEvent() {
        return timeOfEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS"));
    }

    @Override
    public void visit(BalanceEventVisitor balanceEventVisitor) {
        balanceEventVisitor.consider(this);
    }

    @Override
    public String collectionName() {
        return getClass().getSimpleName();
    }
}
