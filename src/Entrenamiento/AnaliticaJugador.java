package Entrenamiento;

import java.util.ArrayList;
import java.util.Date;

import Deportes.EstadisticaJugadorPartido;
import Deportes.EstadisticaPractica;
import Deportes.Partido;
import Deportes.PartidoPractica;
import Usuario.Socio;

public class AnaliticaJugador {

	private Socio socio;
	private Date desde;
	private Date hasta;

	private int sesionesConvocado;
	private int asistencias;
	private int llegadasTarde;
	private int faltas;
	private int sesionesSinRegistro;
	private double sumaEvaluaciones;
	private int cantidadEvaluaciones;

	private int partidosConjunto;
	private int puntajeConjunto;

	private int partidosPractica;
	private int setsGanados;

	public AnaliticaJugador(Socio socio, Date desde, Date hasta) throws IllegalArgumentException {
		if (socio == null) {
			throw new IllegalArgumentException("El socio es obligatorio");
		}
		if (desde == null || hasta == null) {
			throw new IllegalArgumentException("El rango de fechas es obligatorio");
		}
		if (desde.after(hasta)) {
			throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
		}
		this.socio = socio;
		this.desde = desde;
		this.hasta = hasta;
	}

	public static AnaliticaJugador calcular(Socio socio, Date desde, Date hasta,
			ArrayList<SesionEntrenamiento> sesiones, ArrayList<Partido> partidos,
			ArrayList<PartidoPractica> partidosPractica) {
		AnaliticaJugador analitica = new AnaliticaJugador(socio, desde, hasta);
		analitica.procesarSesiones(sesiones);
		analitica.procesarPartidos(partidos);
		analitica.procesarPartidosPractica(partidosPractica);
		return analitica;
	}

	private boolean enRango(Date fecha) {
		return fecha != null && !fecha.before(desde) && !fecha.after(hasta);
	}

	public void procesarSesiones(ArrayList<SesionEntrenamiento> sesiones) {
		for (SesionEntrenamiento sesion : sesiones) {
			if (!enRango(sesion.getFecha()) || !sesion.estaConvocado(socio)) {
				continue;
			}
			sesionesConvocado++;
			RegistroAsistencia registro = sesion.obtenerRegistro(socio);
			if (registro == null) {
				sesionesSinRegistro++;
				continue;
			}
			if (registro.asistio()) {
				asistencias++;
				if (RegistroAsistencia.LLEGO_TARDE.equals(registro.getEstado())) {
					llegadasTarde++;
				}
			} else {
				faltas++;
			}
			if (registro.tieneEvaluacion()) {
				sumaEvaluaciones += registro.getEvaluacion();
				cantidadEvaluaciones++;
			}
		}
	}

	public void procesarPartidos(ArrayList<Partido> partidos) {
		for (Partido partido : partidos) {
			if (!partido.isJugado() || !enRango(partido.getFecha())) {
				continue;
			}
			for (EstadisticaJugadorPartido est : partido.getEstadisticas()) {
				if (est.getJugador().equals(socio)) {
					partidosConjunto++;
					puntajeConjunto += est.getPuntajeDestacado();
				}
			}
		}
	}

	public void procesarPartidosPractica(ArrayList<PartidoPractica> partidos) {
		for (PartidoPractica partido : partidos) {
			if (!partido.isFinalizado() || !enRango(partido.getFecha())
					|| !partido.getJugadoresParticipantes().contains(socio)) {
				continue;
			}
			partidosPractica++;
			EstadisticaPractica est = partido.getEstadisticasPorJugador().get(socio);
			if (est != null) {
				setsGanados += est.getSetsGanados();
			}
		}
	}

	public double calcularPorcentajeAsistencia() {
		int registradas = asistencias + faltas;
		return registradas == 0 ? 0 : asistencias * 100.0 / registradas;
	}

	public double calcularPromedioEvaluaciones() {
		return cantidadEvaluaciones == 0 ? 0 : sumaEvaluaciones / cantidadEvaluaciones;
	}

	public double calcularPuntajePromedioConjunto() {
		return partidosConjunto == 0 ? 0 : (double) puntajeConjunto / partidosConjunto;
	}

	public String resumen() {
		return socio.getNombre() + " | sesiones: " + sesionesConvocado + " convocado, " + asistencias + " asistio ("
				+ llegadasTarde + " tarde), " + faltas + " falto, " + sesionesSinRegistro
				+ " sin registro | asistencia: " + String.format("%.1f", calcularPorcentajeAsistencia())
				+ "% | evaluacion promedio: " + String.format("%.2f", calcularPromedioEvaluaciones()) + " ("
				+ cantidadEvaluaciones + " evaluaciones) | partidos de conjunto: " + partidosConjunto
				+ ", puntaje destacado total " + puntajeConjunto + " | partidos de practica: " + partidosPractica
				+ ", sets ganados " + setsGanados;
	}

	public Socio getSocio() {
		return socio;
	}

	public Date getDesde() {
		return desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public int getSesionesConvocado() {
		return sesionesConvocado;
	}

	public int getAsistencias() {
		return asistencias;
	}

	public int getLlegadasTarde() {
		return llegadasTarde;
	}

	public int getFaltas() {
		return faltas;
	}

	public int getSesionesSinRegistro() {
		return sesionesSinRegistro;
	}

	public int getCantidadEvaluaciones() {
		return cantidadEvaluaciones;
	}

	public int getPartidosConjunto() {
		return partidosConjunto;
	}

	public int getPuntajeConjunto() {
		return puntajeConjunto;
	}

	public int getPartidosPractica() {
		return partidosPractica;
	}

	public int getSetsGanados() {
		return setsGanados;
	}

}