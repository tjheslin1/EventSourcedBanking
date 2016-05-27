package io.github.tjheslin1.events;

public class DepositFundsEvent implements Event {

    public final int amount;

    public DepositFundsEvent(int amount) {
        this.amount = amount;
    }

    @Override
    public void visit(EventVisitor eventVisitor) {
        eventVisitor.consider(this);
    }
}