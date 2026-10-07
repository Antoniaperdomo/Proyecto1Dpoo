package Deportes;

public class MesaTenisDeMesa extends Instalacion {
	private boolean techada;

	public MesaTenisDeMesa(boolean techada) {
		super(2, 4);
		this.techada = techada;
	}

	public boolean isTechada() {
		return techada;
	}

}
