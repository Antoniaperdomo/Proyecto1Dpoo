package Deportes;

public abstract class EstadisticaPractica {
	private int setsGanados;

	public EstadisticaPractica() {
		this.setsGanados = 0;
	}

	public int getSetsGanados() {
		return setsGanados;
	}

	public void setSetsGanados(int setsGanados) {
		this.setsGanados = setsGanados;
	}

	public abstract String resumen();

}
