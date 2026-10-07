package Deportes;

public class TenisMesa extends DeporteIndividual{

	public TenisMesa(String nivel) {
		super("TENIS", nivel);
	}

	@Override
	public EstadisticaPractica crearEstadistica() {
		return new EstadisticasTenisYPadel();
	}
	
	

}
