package Deportes;

public class TenisMesa extends DeporteIndividual {

	public TenisMesa(String nivel) {
		super("TENISDEMESA", nivel);
	}

	@Override
	public EstadisticaPractica crearEstadistica() {
		return new EstadisticaTenisDeMesa();
	}

}
