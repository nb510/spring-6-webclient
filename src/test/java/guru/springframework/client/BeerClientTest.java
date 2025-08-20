package guru.springframework.client;

import guru.springframework.dto.BeerDto;
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

    @Test
    void testGetBeerById() throws InterruptedException {
        beerClient.listBeer()
                .flatMap(beerDto -> beerClient.getBeerById(beerDto.getId()))
                .subscribe(dto -> System.out.println(dto.getBeerName()));

        Thread.sleep(1000);
    }
}