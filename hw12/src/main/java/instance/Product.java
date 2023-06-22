package instance;

import org.bson.Document;

public class Product {

    private final Long id;
    private final String name;
    private final double price;
    private final int currencyId;

    public Product(Long id, String name, double price, int currencyId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currencyId = currencyId;
    }

    public Product(Document doc) {
        this(doc.getLong("id"), doc.getString("name"), doc.getDouble("price"), doc.getInteger("currencyId"));
    }

    public Document toDocument() {
        return new Document().append("id", id).append("name", name).append("price", price)
            .append("currencyId", currencyId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public String toString(Currency currency) {
        return String.format("Product(id = %d, name = %s, price = %f%s)\n", id, name,
            Currency.convert(price, Currency.values()[currencyId], currency), currency.name());
    }

    @Override
    public String toString() {
        return toString(Currency.values()[currencyId]);
    }
}
