package io.github.tjheslin1.aggregate.infrastructure.application.cqrs.deposit;

import io.github.tjheslin1.aggregate.domain.events.BalanceCommand;
import io.github.tjheslin1.aggregate.domain.events.EventVisitor;

import java.time.LocalDateTime;

public class DepositFundsCommand extends BalanceCommand {

    private DepositFundsCommand(int accountId, double amount, LocalDateTime timeOfEvent) {
        super(accountId, amount, timeOfEvent);
    }

    public static DepositFundsCommand depositFundsCommand(int accountId, double amount, LocalDateTime timeOfCommand) {
        return new DepositFundsCommand(accountId, amount, timeOfCommand);
    }

    @Override
    public void visit(EventVisitor eventVisitor) {
        eventVisitor.consider(this);
    }
}
