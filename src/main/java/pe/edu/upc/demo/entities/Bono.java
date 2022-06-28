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

	@Column(name = "NombreBono", nullable = false)
	private String NombreBono;

	@Column(name = "ValorNominal", nullable = false)
	private float ValorNominal;

	@Column(name = "TipoMoneda", nullable = false)
	private String TipoMoneda;

	@Column(name = "TasaCupon", nullable = false)
	private float TasaCupon;

	@Column(name = "PeriodoTasaCupon", nullable = false)
	private String PeriodoTasaCupon;

	@Column(name = "TipoTasa", nullable = false)
	private String TipoTasa;

	@Column(name = "PeriodoPago", nullable = false)
	private String PeriodoPago;

	@Column(name = "Vencimiento", nullable = false)
	private float Vencimiento;

	@Column(name = "PeriodoVencimiento", nullable = false)
	private String PeriodoVencimiento;

	@Column(name = "CostoOportunidad", nullable = false)
	private float CostoOportunidad;

	@Column(name = "TasaNegociacion", nullable = false)
	private float TasaNegociacion;

	@ManyToOne
	@JoinColumn(name = "IdUsuario", nullable = false)
	private Usuario usuario;

	public Bono() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Bono(int idBono, String nombreBono, float valorNominal, String tipoMoneda, float tasaCupon,
			String periodoTasaCupon, String tipoTasa, String periodoPago, float vencimiento, String periodoVencimiento,
			float costoOportunidad, float tasaNegociacion, Usuario usuario) {
		super();
		this.IdBono = idBono;
		this.NombreBono = nombreBono;
		this.ValorNominal = valorNominal;
		this.TipoMoneda = tipoMoneda;
		this.TasaCupon = tasaCupon;
		this.PeriodoTasaCupon = periodoTasaCupon;
		this.TipoTasa = tipoTasa;
		this.PeriodoPago = periodoPago;
		this.Vencimiento = vencimiento;
		this.PeriodoVencimiento = periodoVencimiento;
		this.CostoOportunidad = costoOportunidad;
		this.TasaNegociacion = tasaNegociacion;
		this.usuario = usuario;
	}

	public int getIdBono() {
		return IdBono;
	}

	public void setIdBono(int idBono) {
		IdBono = idBono;
	}

	public String getNombreBono() {
		return NombreBono;
	}

	public void setNombreBono(String nombreBono) {
		NombreBono = nombreBono;
	}

	public float getValorNominal() {
		return ValorNominal;
	}

	public void setValorNominal(float valorNominal) {
		ValorNominal = valorNominal;
	}

	public String getTipoMoneda() {
		return TipoMoneda;
	}

	public void setTipoMoneda(String tipoMoneda) {
		TipoMoneda = tipoMoneda;
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

	public String getTipoTasa() {
		return TipoTasa;
	}

	public void setTipoTasa(String tipoTasa) {
		TipoTasa = tipoTasa;
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

	public float getCostoOportunidad() {
		return CostoOportunidad;
	}

	public void setCostoOportunidad(float costoOportunidad) {
		CostoOportunidad = costoOportunidad;
	}

	public float getTasaNegociacion() {
		return TasaNegociacion;
	}

	public void setTasaNegociacion(float tasaNegociacion) {
		TasaNegociacion = tasaNegociacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
