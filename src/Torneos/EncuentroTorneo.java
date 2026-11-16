package Torneos;

import java.util.Date;

public class EncuentroTorneo {
	private final int ronda;
	private final InscripcionTorneo local;
	private final InscripcionTorneo visitante;
	private final Date fecha;
	private int marcadorLocal;
	private int marcadorVisitante;
	private boolean jugado;

	public EncuentroTorneo(int ronda, InscripcionTorneo local, InscripcionTorneo visitante, Date fecha) {
		if (local == null || visitante == null || local == visitante) {
			throw new IllegalArgumentException("Un encuentro necesita dos inscripciones distintas");
		}
		this.ronda = ronda;
		this.local = local;
		this.visitante = visitante;
		this.fecha = fecha;
	}

	public int getRonda() {
		return ronda;
	}

	public InscripcionTorneo getLocal() {
		return local;
	}

	public InscripcionTorneo getVisitante() {
		return visitante;
	}

	public Date getFecha() {
		return fecha;
	}

	public int getMarcadorLocal() {
		return marcadorLocal;
	}

	public int getMarcadorVisitante() {
		return marcadorVisitante;
	}

	public boolean isJugado() {
		return jugado;
	}

	/** Guarda el marcador una sola vez; las reglas del deporte las valida el Torneo. */
	public void registrar(int marcadorLocal, int marcadorVisitante) {
		if (jugado) {
			throw new IllegalStateException("El resultado de este encuentro ya fue registrado");
		}
		if (marcadorLocal < 0 || marcadorVisitante < 0) {
			throw new IllegalArgumentException("El marcador no puede ser negativo");
		}
		this.marcadorLocal = marcadorLocal;
		this.marcadorVisitante = marcadorVisitante;
		this.jugado = true;
	}

	/** Ganador del encuentro, o null si no se ha jugado o fue empate. */
	public InscripcionTorneo getGanador() {
		if (!jugado || marcadorLocal == marcadorVisitante) {
			return null;
		}
		return marcadorLocal > marcadorVisitante ? local : visitante;
	}

	public InscripcionTorneo getPerdedor() {
		InscripcionTorneo g = getGanador();
		if (g == null) {
			return null;
		}
		return g == local ? visitante : local;
	}

	@Override
	public String toString() {
		String resultado = jugado ? marcadorLocal + " - " + marcadorVisitante : "pendiente";
		return "Ronda " + ronda + ": " + local.getNombre() + " vs " + visitante.getNombre() + " [" + resultado + "]";
	}

}
