package Deportes;

public class Tenis extends DeporteIndividual {

	public Tenis(String nivel) {
		super("TENIS", nivel);
	}

	@Override
	public EstadisticaPractica crearEstadistica() {
		return new EstadisticasTenisYPadel();
	}

}
