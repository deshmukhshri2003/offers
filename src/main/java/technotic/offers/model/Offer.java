package technotic.offers.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {

    private String id;
    private String description;
    private Double price;
    private String currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date expirationDate;
    private Boolean cancelled;
    private Boolean expired;

    private Offer(String id, String description, Double price, String currency, Date expirationDate, Boolean expired) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.expirationDate = expirationDate;
        this.expired = expired;
    }

    public Offer(String id, String description, Double price, String currency, Date expirationDate) {
        this(id, description, price, currency, expirationDate, null);
    }

    public Offer(String description, Double price, String currency, Date expirationDate) {
        this(null, description, price, currency, expirationDate);
    }

    public Offer() {
    }

    public static Offer asExpired(Offer offer) {
        return new Offer(offer.id, offer.description, offer.price, offer.currency, offer.expirationDate, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        return new EqualsBuilder()
                .append(id, offer.id)
                .append(description, offer.description)
                .append(price, offer.price)
                .append(currency, offer.currency)
                .append(expirationDate, offer.expirationDate)
                .append(cancelled, offer.cancelled)
                .append(expired, offer.expired)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(description)
                .append(price)
                .append(currency)
                .append(expirationDate)
                .append(cancelled)
                .append(expired)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Boolean isExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }
}
