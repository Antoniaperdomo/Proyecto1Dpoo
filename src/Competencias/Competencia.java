package Competencias;

public abstract class Competencia {
	protected String nombre;
	protected String entidadOrganizadora;
	protected String temporadaOAño;
	protected Deporte deporte;
	protected Categoria categoria;
	public Competencia(String nombre, String entidadOrganizadora, String temporadaOAño, Deporte deporte,
			Categoria categoria) {
		super();
		this.nombre = nombre;
		this.entidadOrganizadora = entidadOrganizadora;
		this.temporadaOAño = temporadaOAño;
		this.deporte = deporte;
		this.categoria = categoria;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEntidadOrganizadora() {
		return entidadOrganizadora;
	}
	public void setEntidadOrganizadora(String entidadOrganizadora) {
		this.entidadOrganizadora = entidadOrganizadora;
	}
	public String getTemporadaOAño() {
		return temporadaOAño;
	}
	public void setTemporadaOAño(String temporadaOAño) {
		this.temporadaOAño = temporadaOAño;
	}
	public Deporte getDeporte() {
		return deporte;
	}
	public void setDeporte(Deporte deporte) {
		this.deporte = deporte;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
}
