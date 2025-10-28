package CIA.app.dtos;

public class CheckoutResponse {
    private String preferenceId;
    private String initPoint; // URL para redirigir al checkout de MP
    private String sandboxInitPoint;

    public CheckoutResponse() {
    }

    public CheckoutResponse(String preferenceId, String initPoint, String sandboxInitPoint) {
        this.preferenceId = preferenceId;
        this.initPoint = initPoint;
        this.sandboxInitPoint = sandboxInitPoint;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public String getInitPoint() {
        return initPoint;
    }

    public String getSandboxInitPoint() {
        return sandboxInitPoint;
    }
}
