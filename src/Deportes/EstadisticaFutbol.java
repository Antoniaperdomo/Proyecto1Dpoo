package Deportes;

import Usuario.Socio;

public class EstadisticaFutbol extends EstadisticaJugadorPartido {
	private int goles;
	private int asistencias;
	private int tarjetasAmarillas;
	private int tarjetasRojas;

	public EstadisticaFutbol(Socio jugador) {
		super(jugador);
		// TODO Auto-generated constructor stub
	}

	public int getGoles() {
		return goles;
	}

	public void setGoles(int goles) {
		this.goles = goles;
	}

	public int getAsistencias() {
		return asistencias;
	}

	public void setAsistencias(int asistencias) {
		this.asistencias = asistencias;
	}

	public int getTarjetasAmarillas() {
		return tarjetasAmarillas;
	}

	public void setTarjetasAmarillas(int tarjetasAmarillas) {
		this.tarjetasAmarillas = tarjetasAmarillas;
	}

	public int getTarjetasRojas() {
		return tarjetasRojas;
	}

	public void setTarjetasRojas(int tarjetasRojas) {
		this.tarjetasRojas = tarjetasRojas;
	}

	@Override
	public int getPuntajeDestacado() {
		// TODO Auto-generated method stub
		return goles;
	}

	@Override
	public String resumen() {
		// TODO Auto-generated method stub
		return goles + " goles, " + asistencias + " asistencias, " + tarjetasAmarillas + " tarjetas amarillas, "
				+ tarjetasRojas + " tarjetas rojas.";
	}

	@Override
	public void procesarTarjetas(Equipo equipo, String ambito) {
		equipo.registrarTarjetas(this, ambito);

	}

}
