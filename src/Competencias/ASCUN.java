package Competencias;

import Deportes.Categoria;
import Deportes.Deporte;

public class ASCUN extends Competencia {
	private String faseActual;

	public ASCUN(String nombre, String entidadOrganizadora, String temporadaOAño, Deporte deporte, Categoria categoria,
			String faseActual) {
		super(nombre, entidadOrganizadora, temporadaOAño, deporte, categoria);
		this.faseActual = faseActual;
	}

	public String getFaseActual() {
		return faseActual;
	}

	public void setFaseActual(String faseActual) {
		this.faseActual = faseActual;
	}

}
