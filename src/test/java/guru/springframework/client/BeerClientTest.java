package guru.springframework.client;

import guru.springframework.dto.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

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

        beerClient.listBeer()
                .doOnTerminate(() -> isDone.set(true))
                .subscribe(dto -> {
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
                .doOnTerminate(() -> isDone.set(true))
                .subscribe(dto -> {
                    System.out.println(dto.getBeerName());
                    isDone.set(true);
                });

        await().untilTrue(isDone);
    }

    @Test
    void testGetBeerByStyle() {
        AtomicBoolean isDone = new AtomicBoolean(false);

        beerClient.getBeerByStyle("Pale Ale")
                .doOnTerminate(() -> isDone.set(true))
                .subscribe(dto -> {
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

        beerClient.createBeer(newDto)
                .doOnTerminate(() -> isDone.set(true))
                .subscribe(dto -> {
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
                .doOnTerminate(() -> isDone.set(true))
                .subscribe(dto -> {
                    System.out.println(dto);
                    isDone.set(true);
                });

        await().untilTrue(isDone);
    }

    @Test
    void testDeleteBeer() {
        AtomicBoolean isDone = new AtomicBoolean(false);

        BeerDto newDto = BeerDto.builder()
                .price(new BigDecimal("10.99"))
                .beerName("Delete Me")
                .beerStyle("Lager")
                .quantityOnHand(200)
                .upc("54321")
                .build();

        beerClient.createBeer(newDto)
                .flatMap(createdBeer ->
                        beerClient.deleteBeer(createdBeer.getId())
                                .then(beerClient.getBeerById(createdBeer.getId())
                                        .doOnNext(dto -> {
                                            throw new AssertionError("Beer was not deleted!");
                                        })
                                        .onErrorResume(e -> {
                                            System.out.println("Beer successfully deleted: " + e.getMessage());
                                            return Mono.empty();
                                        })
                                )
                )
                .doOnTerminate(() -> isDone.set(true))
                .subscribe();

        await().untilTrue(isDone);
    }

}