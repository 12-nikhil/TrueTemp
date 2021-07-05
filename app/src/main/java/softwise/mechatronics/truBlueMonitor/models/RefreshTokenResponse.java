package softwise.mechatronics.truBlueMonitor.models;

public class RefreshTokenResponse {
    private boolean success;
    private String message;
    //String success;
   // String refreshToken;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

   /* public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }*/
}
