package io.github.tjheslin1.esb.infrastructure.application.cqrs.command;

import io.github.tjheslin1.esb.domain.cqrs.command.EventJsonMarshaller;
import io.github.tjheslin1.esb.domain.events.BalanceEvent;
import io.github.tjheslin1.esb.infrastructure.application.events.DepositFundsBalanceEvent;
import org.bson.Document;

public class DepositFundsMarshaller implements EventJsonMarshaller {

    @Override
    public Document marshallBalanceEvent(BalanceEvent balanceEvent) {
        DepositFundsBalanceEvent depositFundsEvent = (DepositFundsBalanceEvent) balanceEvent;

        Document depositDoc = new Document("timeOfEvent", depositFundsEvent.timeOfEvent());
        depositDoc.append("accountId", depositFundsEvent.accountId());
        depositDoc.append("amount", depositFundsEvent.amount());
        return depositDoc;
    }
}