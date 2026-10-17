package Deportes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Usuario.Socio;

public class PartidoPractica {
	private Date fecha;
	private String Disciplina;
	private Instalacion instalacion;
	private ArrayList<Socio> jugadoresParticipantes;
	private HashMap<Socio ,EstadisticaPractica> estadisticasPorJugador;
	public PartidoPractica(Date fecha, String disciplina, Instalacion instalacion,
			ArrayList<Socio> jugadoresParticipantes, HashMap<Socio, EstadisticaPractica> estadisticasPorJugador) {
	
		this.fecha = fecha;
		Disciplina = disciplina;
		this.instalacion = instalacion;
		this.jugadoresParticipantes = jugadoresParticipantes;
		this.estadisticasPorJugador = estadisticasPorJugador;
	}
	
	
	


}
