package com.TpFinal.data.dto.persona;

import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.TpFinal.data.dto.EstadoRegistro;
import com.TpFinal.data.dto.contrato.ContratoAlquiler;


@Entity
@Table(name="inquilino")
@PrimaryKeyJoinColumn(name="id")
public class Inquilino extends RolPersona {
	
	
	
	@Enumerated(EnumType.STRING)
	@Column(name="calificacion")
	private Calificacion calificacion;
	@OneToMany(mappedBy="inquilinoContrato", fetch=FetchType.EAGER, cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private Set<ContratoAlquiler>contratos;

	
	public Inquilino() {super();}
	
	private Inquilino(Builder b) {
		super(b.persona, b.idRol, b.rolPersona, b.estadoRegistro);
		this.contratos=b.contratos;
		this.calificacion=b.calificacion;
	}

	public Calificacion getCalificacion() {
		return calificacion;
	}

	public Set<ContratoAlquiler> getContratos() {
		return contratos;
	}

	public void setCalificacion(Calificacion calificacion) {
		this.calificacion = calificacion;
	}

	public void setContratos(Set<ContratoAlquiler> contratos) {
		this.contratos = contratos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((calificacion == null) ? 0 : calificacion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Inquilino other = (Inquilino) obj;
		if (calificacion != other.calificacion)
			return false;
		return true;
	}


	
	public static class Builder{
		private Set<ContratoAlquiler>contratos;
		private Calificacion calificacion;
		private Long idRol;
		private TipoRolPersona rolPersona;
		private EstadoRegistro estadoRegistro;
		private Persona persona;
		
		public Builder setContratos(Set<ContratoAlquiler>dato) {
			this.contratos=dato;
			return this;
		}
		public Builder setCalificacion(Calificacion dato) {
			this.calificacion=dato;
			return this;
		}
		public Builder setIdRol(Long dato) {
			this.idRol=dato;
			return this;
		}
		public Builder setRolPersona(TipoRolPersona dato) {
			this.rolPersona=dato;
			return this;
		}
		public Builder setEstadoRegistro(EstadoRegistro dato) {
			this.estadoRegistro=dato;
			return this;
		}
		public Builder setContratos(EstadoRegistro dato) {
			this.estadoRegistro=dato;
			return this;
		}
		public Inquilino build(Builder b) {
			return new Inquilino(b);
		}
	}
	
	

	
}
