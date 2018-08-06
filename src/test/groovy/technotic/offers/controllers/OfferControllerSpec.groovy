package technotic.offers.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory
import technotic.offers.datetime.DateTimeProvider
import technotic.offers.model.Offer
import technotic.offers.model.OfferSearch
import technotic.offers.repositories.OfferRepository

import static groovy.json.JsonOutput.toJson
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [OfferController])
class OfferControllerSpec extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    OfferRepository offerRepository

    @Autowired
    DateTimeProvider dateTimeProvider

    void setup() {
        dateTimeProvider.now >> date("01-01-2018")
    }

    void "create offer"() {

        given:
        Map request = [
                description   : 'something',
                price         : '123.0',
                currency      : 'USD',
                expirationDate: '01-01-2001'
        ]

        and:
        offerRepository.save(new Offer("something", 123.0, "USD", date("01-01-2001"))) \
                  >> new Offer("123", "something", 123.0, "USD", date("01-01-2001"))

        when:
        def results = mvc.perform(
                post('/offers')
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request)))

        then:
        results.andExpect(status().isCreated())
        results.andExpect(jsonPath('$.id').value('123'))
        results.andExpect(jsonPath('$.description').value('something'))
        results.andExpect(jsonPath('$.price').value('123.0'))
        results.andExpect(jsonPath('$.currency').value('USD'))
        results.andExpect(jsonPath('$.expirationDate').value('01-01-2001'))
        results.andExpect(jsonPath('$.expired').value('true'))
    }

    @Unroll
    void "get offer by id for existing offer"() {

        def id = "1234"

        given:
        offerRepository.getById(id) >> Optional.of(new Offer(id, "something", 123.0, "USD", date(expirationDate)))

        when:
        def results = mvc.perform(
                get("/offers/$id")
                        .accept(APPLICATION_JSON))

        then:
        results.andExpect(status().isOk())
        results.andExpect(jsonPath('$.id').value(id))
        results.andExpect(jsonPath('$.description').value('something'))
        results.andExpect(jsonPath('$.price').value('123.0'))
        results.andExpect(jsonPath('$.currency').value('USD'))
        results.andExpect(jsonPath('$.expirationDate').value(expirationDate))
        if (expired)
            results.andExpect(jsonPath('$.expired').value(true))
        else
            results.andExpect(jsonPath('$.expired').doesNotExist())

        where:
        expirationDate || expired
        "01-01-2001"   || true
        "01-01-2018"   || false

    }

    void "get offer by id for non-existing / cancelled offer"() {

        def id = "1234"

        given:
        offerRepository.getById(id) >> Optional.empty()

        when:
        def results = mvc.perform(
                get("/offers/$id")
                        .accept(APPLICATION_JSON))

        then:
        results.andExpect(status().isNotFound())
    }

    void "cancel an existing offer"() {

        def id = "1234"

        given:
        offerRepository.cancel(id) >> true

        when:
        def results = mvc.perform(
                delete("/offers/$id"))

        then:
        results.andExpect(status().isOk())
        results.andExpect(content().string("OK"))
    }

    void "cancel an non-existing offer"() {

        def id = "1234"

        given:
        offerRepository.cancel(id) >> false

        when:
        def results = mvc.perform(
                delete("/offers/$id"))

        then:
        results.andExpect(status().isNotFound())
    }

    void "find offers"() {

        given:
        offerRepository.find(new OfferSearch("ab", 100.00, 120.00, "USD", date("01-01-2001"), date("01-02-2050"))) \
             >> [new Offer("123", "abc1", 115.0, "USD", date("02-01-2001")), new Offer("456", "abc2", 117.0, "USD", date("02-01-2021"))]

        when:
        def results = mvc.perform(
                get("/offers")
                        .param("description", "ab")
                        .param("priceStart", "100.00")
                        .param("priceEnd", "120.00")
                        .param("currency", "USD")
                        .param("expirationDateStart", "01-01-2001")
                        .param("expirationDateEnd", "01-02-2050")
                        .accept(APPLICATION_JSON))

        then:
        results.andExpect(status().isOk())
        results.andExpect(jsonPath('$.length()').value(2))
        results.andExpect(jsonPath('$[0].id').value("123"))
        results.andExpect(jsonPath('$[0].description').value("abc1"))
        results.andExpect(jsonPath('$[0].price').value("115.0"))
        results.andExpect(jsonPath('$[0].currency').value("USD"))
        results.andExpect(jsonPath('$[0].expirationDate').value("02-01-2001"))
        results.andExpect(jsonPath('$[0].expired').value("true"))
        results.andExpect(jsonPath('$[1].id').value("456"))
        results.andExpect(jsonPath('$[1].description').value("abc2"))
        results.andExpect(jsonPath('$[1].price').value("117.0"))
        results.andExpect(jsonPath('$[1].currency').value("USD"))
        results.andExpect(jsonPath('$[1].expirationDate').value("02-01-2021"))
        results.andExpect(jsonPath('$[1].expired').doesNotExist())
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        OfferRepository offerRepository() {
            detachedMockFactory.Stub(OfferRepository)
        }

        @Bean
        DateTimeProvider dateTimeProvider() {
            detachedMockFactory.Stub(DateTimeProvider)
        }
    }

    private Date date(String value) {
        value ? Date.parse("dd-MM-yyyy", value) : null
    }
}


