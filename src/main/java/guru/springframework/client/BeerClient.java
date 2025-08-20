package guru.springframework.client;

import guru.springframework.dto.BeerDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BeerClient {
    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final WebClient webClient;

    public BeerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<BeerDto> listBeer() {
        return webClient.get()
                .uri(path -> path.path(BEER_PATH).build())
                .retrieve()
                .bodyToFlux(BeerDto.class);
    }

    public Mono<BeerDto> getBeerById(String beerId) {
        return webClient.get()
                .uri(path -> path.path(BEER_PATH_ID).build(beerId))
                .retrieve()
                .bodyToMono(BeerDto.class);
    }

    public Flux<BeerDto> getBeerByStyle(String beerStyle) {
        return webClient.get()
                .uri(path -> path.path(BEER_PATH).queryParam("style", beerStyle).build())
                .retrieve()
                .bodyToFlux(BeerDto.class);
    }

    public Mono<BeerDto> createBeer(BeerDto beerDto) {
        return webClient.post()
                .uri(path -> path.path(BEER_PATH).build())
                .body(Mono.just(beerDto), BeerDto.class)
                .retrieve()
                .toBodilessEntity()
                .map(voidResponseEntity -> voidResponseEntity.getHeaders().get("Location").get(0))
                .map(path -> path.split("/")[path.split("/").length - 1])
                .flatMap(this::getBeerById);
    }

    public Mono<BeerDto> updateBeer(String beerId, BeerDto beerDto) {
        return webClient.put()
                .uri(path -> path.path(BEER_PATH_ID).build(beerId))
                .body(Mono.just(beerDto), BeerDto.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(beerId));
    }

    public Mono<Void> deleteBeer(String beerId) {
        return webClient.delete()
                .uri(path -> path.path(BEER_PATH_ID).build(beerId))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
