package CIA.app.dtos;

public class CheckoutRequest {
    private Integer serviceId;   // servicio que el usuario va a pagar
  // opcional: descripcion personalizada, cantidad, etc.

  public Integer getServiceId() { return serviceId; }
  public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
}

