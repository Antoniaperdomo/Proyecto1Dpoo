package Deportes;

public class EstadisticaTenisDeMesa extends EstadisticaPractica {

	private int puntosTotales;

	public EstadisticaTenisDeMesa() {
		super();
		this.puntosTotales = 0;
	}

	public int getPuntosTotales() {
		return puntosTotales;
	}

	public void setPuntosTotales(int puntosTotales) {
		this.puntosTotales = puntosTotales;
	}

	@Override
	public String resumen() {
		return getSetsGanados() + " sets, " + puntosTotales + " puntos";
	}

}
