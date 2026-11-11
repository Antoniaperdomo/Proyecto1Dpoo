package Competencias;

import java.util.ArrayList;

import Deportes.Categoria;
import Deportes.Deporte;
import Deportes.Equipo;

public class ASCUN extends Competencia {
	public static final String GROUPS = "GROUPS";
	public static final String CUARTOS = "CUARTOS";
	public static final String SEMIFINAL = "SEMIFINAL";
	public static final String FINAL = "FINAL";
	public static final String TERMINADA = "TERMINADA";

	private String faseActual;
	private TablaPosiciones tablaFase;

	public ASCUN(String nombre, String entidadOrganizadora, String temporadaOAño, Deporte deporte, Categoria categoria,
			String faseActual) {
		super(nombre, entidadOrganizadora, temporadaOAño, deporte, categoria);
		this.faseActual = faseActual;
		this.tablaFase = new TablaPosiciones();
	}

	public String getFaseActual() {
		return faseActual;
	}

	public void setFaseActual(String faseActual) {
		this.faseActual = faseActual;
	}

	@Override
	public TablaPosiciones getTabla() {
		// TODO Auto-generated method stub
		return tablaFase;
	}

	// RF 10
	@Override
	public boolean puedeJugar(Equipo equipo) {
		return super.puedeJugar(equipo) && tablaFase.estaRegistrado(equipo);
	}

	private String siguienteFase() {
		if (GROUPS.equals(faseActual)) {
			return CUARTOS;
		}
		if (CUARTOS.equals(faseActual)) {
			return SEMIFINAL;
		}
		if (SEMIFINAL.equals(faseActual)) {
			return FINAL;
		}
		if (FINAL.equals(faseActual)) {
			return TERMINADA;
		}
		throw new IllegalStateException("La competencia no tiene una fase que pueda avanzar:" + faseActual);
	}

	// RF 12
	public ArrayList<Equipo> avanzarFase(int cupos) {
		if (cupos <= 0) {
			throw new IllegalArgumentException("Los cupos para la siguiente fase deben ser mayores a 0");
		}
		String siguiente = siguienteFase();
		ArrayList<Equipo> ordenados = tablaFase.getClasificacion();
		ArrayList<Equipo> clasificados = new ArrayList<Equipo>();
		TablaPosiciones nueva = new TablaPosiciones();
		for (int i = 0; i < ordenados.size() && i < cupos; i++) {
			clasificados.add(ordenados.get(i));
			nueva.registrarUnEquipo(ordenados.get(i));
		}
		tablaFase = nueva;
		faseActual = siguiente;
		return clasificados;
	}

	public Equipo getCaompeon() {
		if (!TERMINADA.equals(faseActual) || tablaFase.getClasificacion().isEmpty()) {
			return null;
		}
		return tablaFase.getClasificacion().get(0);
	}

}
