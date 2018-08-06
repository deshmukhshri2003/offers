# Background

Per Wikipedia, "an offer is a proposal to sell a specific product or service under specific conditions". As a merchant I offer goods for sale. 
I want to create an offer so that I can share it with my customers.

All my offers have shopper friendly descriptions. I price all my offers up front in a defined currency.

An offer is time-bounded, with the length of time an offer is valid for defined as part of the offer, and should expire automatically.
Offers may also be explicitly cancelled before they expire.

# Assignment

You are required to create a simple RESTful software service that will allow a merchant to create a new simple offer.
Offers, once created, may be queried. After the period of time defined on the offer it should expire and further requests 
to query the offer should reflect that somehow. Before an offer has expired users may cancel it.

# Assumptions

* There is no requirement for offers to be updated so not implemented (other can cancellation)
* Assume offers can be queried by any field. Price and Expiration Time is a range of values.
* Manually cancelled offer will have "cancelled" flag and excluded from search results
* Merchant and customers have same API - in reality customers could not cancel or create offers
* Length of time assumed to be specified as expiration date
* Offers are available as soon as they are created
* No input validation for new offers
* Allows already expired offers to be created!

# API

* GET /offers - lists all current offers (cancelled offers excluded)
* GET /offers/{id} - get offer by id
* POST /offers (body contains offer as JSON) - create a new offer and return offer with generated id
* GET /offers with  addiationl parameters: description, priceStart, priceEnd, currency, expirationDateStart, expirationDate end - returns filered list of current offers (cancelled offers excluded)
* DELETE /offers/{id} - cancels an offer by id. Cancelled offers are no longer returned from any GET request

An offer is defined in JSON as:

```json
{
  "id": "id generated when created",
  "description": "Product description",
  "currency": "currency code e.g. USD",
  "price": 100.01,
  "expirationDate": "01-01-2019"
}
```


