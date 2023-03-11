package response;

import java.util.List;

public record OkResponse(String searchedBy, List<Result> results) implements Response {

    @Override
    public String getSearchedBy() {
        return searchedBy;
    }

    public List<Result> getResults() {
        return results;
    }

    public record Result(String name, String url) {}
}
