package Competencias;

import java.util.HashMap;
import java.util.ArrayList;

import Deportes.Equipo;

public class TablaPosiciones {

	private HashMap<Equipo, Integer> puntosPorEquipo;

	public TablaPosiciones() {
		this.puntosPorEquipo = new HashMap<>();
	}

	public void registrarUnEquipo(Equipo equipo) {
		puntosPorEquipo.put(equipo, 0);
	}

	public void sumarPuntos(Equipo equipo, int puntos) {
		if (!puntosPorEquipo.containsKey(equipo)) {
			throw new IllegalStateException("El equipo " + equipo.getNombre() + " no esta en la tabla");
		}
		int actuales = puntosPorEquipo.get(equipo);
		puntosPorEquipo.put(equipo, actuales + puntos);
	}

	public int getPuntos(Equipo equipo) {
		return puntosPorEquipo.getOrDefault(equipo, 0);
	}

	public HashMap<Equipo, Integer> getPuntosPorEquipo() {
		return puntosPorEquipo;
	}

	public boolean estaRegistrado(Equipo equipo) {
		return puntosPorEquipo.containsKey(equipo);
	}

	// RF 11 y 13
	public ArrayList<Equipo> getClasificacion() {
		ArrayList<Equipo> equipos = new ArrayList<Equipo>(puntosPorEquipo.keySet());
		for (int i = 0; i < equipos.size(); i++) {
			int mejor = i;
			for (int j = i + 1; j < equipos.size(); j++) {
				Equipo a = equipos.get(j);
				Equipo b = equipos.get(mejor);
				if (getPuntos(a) > getPuntos(b)
						|| getPuntos(a) == getPuntos(b) && a.getNombre().compareTo(b.getNombre()) < 0) {
					mejor = j;
				}
			}
			Equipo temporal = equipos.get(i);
			equipos.set(i, equipos.get(mejor));
			equipos.set(mejor, temporal);
		}
		return equipos;
	}

}
