package io.github.tjheslin1.esb.infrastructure.application.cqrs.query;

import io.github.tjheslin1.WithMockito;
import io.github.tjheslin1.esb.domain.BankAccount;
import io.github.tjheslin1.esb.domain.events.BalanceCommand;
import io.github.tjheslin1.esb.domain.events.EventStore;
import io.github.tjheslin1.esb.infrastructure.application.cqrs.command.DepositFundsCommand;
import io.github.tjheslin1.esb.infrastructure.application.cqrs.command.WithdrawFundsCommand;
import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.tjheslin1.esb.application.eventwiring.DepositEventWiring.depositEventWiring;
import static io.github.tjheslin1.esb.application.eventwiring.WithdrawEventWiring.withdrawalEventWiring;
import static io.github.tjheslin1.esb.infrastructure.application.cqrs.command.DepositFundsCommand.depositFundsCommand;
import static io.github.tjheslin1.esb.infrastructure.application.cqrs.command.WithdrawFundsCommand.withdrawFundsCommand;
import static io.github.tjheslin1.esb.infrastructure.application.cqrs.query.ProjectBankAccountQuery.projectBankAccountQuery;

public class ProjectBankAccountQueryTest implements WithAssertions, WithMockito {
    public static final int ACCOUNT_ID = 20;

    private final Clock clock = Clock.systemDefaultZone();
    private LocalDateTime timeOfFirstEvent = LocalDateTime.now(clock);

    private DepositFundsCommand firstDepositFundsCommand = depositFundsCommand(ACCOUNT_ID, 10, timeOfFirstEvent);
    private WithdrawFundsCommand firstWithdrawalFundsEvent = withdrawFundsCommand(ACCOUNT_ID, 5, timeOfFirstEvent.plusMinutes(1));
    private DepositFundsCommand secondDepositFundsCommand = depositFundsCommand(ACCOUNT_ID, 40, timeOfFirstEvent.plusMinutes(2));
    private DepositFundsCommand thirdDepositFundsCommand = depositFundsCommand(ACCOUNT_ID, 38, timeOfFirstEvent.plusMinutes(3));
    private DepositFundsCommand fourthDepositFundsCommand = depositFundsCommand(ACCOUNT_ID, 1, timeOfFirstEvent.plusMinutes(4));
    private WithdrawFundsCommand secondWithdrawalFundsEvent = withdrawFundsCommand(ACCOUNT_ID, 16, timeOfFirstEvent.plusMinutes(5));

    private EventStore eventStore = mock(EventStore.class);

    @Test
    public void retrievesBankAccount() {
        when(eventStore.eventsSortedByTime(ACCOUNT_ID, depositEventWiring(), withdrawalEventWiring())).thenReturn(Stream.of(
                firstDepositFundsCommand));

        BankAccount bankAccount = projectBankAccountQuery(ACCOUNT_ID, eventStore);

        double expectedBalance = firstDepositFundsCommand.amount();
        assertThat(bankAccount.balance().funds()).isEqualTo(expectedBalance);
    }

    @Ignore // TODO as the implementation has changed testing the ordering will have to work differently. Maybe move to BalanceCommandReader
    @Test
    public void eventsAreRetrievedFromDatabaseAndSortedByDateTime() {
        when(eventStore.eventsSortedByTime(ACCOUNT_ID, depositEventWiring(), withdrawalEventWiring())).thenReturn(Stream.of(
                firstDepositFundsCommand, secondDepositFundsCommand, thirdDepositFundsCommand, fourthDepositFundsCommand));

        when(eventStore.eventsSortedByTime(ACCOUNT_ID, withdrawalEventWiring())).thenReturn(Stream.of(
                firstWithdrawalFundsEvent, secondWithdrawalFundsEvent));

        List<BalanceCommand> sortedEvents = eventStore.eventsSortedByTime(ACCOUNT_ID, depositEventWiring(), withdrawalEventWiring())
                .collect(Collectors.toList());

        assertThat(sortedEvents).containsExactly(
                firstDepositFundsCommand,
                firstWithdrawalFundsEvent,
                secondDepositFundsCommand,
                thirdDepositFundsCommand,
                fourthDepositFundsCommand,
                secondWithdrawalFundsEvent
        );
    }

    @Test
    public void retrieverHandlesEmptyResults() {
        when(eventStore.eventsSortedByTime(ACCOUNT_ID, depositEventWiring())).thenReturn(Stream.empty());

        List<BalanceCommand> sortedEvents = eventStore.eventsSortedByTime(ACCOUNT_ID, depositEventWiring())
                .collect(Collectors.toList());

        assertThat(sortedEvents.isEmpty());
    }
}