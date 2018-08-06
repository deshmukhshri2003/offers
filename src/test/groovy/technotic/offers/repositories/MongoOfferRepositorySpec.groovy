package technotic.offers.repositories

import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification
import spock.lang.Unroll
import technotic.offers.model.Offer
import technotic.offers.model.OfferSearch

class MongoOfferRepositorySpec extends Specification {

    static MongoOfferRepository offerRepository

    static MongodExecutable mongodExecutable
    static MongoTemplate mongoTemplate

    void setupSpec() {
        String ip = "localhost"
        int port = 27017

        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build()

        MongodStarter starter = MongodStarter.getDefaultInstance()
        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()
        mongoTemplate = new MongoTemplate(new MongoClient(ip, port), "test")
        offerRepository = new MongoOfferRepository(mongoTemplate)
    }

    void cleanupSpec() {
        mongodExecutable.stop()
    }

    void setup() {
        mongoTemplate.dropCollection(Offer.class)
    }

    void "get by id for existing offer"() {

        given: "there is an offer"
        Offer existingOffer = givenAnOffer()

        when: "the offer is fetched by id"
        def fetchedOffer = offerRepository.getById(existingOffer.id)

        then: "the existing offer is returned"
        fetchedOffer.present
        fetchedOffer.get() == existingOffer
    }

    void "get by id for non-existing offer"() {

        given: "there are no offers"
        assert findAllOffers().isEmpty()

        when: "an non-existent offer is fetched by id"
        def fetchedOffer = offerRepository.getById("123")

        then: "no offer is returned"
        !fetchedOffer.present
    }

    void "create offer"() {

        def description = "description 1"
        def currency = "USD"
        def price = 100.0
        def expirationDate = date("01/01/2001")

        Offer offer = new Offer(description, price, currency, expirationDate)

        given: "there are no offers"
        assert findAllOffers().isEmpty()

        when: "an offer is created"
        Offer createdOffer = offerRepository.save(offer)

        then: "the created offer has a generated id and the values of the supplied offer"
        createdOffer.currency == currency
        createdOffer.description == description
        createdOffer.price == price
        createdOffer.expirationDate == expirationDate
        createdOffer.id != null
    }

    void "cancel an existing offer"() {

        given: "there is an existing offer"
        Offer existingOffer = givenAnOffer()

        when: "the offer is cancelled"
        boolean cancelled = offerRepository.cancel(existingOffer.id)

        then: "the offer is successfully cancelled"
        cancelled

        and: "the offer can no longer be found"
        !offerRepository.getById(existingOffer.id).present
    }

    void "cancel a non-existing offer"() {

        def id = "123"

        given: "there is no existing offer for an id"
        !offerRepository.getById(id).present

        when: "the offer is cancelled"
        boolean cancelled = offerRepository.cancel(id)

        then: "the offer is not cancelled"
        !cancelled
    }

    @Unroll
    void "find offers"() {

        given: "some offers exist"
        givenAnOffer("1", "abc1", 110.00, "USD", "01/02/2001")
        givenAnOffer("2", "abc2", 120.00, "USD", "02/02/2001")
        givenAnOffer("3", "xyz1", 130.00, "RUB", "03/02/2001")
        givenAnOffer("4", "abc4", 140.00, "EUR", "04/02/2001")
        // This offer is cancelled and will never appear in the results
        givenAnOffer("5", "abc1", 110.00, "USD", "01/02/2001", true)

        when: "offers are searched for"
        Collection<Offer> offersFound = offerRepository.find(new OfferSearch(description, priceStart, priceEnd, currency, date(expirationDateStart), date(expirationDateEnd)))

        then: "the expected offers are found"
        offersFound.id as Set == expectedOffersFound as Set

        where:
        description | priceStart | priceEnd | currency | expirationDateStart | expirationDateEnd || expectedOffersFound
        null        | null       | null     | null     | null                | null              || ["1", "2", "3", "4"]
        "abc1"      | null       | null     | null     | null                | null              || ["1"]
        null        | 119.00     | 121.00   | null     | null                | null              || ["2"]
        null        | null       | null     | "RUB"    | null                | null              || ["3"]
        null        | null       | null     | null     | "04/02/2001"        | "04/02/2001"      || ["4"]
        "abc"       | 100.00     | 125.00   | "USD"    | "01/01/2001"        | "01/03/2001"      || ["1", "2"]
    }

    private Offer givenAnOffer() {
        givenAnOffer("description 1", 100.0, "USD", "01/01/2001")
    }

    private Offer givenAnOffer(String description, Double price, String currency, String expirationDate) {
        givenAnOffer(null, description, price, currency, expirationDate)
    }

    private Offer givenAnOffer(String id, String description, Double price, String currency, String expirationDate, Boolean cancelled = null) {
        Offer offer = offerRepository.save(new Offer(id, description, price, currency, date(expirationDate)))
        if (cancelled) {
            offerRepository.cancel(offer.id)
        }
        offer
    }

    private Collection<Offer> findAllOffers() {
        offerRepository.find(new OfferSearch(null, null, null, null, null, null))
    }

    private Date date(String value) {
        value ? Date.parse("dd/MM/yyyy", value) : null
    }
}
