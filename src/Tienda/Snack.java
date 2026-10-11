package Tienda;

import java.util.ArrayList;

public class Snack extends ProductoCafeteria {
	private ArrayList<String> alergenos;

	public Snack(String nombre, double precio, ArrayList<String> alergenos) {
		super(nombre, precio);
		this.alergenos = alergenos;
	}

	public ArrayList<String> getAlergenos() {
		return alergenos;
	}

}
