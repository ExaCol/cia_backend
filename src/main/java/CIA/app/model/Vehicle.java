package CIA.app.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column
    private String tipo;
    @Column
    private String placa;
    @Column
    private String tipo_tarifa_soat;
    @Column
    private String clasificacion_tecno;
    @Column
    @Temporal(TemporalType.DATE)
    private Date vencimiento_soat; 
    @Column
    @Temporal(TemporalType.DATE)
    private Date vencimiento_tecno; 

    @ManyToOne
    @JoinColumn(name = "usr_id")
    @JsonBackReference(value="usr-vehicle")
    private Usr usr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo_tarifa_soat() {
        return tipo_tarifa_soat;
    }

    public void setTipo_tarifa_soat(String tipo_tarifa_soat) {
        this.tipo_tarifa_soat = tipo_tarifa_soat;
    }

    public String getClasificacion_tecno() {
        return clasificacion_tecno;
    }

    public void setClasificacion_tecno(String clasificacion_tecno) {
        this.clasificacion_tecno = clasificacion_tecno;
    }

    public Date getVencimiento_soat() {
        return vencimiento_soat;
    }

    public void setVencimiento_soat(Date vencimiento_soat) {
        this.vencimiento_soat = vencimiento_soat;
    }

    public Date getVencimiento_tecno() {
        return vencimiento_tecno;
    }

    public void setVencimiento_tecno(Date vencimiento_tecno) {
        this.vencimiento_tecno = vencimiento_tecno;
    }

    public Usr getUsr() {
        return usr;
    }

    public void setUsr(Usr usr) {
        this.usr = usr;
    }

    
}
