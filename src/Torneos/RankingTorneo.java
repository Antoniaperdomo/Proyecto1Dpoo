package Torneos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Deportes.Equipo;
import Deportes.InscripcionIndividual;
import Deportes.Partido;
import Deportes.PartidoInterno;
import Usuario.Participante;
import Usuario.Socio;

public class RankingTorneo {
	private RankingTorneo() {
	}

	/** Puntos de liga que ha acumulado un equipo en los partidos ya jugados. */
	public static int puntajeAcumulado(Equipo equipo, ArrayList<Partido> partidos) {
		int total = 0;
		for (Partido p : partidos) {
			if (!p.isJugado()) {
				continue;
			}
			if (p.getEquipo1() == equipo) {
				total += equipo.getDeporte().calcularPuntos(p.getMarcadorEquipo1(), p.getMarcadorEquipo2());
			} else if (p instanceof PartidoInterno && ((PartidoInterno) p).getEquipo2() == equipo) {
				total += equipo.getDeporte().calcularPuntos(p.getMarcadorEquipo2(), p.getMarcadorEquipo1());
			}
		}
		return total;
	}

	/** True si el equipo esta entre los {@code n} de mayor puntaje de su deporte y categoria. */
	public static boolean equipoEntreLosMejores(final Equipo equipo, ArrayList<Equipo> todos,
			final ArrayList<Partido> partidos, int n) {
		ArrayList<Equipo> rivales = new ArrayList<Equipo>();
		for (Equipo e : todos) {
			if (e.isActivo() && e.getDeporte().getNombre().equals(equipo.getDeporte().getNombre())
					&& e.getCategoria().esIgual(equipo.getCategoria())) {
				rivales.add(e);
			}
		}
		Collections.sort(rivales, new Comparator<Equipo>() {
			@Override
			public int compare(Equipo a, Equipo b) {
				int porPuntos = puntajeAcumulado(b, partidos) - puntajeAcumulado(a, partidos);
				return porPuntos != 0 ? porPuntos : a.getNombre().compareTo(b.getNombre());
			}
		});
		return rivales.indexOf(equipo) >= 0 && rivales.indexOf(equipo) < n;
	}

	/**
	 * True si el participante es un socio activo entre los {@code n} mejores ELO
	 * de su disciplina y nivel. Los externos y los empleados no tienen ranking.
	 */
	public static boolean estaEntreLosMejores(Participante participante, String disciplina, String nivel, int n,
			ArrayList<Socio> todosLosSocios) {
		if (!(participante instanceof Socio)) {
			return false;
		}
		final String disc = disciplina;
		ArrayList<Socio> candidatos = new ArrayList<Socio>();
		for (Socio s : todosLosSocios) {
			InscripcionIndividual i = s.buscarInscripcion(disciplina);
			if (s.isActivo() && i != null && nivel.equals(i.getDeporte().getNivel())) {
				candidatos.add(s);
			}
		}
		Collections.sort(candidatos, new Comparator<Socio>() {
			@Override
			public int compare(Socio a, Socio b) {
				int porElo = b.buscarInscripcion(disc).getRankingELO() - a.buscarInscripcion(disc).getRankingELO();
				return porElo != 0 ? porElo : a.getId() - b.getId();
			}
		});
		int posicion = -1;
		for (int i = 0; i < candidatos.size(); i++) {
			if (candidatos.get(i).getId() == participante.getId()) {
				posicion = i;
			}
		}
		return posicion >= 0 && posicion < n;
	}
	

}
