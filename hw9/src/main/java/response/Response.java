package response;

public sealed interface Response permits OkResponse, ErrorResponse {

    String getSearchedBy();
}
