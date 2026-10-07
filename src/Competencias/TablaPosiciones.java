package Competencias;
import java.util.HashMap;

public class TablaPosiciones {
	
	private HashMap<String, Integer> puntosPorEquipo;

	public TablaPosiciones() {
		this.puntosPorEquipo = new HashMap<>();
	}
	
	public void registrarUnEquipo(String equipo) {
		puntosPorEquipo.put(equipo, 0);
	}
	
	public void sumarPuntos(Equipo equipo, int puntos) {
		int actuales = puntosPorEquipo.getOrDefault(equipo, 0);
		puntosPorEquipo.put(equipo, actuales + puntos);
	}
	
	public int getPuntos(Equipo equipo) {
		return puntosPorEquipo.getOrDefault(equipo, 0);
	}
	
	public HashMap<Equipo, Integer> getPuntosPorEquipo(){
		return puntosPorEquipo;
	}
	

}
