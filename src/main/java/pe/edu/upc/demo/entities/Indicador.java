package pe.edu.upc.demo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Indicador")
public class Indicador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private double IdIndicador;

	@Column(name = "Precio", nullable = false)
	private double Precio;
	@Column(name = "Duracion", nullable = false)
	private double Duracion;
	@Column(name = "DuracionModificada", nullable = false)
	private double DuracionModificada;
	@Column(name = "Convexidad", nullable = false)
	private double Convexidad;

	@ManyToOne
	@JoinColumn(name = "IdBono", nullable = false)
	private Bono bono;

	public Indicador() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Indicador(double idIndicador, double precio, double duracion, double duracionModificada, double convexidad,
			Bono bono) {
		super();
		this.IdIndicador = idIndicador;
		this.Precio = precio;
		this.Duracion = duracion;
		this.DuracionModificada = duracionModificada;
		this.Convexidad = convexidad;
		this.bono = bono;
	}

	public double getIdIndicador() {
		return IdIndicador;
	}

	public void setIdIndicador(double idIndicador) {
		IdIndicador = idIndicador;
	}

	public double getPrecio() {
		return Precio;
	}

	public void setPrecio(double precio) {
		Precio = precio;
	}

	public double getDuracion() {
		return Duracion;
	}

	public void setDuracion(double duracion) {
		Duracion = duracion;
	}

	public double getDuracionModificada() {
		return DuracionModificada;
	}

	public void setDuracionModificada(double duracionModificada) {
		DuracionModificada = duracionModificada;
	}

	public double getConvexidad() {
		return Convexidad;
	}

	public void setConvexidad(double convexidad) {
		Convexidad = convexidad;
	}

	public Bono getBono() {
		return bono;
	}

	public void setBono(Bono bono) {
		this.bono = bono;
	}

}
