package com.TpFinal.data.dto.publicacion;

import com.TpFinal.data.dto.EstadoRegistro;
import com.TpFinal.data.dto.contrato.ContratoAlquiler;
import com.TpFinal.data.dto.inmueble.Inmueble;
import com.TpFinal.data.dto.inmueble.TipoMoneda;
import com.TpFinal.data.dto.persona.Propietario;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "publicaciones_alquiler")
public class PublicacionAlquiler extends Publicacion {
	
	public static final String pPrecioAlquiler = "valorCuota";

	public BigDecimal getValorCuota() {
		return valorCuota;
	}

	public void setValorCuota(BigDecimal valorCuota) {
		this.valorCuota = valorCuota;
	}

	@Column(name = pPrecioAlquiler)
	private BigDecimal valorCuota;

	@Enumerated(EnumType.STRING)
	@Column(name = "moneda")
	private TipoMoneda moneda;

	
	public PublicacionAlquiler() {
		super();
		tipoPublicacion = TipoPublicacion.Alquiler;
		this.estadoRegistro=EstadoRegistro.ACTIVO;
	}

	private PublicacionAlquiler(Builder b) {
		this.fechaPublicacion = b.fechaPublicacion;
		this.inmueble = b.inmueble;
		this.moneda = b.moneda;
		this.valorCuota = b.valorCuota;
		tipoPublicacion = TipoPublicacion.Alquiler;
		this.estadoRegistro=EstadoRegistro.ACTIVO;
	}

	public BigDecimal getPrecio() {
		return valorCuota;
	}

	public void setPrecio(BigDecimal precio) {
		this.valorCuota = precio;
	}

	public TipoMoneda getMoneda() {
		return moneda;
	}

	public void setMoneda(TipoMoneda moneda) {
		this.moneda = moneda;
	}

	public static class Builder {		
		private Inmueble inmueble;
		private LocalDate fechaPublicacion;
		private BigDecimal valorCuota;
		private TipoMoneda moneda;

		public Builder setInmueble(Inmueble inmueble) {
			this.inmueble = inmueble;
			return this;
		}

		public Builder setFechaPublicacion(LocalDate fechaPublicacion) {
			this.fechaPublicacion = fechaPublicacion;
			return this;
		}

		public Builder setValorCuota(BigDecimal valorCuota) {
			this.valorCuota = valorCuota;
			return this;
		}

		public Builder setMoneda(TipoMoneda moneda) {
			this.moneda = moneda;
			return this;
		}

		public PublicacionAlquiler build() {
			return new PublicacionAlquiler(this);
		}	
		
	}

	@Override
	public String toString() {
		return "PublicacionAlquiler \n[\nvalorCuota=" + valorCuota + "\nmoneda=" + moneda + "\nidPublicacion=" + idPublicacion
				+ "\nfechaPublicacion=" + fechaPublicacion + "\ntipoPublicacion=" + tipoPublicacion + "\n]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(!(obj instanceof PublicacionAlquiler)) return false;
		PublicacionAlquiler p = (PublicacionAlquiler)obj;
		return Objects.equals(p.getId(), this.getId());
	}
	

}
