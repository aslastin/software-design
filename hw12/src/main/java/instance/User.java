package instance;

import org.bson.Document;

public class User {

    private final Long id;
    private final int currencyId;

    public User(Long id, int currencyId) {
        this.id = id;
        this.currencyId = currencyId;
    }

    public User(Document doc) {
        this(doc.getLong("id"), doc.getInteger("currencyId"));
    }

    public Document toDocument() {
        return new Document().append("id", id).append("currencyId", currencyId);
    }

    public Long getId() {
        return id;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    @Override
    public String toString() {
        return String.format("User(id = %d, currency = %s)\n", id,
            Currency.getCurrencyById(currencyId));
    }
}
