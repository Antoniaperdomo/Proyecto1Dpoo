package Deportes;

import java.util.ArrayList;
import java.util.Date;

import Usuario.Socio;

public abstract class Partido {
	private Date fecha;
	private DeporteConjunto deporte;
	private Equipo equipo1;
	private int marcadorEquipo1;
	private int marcadorEquipo2;
	private ArrayList<EstadisticaJugadorPartido> estadisticas;
	private boolean jugado;

	public Partido(Date fecha, DeporteConjunto deeporte, Equipo equipo1) {
		super();
		this.fecha = fecha;
		this.deporte = deeporte;
		this.equipo1 = equipo1;
		this.estadisticas = new ArrayList<EstadisticaJugadorPartido>();
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public DeporteConjunto getDeeporte() {
		return deporte;
	}

	public void setDeeporte(DeporteConjunto deeporte) {
		this.deporte = deeporte;
	}

	public Equipo getEquipo1() {
		return equipo1;
	}

	public void setEquipo1(Equipo equipo1) {
		this.equipo1 = equipo1;
	}

	public int getMarcadorEquipo1() {
		return marcadorEquipo1;
	}

	public void setMarcadorEquipo1(int marcadorEquipo1) {
		this.marcadorEquipo1 = marcadorEquipo1;
	}

	public int getMarcadorEquipo2() {
		return marcadorEquipo2;
	}

	public void setMarcadorEquipo2(int marcadorEquipo2) {
		this.marcadorEquipo2 = marcadorEquipo2;
	}

	public ArrayList<EstadisticaJugadorPartido> getEstadisticas() {
		return estadisticas;
	}

	public void setEstadisticas(ArrayList<EstadisticaJugadorPartido> estadisticas) {
		this.estadisticas = estadisticas;
	}

	public abstract ArrayList<Equipo> getEquipos();

	public abstract String getAmbito();

	public void registrarMarcador(int marcador1, int marcador2) {
		if (marcador1 < 0 || marcador2 < 0) {
			throw new IllegalArgumentException("El marcador no puede ser negativo");
		}
		this.marcadorEquipo1 = marcador1;
		this.marcadorEquipo2 = marcador2;
	}

	private Equipo buscarEquipoDe(Socio socio) {
		for (Equipo equipo : getEquipos()) {
			if (equipo.tieneJugador(socio)) {
				return equipo;
			}
		}
		return null;
	}
	
	public boolean isJugado() {
		return jugado;
	}

	// RF 05

	public void agregarEstadisistica(EstadisticaJugadorPartido est) {
		if (est.getClass() != deporte.crearEstadistica(est.getJugador()).getClass()) {
			throw new IllegalArgumentException("La estadistica no corresponde al deporte " + deporte.getNombre());
		}
		for (EstadisticaJugadorPartido ex : estadisticas) {
			if (ex.getJugador().equals(est.getJugador())) {
				throw new IllegalStateException("El jugador ya tiene estadisticas en este partido");
			}
		}
		estadisticas.add(est);
	}

	// RF 4, 14

	public void validar() {
		for (Equipo equipo : getEquipos()) {
			if (!equipo.getDeporte().getNombre().equals(deporte.getNombre())) {
				throw new IllegalStateException("El equipo " + equipo.getNombre() + " no es de " + deporte.getNombre());
			}
			if (equipo.contarHabilitados(getAmbito()) < deporte.getMinJugadores()) {
				throw new IllegalStateException("No se puede jusgar: " + equipo.getNombre() + " no tiene el minimo de "
						+ deporte.getMinJugadores() + " jugadores habilitados");
			}
		}
		for (EstadisticaJugadorPartido est : estadisticas) {
			Equipo equipo = buscarEquipoDe(est.getJugador());
			if (equipo == null) {
				throw new IllegalStateException(
						est.getJugador().getNombre() + " no pertenece a ningun quipo del partido");
			}
			if (equipo.estaSuspendido(est.getJugador(), getAmbito())) {
				throw new IllegalStateException(
						est.getJugador().getNombre() + " esta suspendido y no puede ser alineado");
			}
		}
	}

	// RF 06

	public void cerrar() {
		for (Equipo equipo : getEquipos()) {
			equipo.cumplirSuspension(getAmbito());
		}
		for (EstadisticaJugadorPartido est : estadisticas) {
			Equipo equipo = buscarEquipoDe(est.getJugador());
			est.procesarTarjetas(equipo, getAmbito());

		}
		this.jugado = true;
	}

	public void registrarResultadi(int marcador1, int marcador2, ArrayList<EstadisticaJugadorPartido> estadisticas) {
		registrarMarcador(marcador1, marcador2);
		for (EstadisticaJugadorPartido est : estadisticas) {
			agregarEstadisistica(est);
		}
		validar();
		cerrar();
	}

}
