package pe.edu.upc.demo.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int IdUsuario;

	@Column(name = "usuario", nullable = false, length = 60)
	private String usuario;

	@Column(name = "password", nullable = false, length = 60)
	private String password;
	
	@Column(name = "email", nullable = false, length = 60)
	private String email;
	
	@Column(name = "nombres", nullable = false, length = 60)
	private String nombres;

	private Boolean enabled;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private List<Role> roles;

	public Usuario() {
		super();
		//TODO Auto-generated constructor stub
	}

	public Usuario(int idUsuario, String usuario, String password, String email, String nombres, Boolean enabled,
			List<Role> roles) {
		super();
		this.IdUsuario = idUsuario;
		this.usuario = usuario;
		this.password = password;
		this.email = email;
		this.nombres = nombres;
		this.enabled = enabled;
		this.roles = roles;
	}

	public int getIdUsuario() {
		return IdUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		IdUsuario = idUsuario;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	
	
}
