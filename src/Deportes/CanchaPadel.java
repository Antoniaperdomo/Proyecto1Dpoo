package Deportes;

import java.util.ArrayList;

public class CanchaPadel extends Instalacion {

	public CanchaPadel(String nombre) {
	    super(nombre, 4, 4, new ArrayList<ReservaInstalacion>());
	}

	@Override
	public String getDisciplina() {
		// TODO Auto-generated method stub
		return "PADEL";
	}

}
