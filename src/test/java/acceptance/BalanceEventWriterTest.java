package acceptance;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import io.github.tjheslin1.eventsourcedbanking.dao.MongoConnection;
import io.github.tjheslin1.eventsourcedbanking.dao.writing.DepositFundsMarshaller;
import io.github.tjheslin1.eventsourcedbanking.dao.writing.BalanceEventWriter;
import io.github.tjheslin1.eventsourcedbanking.dao.writing.WithdrawFundsMarshaller;
import io.github.tjheslin1.eventsourcedbanking.events.DepositFundsBalanceEvent;
import io.github.tjheslin1.eventsourcedbanking.events.WithdrawFundsBalanceEvent;
import io.github.tjheslin1.settings.Settings;
import io.github.tjheslin1.settings.TestSettings;
import org.assertj.core.api.WithAssertions;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;

import static io.github.tjheslin1.eventsourcedbanking.events.DepositFundsBalanceEvent.depositFundsEvent;
import static io.github.tjheslin1.eventsourcedbanking.events.WithdrawFundsBalanceEvent.withdrawFundsEvent;

public class BalanceEventWriterTest implements WithAssertions {

    private Settings settings = new TestSettings();
    private MongoConnection mongoConnection = new MongoConnection(settings);

    private BalanceEventWriter eventWriter;
    private MongoClient mongoClient;

    private final Clock clock = Clock.systemDefaultZone();

    @Before
    public void before() {
        mongoClient = mongoConnection.connection();
        eventWriter = new BalanceEventWriter(mongoClient, settings);
    }

    @After
    public void after() {
        MongoCollection<Document> depositFundsEventsCollection = mongoClient.getDatabase(settings.mongoDbName())
                .getCollection(DepositFundsBalanceEvent.class.getSimpleName());

        MongoCollection<Document> withdrawFundsEventsCollection = mongoClient.getDatabase(settings.mongoDbName())
                .getCollection(WithdrawFundsBalanceEvent.class.getSimpleName());

        depositFundsEventsCollection.deleteMany(new Document());
        withdrawFundsEventsCollection.deleteMany(new Document());
    }

    @Test
    public void writeDepositFundsEventToDatabaseTest() throws Exception {
        DepositFundsBalanceEvent depositFundsBalanceEvent = depositFundsEvent(6, LocalDateTime.now(clock));
        eventWriter.write(depositFundsBalanceEvent, new DepositFundsMarshaller());

        assertThat(mongoClient.getDatabase(settings.mongoDbName())
                .getCollection(depositFundsBalanceEvent.collectionName())
                .count())
                .isEqualTo(1);
    }

    @Test
    public void writeWithdrawFundsEventToDatabaseTest() throws Exception {
        WithdrawFundsBalanceEvent withdrawFundsBalanceEvent = withdrawFundsEvent(6, LocalDateTime.now(clock));
        eventWriter.write(withdrawFundsBalanceEvent, new WithdrawFundsMarshaller());

        assertThat(mongoClient.getDatabase(settings.mongoDbName())
                .getCollection(withdrawFundsBalanceEvent.collectionName())
                .count())
                .isEqualTo(1);
    }
}