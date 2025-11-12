package CIA.app.dtos;

public class CheckoutRequest {
  private Integer serviceId;

  
  public CheckoutRequest(Integer serviceId) {
    this.serviceId = serviceId;
  }

  public Integer getServiceId() {
    return serviceId;
  }

  public void setServiceId(Integer serviceId) {
    this.serviceId = serviceId;
  }
}
