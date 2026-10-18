package Usuario;

import java.util.ArrayList;
import java.util.Date;

import Deportes.Instalacion;
import Deportes.PartidoPractica;

public class Entrenador extends Empleado implements Participante {

	public Entrenador(int id, String nombre, String login, String password, Date fechaNacimiento,
			Date fechaContratacion) {
		super(id, nombre, login, password, fechaNacimiento, fechaContratacion);
	}
	
	//RF 19
	
	public PartidoPractica programarPartido(Date fecha, String disciplina, Instalacion instalacion,
			ArrayList<Socio> jugadores) {
		return new PartidoPractica(fecha, disciplina, instalacion, jugadores, true);
	}

}
