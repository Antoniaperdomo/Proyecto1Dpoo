package Deportes;

import java.util.ArrayList;

public class MesaTenisDeMesa extends Instalacion {
	private boolean techada;

	public MesaTenisDeMesa(String nombre, boolean techada) {
	    super(nombre, 2, 4, new ArrayList<ReservaInstalacion>());
	    this.techada = techada;
	}

	public boolean isTechada() {
		return techada;
	}

	@Override
	public String getDisciplina() {
		// TODO Auto-generated method stub
		return "TENISDEMESA";
	}

}
