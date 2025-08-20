package guru.springframework.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@SpringBootTest
class BeerClientTest {

    @Autowired
    BeerClient beerClient;

    @Test
    void testListBeers() throws InterruptedException {
        AtomicBoolean isDone = new AtomicBoolean(false);

        beerClient.listBeer().subscribe(dto -> {
            System.out.println(dto);
            isDone.set(true);
        });

        await().untilTrue(isDone);
    }

    @Test
    void testGetBeerById() throws InterruptedException {
        AtomicBoolean isDone = new AtomicBoolean(false);

        beerClient.listBeer()
                .flatMap(beerDto -> beerClient.getBeerById(beerDto.getId()))
                .subscribe(dto -> {
                    System.out.println(dto.getBeerName());
                    isDone.set(true);
                });

        await().untilTrue(isDone);
    }

    @Test
    void testGetBeerByStyle() {
        AtomicBoolean isDone = new AtomicBoolean(false);

        beerClient.getBeerByStyle("Pale Ale").subscribe(dto -> {
            System.out.println(dto);
            isDone.set(true);
        });

        await().untilTrue(isDone);
    }
}