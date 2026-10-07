package Administrador;

public class SugerenciaProducto {
	
	private String nombre;
	private String categoria;
	private String estado;
	public SugerenciaProducto(String nombre, String categoria, String estado) {
		super();
		this.nombre = nombre;
		this.categoria = categoria;
		this.estado = estado;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	

}
