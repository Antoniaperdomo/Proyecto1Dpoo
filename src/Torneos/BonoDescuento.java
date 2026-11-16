package Torneos;

public class BonoDescuento {
	private final String torneoOrigen;
	private final int porcentaje;
	private boolean usado;

	public BonoDescuento(String torneoOrigen, int porcentaje) {
		if (torneoOrigen == null || torneoOrigen.trim().isEmpty()) {
			throw new IllegalArgumentException("El bono debe tener torneo de origen");
		}
		if (porcentaje < 1 || porcentaje > 100) {
			throw new IllegalArgumentException("El porcentaje del bono debe estar entre 1 y 100");
		}
		this.torneoOrigen = torneoOrigen;
		this.porcentaje = porcentaje;
		this.usado = false;
	}

	public String getTorneoOrigen() {
		return torneoOrigen;
	}

	public int getPorcentaje() {
		return porcentaje;
	}

	public boolean isUsado() {
		return usado;
	}

	/** Marca el bono como usado; un bono ya usado no se puede reutilizar. */
	public void usar() {
		if (usado) {
			throw new IllegalStateException("El bono de " + torneoOrigen + " ya fue usado");
		}
		usado = true;
	}

	/** Persistencia: restaura el estado guardado. */
	public void restaurar(boolean usado) {
		this.usado = usado;
	}

	@Override
	public String toString() {
		return torneoOrigen + " - " + porcentaje + "% (" + (usado ? "usado" : "disponible") + ")";
	}
}
