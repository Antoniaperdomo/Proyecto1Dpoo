package Deportes;

import Usuario.Socio;

public class Categoria {
	public static final String SUB13 = "SUB13";
	public static final String SUB15 = "SUB15";
	public static final String SUB17 = "SUB17";
	public static final String MAYORES = "MAYORES";

	private String nombre;

	public Categoria(String nombre) {
		if (!SUB13.equals(nombre) && !SUB15.equals(nombre) && !SUB17.equals(nombre) && !MAYORES.equals(nombre)) {
			throw new IllegalArgumentException("Categoria invalida: " + nombre);
		}

		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean esIgual(Categoria otra) {
		return this.nombre.equals(otra.nombre);
	}
	// RF 02

	public boolean aceptaEdad(Socio socio) {
		int edad = socio.getEdad();
		if (SUB13.equals(nombre)) {
			return edad <= 12;
		}
		if (SUB15.equals(nombre)) {
			return edad >= 13 && edad <= 14;
		}
		if (SUB17.equals(nombre)) {
			return edad >= 15 && edad <= 16;
		}
		return edad >= 17;

	}

}
