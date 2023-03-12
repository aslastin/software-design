package instance;

public enum Currency {
    RUB(0, 1.0), USD(1,  75.35), EURO(2,  80.61);

    private final int id;
    private final double coef;

    Currency(int id, double coef) {
        this.id  = id;
        this.coef = coef;
    }

    public static double convert(double value, Currency initialCurrency, Currency resultCurrency) {
        return value * initialCurrency.coef / resultCurrency.coef;
    }

    public static Currency getCurrencyById(int currencyId) {
        return Currency.values()[currencyId];
    }

    public int getId() {
        return id;
    }

    public double getCoef() {
        return coef;
    }

    @Override
    public String toString() {
        return String.format("Currency(id = %s, repr = %s, coef = %f)\n", id, name(), coef);
    }
}
