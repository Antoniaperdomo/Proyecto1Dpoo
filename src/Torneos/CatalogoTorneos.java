package Torneos;

import java.util.ArrayList;

public class CatalogoTorneos {
	private CatalogoTorneos() {
	}

	/** Filtra por estado, tipo o deporte; los filtros nulos o vacios no se aplican. */
	public static ArrayList<Torneo> filtrar(ArrayList<Torneo> todos, String estado, String tipo, String deporte) {
		ArrayList<Torneo> resultado = new ArrayList<Torneo>();
		for (Torneo t : todos) {
			boolean coincide = vacio(estado) || t.getEstado().equalsIgnoreCase(estado);
			coincide = coincide && (vacio(tipo) || t.getTipo().equalsIgnoreCase(tipo));
			coincide = coincide && (vacio(deporte) || t.getDeporte().getNombre().equalsIgnoreCase(deporte));
			if (coincide) {
				resultado.add(t);
			}
		}
		return resultado;
	}

	public static Torneo buscar(ArrayList<Torneo> todos, int id) {
		for (Torneo t : todos) {
			if (t.getId() == id) {
				return t;
			}
		}
		throw new IllegalArgumentException("El torneo " + id + " no existe");
	}

	/** Siguiente identificador libre. */
	public static int siguienteId(ArrayList<Torneo> todos) {
		int max = 0;
		for (Torneo t : todos) {
			max = Math.max(max, t.getId());
		}
		return max + 1;
	}

	public static String resumen(Torneo t) {
		String categoriaONivel = t.esIndividual() ? "nivel " + t.getNivel() + ", " + t.getModalidad().toLowerCase()
				: "categoria " + t.getCategoria().getNombre();
		String premio = t instanceof TorneoRecreativo ? "bono " + ((TorneoRecreativo) t).getPorcentajeBono() + "%"
				: "tarifa $" + t.getTarifa();
		return "[" + t.getId() + "] " + t.getNombre() + " - " + t.getTipo() + " - " + t.getDeporte().getNombre()
				+ " (" + categoriaONivel + ") - " + premio + " - inicia " + t.getFechaInicio() + " - cupos regulares "
				+ t.contarOcupados(InscripcionTorneo.REGULAR) + "/" + t.getCuposRegulares() + ", preferenciales "
				+ t.contarOcupados(InscripcionTorneo.PREFERENCIAL) + "/" + t.getCuposPreferenciales() + " - "
				+ t.getEstado();
	}

	/** Listado de texto; si no hay torneos lo informa. */
	public static String listar(ArrayList<Torneo> torneos) {
		if (torneos.isEmpty()) {
			return "No hay torneos disponibles.";
		}
		StringBuilder sb = new StringBuilder();
		for (Torneo t : torneos) {
			sb.append(resumen(t)).append("\n");
		}
		return sb.toString();
	}

	private static boolean vacio(String s) {
		return s == null || s.trim().isEmpty();
	}
}
