package Tienda;

import java.util.ArrayList;

public class Snack extends ProductoCafeteria {
	private ArrayList<String> alergenos;

	public Snack(String nombre, double precio, ArrayList<String> alergenos) {
		super(nombre, precio);
		this.alergenos = (alergenos == null) ? new ArrayList<String>() : alergenos;
	}

	public ArrayList<String> getAlergenos() {
		return alergenos;
	}
	public boolean tieneAlergenos() {
		return !alergenos.isEmpty();
	}
	public String getAdvertenciaAlergenos() {
		if (!tieneAlergenos()) {
			return getNombre() + ": no registra alergenos.";
		}
		return "Cuidado, " + getNombre() + " contiene: " + String.join(", ", alergenos);
	}

}
