package CIA.app.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @ManyToOne
    @JsonBackReference(value = "payment-services")
    private Usr usr;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @JsonBackReference(value = "payments-usr")
    private Payments payment;

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

    public Payments getPayment() {
        return payment;
    }

    public void setPayment(Payments payment) {
        this.payment = payment;
    }

}
