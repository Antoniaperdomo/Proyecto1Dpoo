package Deportes;
public abstract class DeporteIndividual extends Deporte {
	public static final String PRINCIPIANTE = "PRINCIPIANTE";
	public static final String INTERMEDIO = "INTERMEDIO";
	public static final String AVANZADO = "AVANZADO";

	private String nivel;

	public DeporteIndividual(String nombre, String nivel) {
		super(nombre);
		this.nivel = nivel;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		if (!PRINCIPIANTE.equals(nivel) && !INTERMEDIO.equals(nivel) && !AVANZADO.equals(nivel)) {
			throw new IllegalArgumentException("Nivel invalido: " + nivel);
		}
		this.nivel = nivel;
	}

	// RF 19//
	public boolean puedeJugarCon(DeporteIndividual oponente) {
		if (PRINCIPIANTE.equals(nivel) && AVANZADO.equals(oponente.nivel)) {
			return false;
		}
		if (AVANZADO.equals(nivel) && PRINCIPIANTE.equals(oponente.nivel)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean esIndividual() {
		return true;
	}

	public abstract EstadisticaPractica crearEstadistica();

}
