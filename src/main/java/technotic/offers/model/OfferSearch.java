package technotic.offers.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class OfferSearch {

    private final String description;
    private final Double priceStart;
    private final Double priceEnd;
    private final String currency;
    private final Date expirationDateStart;
    private final Date expirationDateEnd;

    public OfferSearch(String description,
                       Double priceStart,
                       Double priceEnd,
                       String currency,
                       Date expirationDateStart,
                       Date expirationDateEnd) {
        this.description = description;
        this.priceStart = priceStart;
        this.priceEnd = priceEnd;
        this.currency = currency;
        this.expirationDateStart = expirationDateStart;
        this.expirationDateEnd = expirationDateEnd;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OfferSearch that = (OfferSearch) o;

        return new EqualsBuilder()
                .append(description, that.description)
                .append(priceStart, that.priceStart)
                .append(priceEnd, that.priceEnd)
                .append(currency, that.currency)
                .append(expirationDateStart, that.expirationDateStart)
                .append(expirationDateEnd, that.expirationDateEnd)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(description)
                .append(priceStart)
                .append(priceEnd)
                .append(currency)
                .append(expirationDateStart)
                .append(expirationDateEnd)
                .toHashCode();
    }

    public String getDescription() {
        return description;
    }

    public Double getPriceStart() {
        return priceStart;
    }

    public Double getPriceEnd() {
        return priceEnd;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getExpirationDateStart() {
        return expirationDateStart;
    }

    public Date getExpirationDateEnd() {
        return expirationDateEnd;
    }
}
