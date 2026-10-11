package Administrador;

import Usuario.Empleado;

public class SugerenciaProducto {

	public static final String PENDIENTE = "pendiente";
	public static final String APROBADA = "aprobada";
	public static final String RECHAZADA = "rechazada";
	private String nombre;
	private String categoria;
	private String estado;
	private Empleado empleado;

	public SugerenciaProducto(String nombre, String categoria, Empleado empleado) {
		this.nombre = nombre;
		this.categoria = categoria;
		this.estado = PENDIENTE;
		this.empleado = empleado;
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
		if (estado.equals(PENDIENTE) || estado.equals(APROBADA) || estado.equals(RECHAZADA)) {
			this.estado = estado;
		} else {
			throw new IllegalArgumentException("Estado inválido: " + estado);
		}
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

}
