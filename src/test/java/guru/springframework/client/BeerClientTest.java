package guru.springframework.client;

import guru.springframework.dto.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
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

    @Test
    void testCreateBeer() {
        AtomicBoolean isDone = new AtomicBoolean(false);

        BeerDto newDto = BeerDto.builder()
                .price(new BigDecimal("10.99"))
                .beerName("Mango Bobs")
                .beerStyle("IPA")
                .quantityOnHand(500)
                .upc("123245")
                .build();

        beerClient.createBeer(newDto).subscribe(dto -> {
            System.out.println(dto);
            isDone.set(true);
        });

        await().untilTrue(isDone);
    }

    @Test
    void testUpdateBeer() {
        AtomicBoolean isDone = new AtomicBoolean(false);

        BeerDto newDto = BeerDto.builder()
                .price(new BigDecimal("10.99"))
                .beerName("Mango Bobs")
                .beerStyle("IPA")
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDto updatedDto = BeerDto.builder()
                .price(new BigDecimal("10.99"))
                .beerName("$$$$$$$")
                .beerStyle("IPA")
                .quantityOnHand(500)
                .upc("123245")
                .build();

        beerClient.createBeer(newDto)
                .doOnNext(System.out::println)
                .flatMap(beerDto -> beerClient.updateBeer(beerDto.getId(), updatedDto))
                .subscribe(dto -> {
                    System.out.println(dto);
                    isDone.set(true);
                });

        await().untilTrue(isDone);
    }
}