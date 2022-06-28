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
@Table(name = "Valoracion")
public class Valoracion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int IdValoracion;
	@Column(name = "Tir", nullable = false)
	private double Tir;
	@Column(name = "Van", nullable = false)
	private double Van;

	@ManyToOne
	@JoinColumn(name = "IdBono", nullable = false)
	private Bono bono;

	public Valoracion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Valoracion(int idValoracion, double tir, double van, Bono bono) {
		super();
		this.IdValoracion = idValoracion;
		this.Tir = tir;
		this.Van = van;
		this.bono = bono;
	}

	public int getIdValoracion() {
		return IdValoracion;
	}

	public void setIdValoracion(int idValoracion) {
		IdValoracion = idValoracion;
	}

	public double getTir() {
		return Tir;
	}

	public void setTir(double tir) {
		Tir = tir;
	}

	public double getVan() {
		return Van;
	}

	public void setVan(double van) {
		Van = van;
	}

	public Bono getBono() {
		return bono;
	}

	public void setBono(Bono bono) {
		this.bono = bono;
	}

}
