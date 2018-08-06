package technotic.offers.repositories;

import technotic.offers.model.Offer;
import technotic.offers.model.OfferSearch;

import java.util.Collection;
import java.util.Optional;

public interface OfferRepository {

    Offer save(Offer offer);

    Optional<Offer> getById(String id);

    Collection<Offer> find(OfferSearch offerSearch);

    boolean cancel(String id);
}
