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
@Table(name = "Bono")
public class Bono {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int IdBono;

	@Column(name = "ValorNominal", nullable = false)
	private float ValorNominal;

	@Column(name = "TasaCupon", nullable = false)
	private float TasaCupon;

	@Column(name = "PeriodoTasaCupon", nullable = false)
	private String PeriodoTasaCupon;

	@Column(name = "PeriodoPago", nullable = false)
	private String PeriodoPago;

	@Column(name = "Vencimiento", nullable = false)
	private float Vencimiento;

	@Column(name = "PeriodoVencimiento", nullable = false)
	private String PeriodoVencimiento;

	@ManyToOne
	@JoinColumn(name = "IdUsuario", nullable = false)
	private Usuario usuario;

	public Bono() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Bono(int idBono, float valorNominal, float tasaCupon, String periodoTasaCupon, String periodoPago,
			float vencimiento, String periodoVencimiento, Usuario usuario) {
		super();
		this.IdBono = idBono;
		this.ValorNominal = valorNominal;
		this.TasaCupon = tasaCupon;
		this.PeriodoTasaCupon = periodoTasaCupon;
		this.PeriodoPago = periodoPago;
		this.Vencimiento = vencimiento;
		this.PeriodoVencimiento = periodoVencimiento;
		this.usuario = usuario;
	}

	public int getIdBono() {
		return IdBono;
	}

	public void setIdBono(int idBono) {
		IdBono = idBono;
	}

	public float getValorNominal() {
		return ValorNominal;
	}

	public void setValorNominal(float valorNominal) {
		ValorNominal = valorNominal;
	}

	public float getTasaCupon() {
		return TasaCupon;
	}

	public void setTasaCupon(float tasaCupon) {
		TasaCupon = tasaCupon;
	}

	public String getPeriodoTasaCupon() {
		return PeriodoTasaCupon;
	}

	public void setPeriodoTasaCupon(String periodoTasaCupon) {
		PeriodoTasaCupon = periodoTasaCupon;
	}

	public String getPeriodoPago() {
		return PeriodoPago;
	}

	public void setPeriodoPago(String periodoPago) {
		PeriodoPago = periodoPago;
	}

	public float getVencimiento() {
		return Vencimiento;
	}

	public void setVencimiento(float vencimiento) {
		Vencimiento = vencimiento;
	}

	public String getPeriodoVencimiento() {
		return PeriodoVencimiento;
	}

	public void setPeriodoVencimiento(String periodoVencimiento) {
		PeriodoVencimiento = periodoVencimiento;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
