package technotic.offers.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import technotic.offers.model.Offer;
import technotic.offers.model.OfferSearch;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class MongoOfferRepository implements OfferRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoOfferRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Offer save(Offer offer) {
        mongoTemplate.save(offer, "offer");
        return offer;
    }

    @Override
    public Optional<Offer> getById(String id) {
        Query query = new Query();

        query.addCriteria(Criteria.where("id").is(id));
        filterCancelledOffers(query);

        List<Offer> foundOffer = mongoTemplate.find(query, Offer.class);
        return foundOffer.isEmpty() ? Optional.empty() : Optional.of(foundOffer.get(0));
    }

    @Override
    public Collection<Offer> find(OfferSearch offerSearch) {
        Query query = new Query();

        filterCancelledOffers(query);

        if (offerSearch.getDescription() != null) {
            query.addCriteria(Criteria.where("description").regex(offerSearch.getDescription()));
        }

        if (offerSearch.getCurrency() != null) {
            query.addCriteria(Criteria.where("currency").is(offerSearch.getCurrency()));
        }

        if (offerSearch.getPriceStart() != null) {
            query.addCriteria(Criteria.where("price").gte(offerSearch.getPriceStart()).lte(offerSearch.getPriceEnd()));
        }

        if (offerSearch.getExpirationDateStart() != null) {
            query.addCriteria(Criteria.where("expirationDate").gte(offerSearch.getExpirationDateStart()).lte(offerSearch.getExpirationDateEnd()));
        }
        return mongoTemplate.find(query, Offer.class);
    }

    @Override
    public boolean cancel(String id) {
        Optional<Offer> offer = getById(id);
        if (offer.isPresent()) {
            offer.get().setCancelled(true);
            save(offer.get());
            return true;
        } else {
            return false;
        }
    }

    private void filterCancelledOffers(Query query) {
        query.addCriteria(Criteria.where("cancelled").ne(true));
    }
}