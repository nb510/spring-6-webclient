package guru.springframework.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerClientTest {

    @Autowired
    BeerClient beerClient;

    @Test
    void testListBeers() throws InterruptedException {
        beerClient.listBeer().subscribe(System.out::println);

        Thread.sleep(1000);
    }
}