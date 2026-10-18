package Deportes;

import java.util.ArrayList;

public class CanchaTenis extends Instalacion {

	

	public CanchaTenis(String nombre) {
	    super(nombre, 2, 4, new ArrayList<ReservaInstalacion>());
	}

	@Override
	public String getDisciplina() {
		// TODO Auto-generated method stub
		return "TENIS";
	}

}
