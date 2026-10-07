package Deportes;

public abstract class Deporte {
	private final String nombre;

	protected Deporte(String nombre) {

		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public abstract boolean esIndividual();

}
