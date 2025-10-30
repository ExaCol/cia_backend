package CIA.app.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDate releaseDate;

    @Column
    private int amount;

    @Column
    private String state;

    @Column(name = "service_id", nullable = false) 
    private Integer serviceId;

    @ManyToOne
    @JsonBackReference(value = "payment-services")
    private Usr usr;

    @Column(unique = true)
    private String externalReference; 

    private Long mpPaymentId;
    private String mpStatusDetail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Usr getUsr() {
        return usr;
    }

    public void setUsr(Usr usr) {
        this.usr = usr;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public Long getMpPaymentId() {
        return mpPaymentId;
    }

    public void setMpPaymentId(Long mpPaymentId) {
        this.mpPaymentId = mpPaymentId;
    }

    public String getMpStatusDetail() {
        return mpStatusDetail;
    }

    public void setMpStatusDetail(String mpStatusDetail) {
        this.mpStatusDetail = mpStatusDetail;
    }
}
