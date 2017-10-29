package com.TpFinal.dto.cita;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.TpFinal.dto.BorradoLogico;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.Identificable;

@Entity
@Table(name = "citas")
public class Cita implements Identificable, BorradoLogico {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_cita")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_registro")
    @NotNull
    private EstadoRegistro estadoRegistro = EstadoRegistro.ACTIVO;
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    @Column(name = "direccion_lugar")
    private String direccionLugar;
    @Column(name = "citado")
    private String citado;
    @Column(name = "observaciones")
    private String observaciones;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cita")
    TipoCita tipoDeCita;
    @OneToMany(mappedBy = "cita", fetch = FetchType.EAGER)
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    protected Set<Recordatorio> recordatorios = new HashSet<>();
    // private AgenteInmobiliario ai;

    public Cita() {
    }

    public Cita(Builder b) {
	this.fechaHora = b.fechahora;
	this.citado = b.citado;
	this.direccionLugar = b.direccionLugar;
	this.observaciones = b.observaciones;
	this.tipoDeCita = b.tipoDeCita;

    }

    @Override
    public Long getId() {
	return id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!(obj instanceof Cita))
	    return false;
	Cita other = (Cita) obj;
	return getId() != null && Objects.equals(getId(), other.getId());
    }
    
    public Set<Recordatorio> getRecordatorios() {
	return recordatorios;
    }

    public void addRecordatorio(Recordatorio recordatorio) {
	if (!this.recordatorios.contains(recordatorio)) {
	    this.recordatorios.add(recordatorio);
	    recordatorio.setCita(this);
	}
    }

    public void removeRecordatorio(Recordatorio recordatorio) {
	if (this.recordatorios.contains(recordatorio)) {
	    this.recordatorios.remove(recordatorio);
	    recordatorio.setCita(null);
	}
    }

    public LocalDateTime getFechaHora() {
	return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
	this.fechaHora = fechaHora;
    }

    public String getDireccionLugar() {
	return direccionLugar;
    }

    public void setDireccionLugar(String direccionLugar) {
	this.direccionLugar = direccionLugar;
    }

    public String getCitado() {
	return citado;
    }

    public void setCitado(String citado) {
	this.citado = citado;
    }

    public String getObservaciones() {
	return observaciones;
    }

    public void setObservaciones(String observaciones) {
	this.observaciones = observaciones;
    }

    public TipoCita getTipoDeCita() {
	return tipoDeCita;
    }

    public void setTipoDeCita(TipoCita tipoDeCita) {
	this.tipoDeCita = tipoDeCita;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Override
    public void setEstadoRegistro(EstadoRegistro estado) {
	this.estadoRegistro = estado;
    }

    @Override
    public EstadoRegistro getEstadoRegistro() {
	return this.estadoRegistro;
    }

    public class Builder {

	private TipoCita tipoDeCita;
	private String observaciones;
	private String direccionLugar;
	private String citado;
	private LocalDateTime fechahora;

	public Builder setTipoDeCita(TipoCita tipoDeCita) {
	    this.tipoDeCita = tipoDeCita;
	    return this;
	}

	public Builder setObservaciones(String observaciones) {
	    this.observaciones = observaciones;
	    return this;
	}

	public Builder setDireccionLugar(String direccionLugar) {
	    this.direccionLugar = direccionLugar;
	    return this;
	}

	public Builder setCitado(String citado) {
	    this.citado = citado;
	    return this;
	}

	public Builder setFechahora(LocalDateTime fechahora) {
	    this.fechahora = fechahora;
	    return this;
	}

	public Cita build() {
	    return new Cita(this);
	}

    }

}