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
@Table(name = ("flujo"))
public class Flujo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int IdFlujo;
	@Column(name = "Periodo", nullable = false)
	private int Periodo;
	@Column(name = "Capital", nullable = false)
	private float Capital;
	@Column(name = "Amortizacion", nullable = false)
	private float Amortizacion;
	@Column(name = "Interes", nullable = false)
	private float Interes;
	@Column(name = "Cuota", nullable = false)
	private float Cuota;

	@ManyToOne
	@JoinColumn(name = "IdBono", nullable = false)
	private Bono bono;

	public Flujo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Flujo(int idFlujo, int periodo, float capital, float amortizacion, float interes, float cuota, Bono bono) {
		super();
		this.IdFlujo = idFlujo;
		this.Periodo = periodo;
		this.Capital = capital;
		this.Amortizacion = amortizacion;
		this.Interes = interes;
		this.Cuota = cuota;
		this.bono = bono;
	}

	public int getIdFlujo() {
		return IdFlujo;
	}

	public void setIdFlujo(int idFlujo) {
		IdFlujo = idFlujo;
	}

	public int getPeriodo() {
		return Periodo;
	}

	public void setPeriodo(int periodo) {
		Periodo = periodo;
	}

	public float getCapital() {
		return Capital;
	}

	public void setCapital(float capital) {
		Capital = capital;
	}

	public float getAmortizacion() {
		return Amortizacion;
	}

	public void setAmortizacion(float amortizacion) {
		Amortizacion = amortizacion;
	}

	public float getInteres() {
		return Interes;
	}

	public void setInteres(float interes) {
		Interes = interes;
	}

	public float getCuota() {
		return Cuota;
	}

	public void setCuota(float cuota) {
		Cuota = cuota;
	}

	public Bono getBono() {
		return bono;
	}

	public void setBono(Bono bono) {
		this.bono = bono;
	}

}
