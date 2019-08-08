import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorInfo {

   @JsonProperty("message")
   private String message;
   @JsonProperty("status_code")
   private int statusCode;
   @JsonProperty("uri")
   private String uriRequested;

   public ErrorInfo(ApiException exception, String uriRequested) {
       this.message = exception.getMessage();
       this.statusCode = exception.getStatusCode().value();
       this.uriRequested = uriRequested;
   }

   public ErrorInfo(int statusCode, String message, String uriRequested) {
       this.message = message;
       this.statusCode = statusCode;
       this.uriRequested = uriRequested;
   }

   public String getMessage() {
       return message;
   }

   public int getStatusCode() {
       return statusCode;
   }

   public String getUriRequested() {
       return uriRequested;
   }

}