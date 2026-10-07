package Deportes;

public class Padel extends DeporteIndividual {

	public Padel(String nivel) {
		super("PADEL", nivel);
	}

	@Override
	public EstadisticaPractica crearEstadistica() {
		return new EstadisticasTenisYPadel();
	}

}
