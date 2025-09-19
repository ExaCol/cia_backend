package CIA.app.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "usr_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Usr usr;

    @Column
    private int price;

    @Column
    private String serviceType;

    @Column
    private String plate;

    @Column
    private LocalDate exp_date;

    @Column
    private String assurance;

    @Column
    private String duration;

    @Column
    private LocalDate start_Date;

    @Column
    private boolean graduated;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "service-partner")
    private List<Partner> partner;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @JsonBackReference(value="payment-services")
    private Payments payment;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Usr getUsr() {
        return usr;
    }

    public int getPrice() {
        return price;
    }

    public String getserviceType() {
        return serviceType;
    }

    public String getPlate() {
        return plate;
    }

    public LocalDate getExp_date() {
        return exp_date;
    }

    public String getAssurance() {
        return assurance;
    }

    public String getDuration() {
        return duration;
    }

    public LocalDate getStart_Date() {
        return start_Date;
    }

    public List<Partner> getPartner() {
        return partner;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsr(Usr usr) {
        this.usr = usr;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setserviceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setExp_date(LocalDate exp_date) {
        this.exp_date = exp_date;
    }

    public void setAssurance(String assurance) {
        this.assurance = assurance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setStart_Date(LocalDate start_Date) {
        this.start_Date = start_Date;
    }

    public void setPartner(List<Partner> partner) {
        this.partner = partner;
    }

    public Payments getPayment() {
        return payment;
    }

    public void setPayment(Payments payment) {
        this.payment = payment;
    }

    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }

    
    
}
