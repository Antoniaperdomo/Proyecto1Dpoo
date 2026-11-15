package Deportes;

import java.util.ArrayList;

import Usuario.Participante;

public class Equipo {
	private String nombre;
	private DeporteConjunto deporte;
	private int cupoMax;
	private Categoria categoria;
	private ArrayList<JugadorEquipo> jugadores;
	private ArrayList<Suspension> suspensiones;
	private boolean activo;

	// RF-01
	public Equipo(String nombre, DeporteConjunto deporte, int cupoMax, Categoria categoria,
			ArrayList<JugadorEquipo> jugadores, ArrayList<Suspension> suspensiones, boolean activo) {
		if (cupoMax <= 0) {
			throw new IllegalArgumentException("El cupo para un equipo debe ser mayor a 0");

		}
		this.nombre = nombre;
		this.deporte = deporte;
		this.cupoMax = cupoMax;
		this.categoria = categoria;
		this.jugadores = jugadores;
		this.suspensiones = suspensiones;
		this.activo = activo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public DeporteConjunto getDeporte() {
		return deporte;
	}

	public void setDeporte(DeporteConjunto deporte) {
		this.deporte = deporte;
	}

	public int getCupoMax() {
		return cupoMax;
	}

	public void setCupoMax(int cupoMax) {
		this.cupoMax = cupoMax;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public ArrayList<JugadorEquipo> getJugadores() {
		return jugadores;
	}

	public void setJugadores(ArrayList<JugadorEquipo> jugadores) {
		this.jugadores = jugadores;
	}

	public ArrayList<Suspension> getSuspensiones() {
		return suspensiones;
	}

	public void setSuspensiones(ArrayList<Suspension> suspensiones) {
		this.suspensiones = suspensiones;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean tieneMinimoDeJugadores(String ambito) {
		return contarHabilitados(ambito) >= deporte.getMinJugadores();
	}

	// RF 2 y RF 3
	public JugadorEquipo buscarJugador(Participante Participante) {
		for (JugadorEquipo j : jugadores) {
			if (j.getParticipante().equals(Participante)) {
				return j;
			}
		}
		return null;
	}

	public boolean tieneJugador(Participante Participante) {
		return buscarJugador(Participante) != null;
	}

	public JugadorEquipo agregarJugador(Participante Participante, String poscicion, int numeroCamiseta) {
		if (!categoria.aceptaEdad(Participante)) {
			throw new IllegalArgumentException("El Participante no corresponde a la categoria");
		}
		if (tieneJugador(Participante) == true) {
			throw new IllegalStateException("El Participante ya esta en el equipo " + nombre);
		}
		if (jugadores.size() >= cupoMax) {
			throw new IllegalStateException("El equipo ya alzanzo su cupo maximo");
		}
		if (poscicion == null || poscicion.equals("")) {
			throw new IllegalArgumentException("El jugador debe tener una posicion");

		}

		for (JugadorEquipo j : jugadores) {
			if (j.getNumeroCamiseta() == numeroCamiseta) {
				throw new IllegalStateException("La camiseta " + numeroCamiseta + " ya es de otro jugador");
			}
		}
		JugadorEquipo nuevo = new JugadorEquipo(Participante, poscicion, this, numeroCamiseta);
		jugadores.add(nuevo);
		return nuevo;
	}

	// RF 04, 6 , 14

	public boolean estaSuspendido(Participante Participante, String ambito) {
		for (Suspension s : suspensiones) {
			if (!s.isCumplida() && s.getAmbito().equals(ambito) && s.getJugador().equals(Participante)) {
				return true;
			}
		}
		return false;
	}

	public int contarHabilitados(String ambito) {
		int total = 0;
		for (JugadorEquipo j : jugadores) {
			if (!estaSuspendido(j.getParticipante(), ambito)) {
				total++;
			}
		}
		return total;
	}

	public void cumplirSuspension(String ambito) {
		for (Suspension s : suspensiones) {
			if (s.getAmbito().equals(ambito)) {
				s.cumplir();
			}
		}
	}

	public void registrarTarjetas(EstadisticaFutbol est, String ambito) {
		JugadorEquipo j = buscarJugador(est.getJugador());
		if (j == null) {
			return;
		}

		int amarillas = j.getAmarillas(ambito) + est.getTarjetasAmarillas();
		if (est.getTarjetasRojas() >= 1) {
			suspensiones.add(new Suspension(est.getJugador(), "Tarjeta roja", ambito));
			j.setAmarillas(ambito, 0);
		} else if (amarillas >= 2) {
			suspensiones.add(new Suspension(est.getJugador(), "Acumulacion de 2 amarillas", ambito));
			j.setAmarillas(ambito, 0);
		} else {
			j.setAmarillas(ambito, amarillas);
		}

	}

}
