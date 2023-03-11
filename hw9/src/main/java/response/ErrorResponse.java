package response;

public record ErrorResponse(String searchedBy, String errorMessage) implements Response {

    @Override
    public String getSearchedBy() {
        return searchedBy;
    }
}
