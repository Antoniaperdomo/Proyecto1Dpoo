package Deportes;

import java.util.ArrayList;
import java.util.Date;

public class PartidoInterno extends Partido {
	private Equipo equipo2;

	public PartidoInterno(Date fecha, DeporteConjunto deeporte, Equipo equipo1, Equipo equipo2) {
		super(fecha, deeporte, equipo1);

		if (equipo1 == equipo2) {
			throw new IllegalArgumentException("Un equipo no puede jugar contra si mismo");
		}

		this.equipo2 = equipo2;
	}

	public Equipo getEquipo2() {
		return equipo2;
	}

	public void setEquipo2(Equipo equipo2) {
		this.equipo2 = equipo2;
	}

	@Override
	public ArrayList<Equipo> getEquipos() {
		ArrayList<Equipo> lista = new ArrayList<Equipo>();
		lista.add(getEquipo1());
		lista.add(getEquipo2());
		return lista;
	}

	@Override
	public String getAmbito() {
		return Suspension.INTERNO;
	}

}
