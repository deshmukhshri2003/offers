package technotic.offers.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technotic.offers.datetime.DateTimeProvider;
import technotic.offers.model.Offer;
import technotic.offers.model.OfferSearch;
import technotic.offers.repositories.OfferRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/offers")
public class OfferController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);

    private final OfferRepository offerRepository;
    private final DateTimeProvider dateTimeProvider;

    @Autowired
    public OfferController(OfferRepository offerRepository, DateTimeProvider dateTimeProvider) {
        this.offerRepository = offerRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @RequestMapping(
            value = {"", "/"},
            method = POST,
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Offer> create(@RequestBody Offer offer) {
        LOGGER.info("creating {}", offer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(markExpired(offerRepository.save(offer), dateTimeProvider));
    }

    @RequestMapping(
            value = "/{id}",
            method = GET,
            produces = "application/json"
    )
    public ResponseEntity<Offer> getById(@PathVariable String id) {
        LOGGER.info("getting {}", id);
        Optional<Offer> offer = offerRepository.getById(id);
        return offer.map(o -> toResponse(o, dateTimeProvider)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(
            value = "/{id}",
            method = DELETE
    )
    public ResponseEntity<String> cancel(@PathVariable String id) {
        LOGGER.info("cancelling {}", id);
        if (offerRepository.cancel(id)) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(
            value = {"", "/"},
            method = GET,
            produces = "application/json"
    )
    public Collection<Offer> find(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Double priceStart,
            @RequestParam(required = false) Double priceEnd,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date expirationDateStart,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date expirationDateEnd) {
        LOGGER.info("finding offers with {} {} {} {} {} {}", description, currency, priceStart, priceEnd, expirationDateStart, expirationDateEnd);
        return markAnyExpired(offerRepository.find(new OfferSearch(description, priceStart, priceEnd, currency, expirationDateStart, expirationDateEnd)), dateTimeProvider);
    }

    private static Collection<Offer> markAnyExpired(Collection<Offer> offers, DateTimeProvider dateTimeProvider) {
        return offers.stream().map(offer -> markExpired(offer, dateTimeProvider)).collect(Collectors.toList());
    }

    private static Offer markExpired(Offer offer, DateTimeProvider dateTimeProvider) {
        return isExpired(offer, dateTimeProvider) ? Offer.asExpired(offer) : offer;
    }

    private static boolean isExpired(Offer offer, DateTimeProvider dateTimeProvider) {
        return dateTimeProvider.getNow().after(offer.getExpirationDate());
    }

    private static ResponseEntity<Offer> toResponse(Offer offer, DateTimeProvider dateTimeProvider) {
        return ResponseEntity.ok(markExpired(offer, dateTimeProvider));
    }
}
