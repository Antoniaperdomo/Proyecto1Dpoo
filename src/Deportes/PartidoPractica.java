package Deportes;

import java.util.ArrayList;
import java.util.Date;

import Usuario.Socio;

public class PartidoPractica {
	private Date fecha;
	private ArrayList<Socio> jugadoresParticipantes;
	private Instalacion instalacion;
	private ArrayList<EstadisticaPractica> estadistica;

	public PartidoPractica(Date fecha, ArrayList<Socio> jugadoresParcitipantes, Instalacion instalacion,
			ArrayList<EstadisticaPractica> estadistica) {
		this.fecha = fecha;
		this.jugadoresParticipantes = jugadoresParcitipantes;
		this.instalacion = instalacion;
		this.estadistica = estadistica;
	}

	public Date getFecha() {
		return fecha;
	}

	public ArrayList<Socio> getJugadoresParcitipantes() {
		return jugadoresParticipantes;
	}

	public Instalacion getInstalacion() {
		return instalacion;
	}

	public ArrayList<EstadisticaPractica> getEstadistica() {
		return estadistica;
	}

	public void agregarJugador(Socio socio) {
		jugadoresParticipantes.add(socio);
	}

}
