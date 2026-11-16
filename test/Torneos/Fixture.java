package Torneos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Deportes.Baloncesto;
import Deportes.Categoria;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.InscripcionIndividual;
import Deportes.JugadorEquipo;
import Deportes.Suspension;
import Deportes.Tenis;
import Usuario.Administrador;
import Usuario.Entrenador;
import Usuario.Externo;
import Usuario.Socio;

// Datos de prueba compartidos por las pruebas de torneos (HU16 a HU28).
public class Fixture {

	public static final Date INICIO = fecha("2027-03-01");
	public static final Date LIMITE = fecha("2027-02-20");
	public static final Date ANTES_DEL_LIMITE = fecha("2027-02-10");
	public static final Date DESPUES_DEL_LIMITE = fecha("2027-02-25");

	public static Date fecha(String texto) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(texto);
		} catch (ParseException e) {
			throw new IllegalArgumentException(texto);
		}
	}

	public static Administrador admin() {
		return new Administrador(900, "Admin", "admin", "1234", fecha("1980-01-01"));
	}

	public static Socio socio(int id) {
		return new Socio(id, "Socio" + id, "socio" + id, "1234", fecha("1990-01-01"), 0, true);
	}

	//Socio inscrito en una disciplina con un ELO dado (1000 = principiante, 1200 = intermedio).
	public static Socio socioEnDisciplina(int id, String disciplina, int elo) {
		Socio s = socio(id);
		InscripcionIndividual insc = s.inscribirse(disciplina);
		insc.restaurar(elo, new ArrayList<Integer>(Arrays.asList(elo)));
		return s;
	}

	public static Externo externo(int id) {
		return new Externo(id, "Externo" + id, "externo" + id, "1234", fecha("1995-05-05"), false);
	}

	public static Entrenador entrenador(int id) {
		return new Entrenador(id, "Entrenador" + id, "entrenador" + id, "1234", fecha("1985-01-01"),
				fecha("2020-01-01"));
	}

	// Equipo mayor de edad con {@code jugadores} socios; los ids empiezan en idBase. 
	public static Equipo equipoFutbol(String nombre, int jugadores, int idBase) {
		Equipo e = new Equipo(nombre, new Futbol(), 20, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true);
		for (int i = 0; i < jugadores; i++) {
			socio(idBase + i).unirseEquipo(e, "DELANTERO", i + 1);
		}
		return e;
	}

	public static Equipo equipoBaloncesto(String nombre, int jugadores, int idBase) {
		Equipo e = new Equipo(nombre, new Baloncesto(), 20, new Categoria(Categoria.MAYORES),
				new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), true);
		for (int i = 0; i < jugadores; i++) {
			socio(idBase + i).unirseEquipo(e, "ALERO", i + 1);
		}
		return e;
	}

	public static Socio integrante(Equipo e, int indice) {
		return (Socio) e.getJugadores().get(indice).getParticipante();
	}

	public static TorneoRecreativo recreativoFutbol(int cupos) {
		return new TorneoRecreativo(1, "Copa", new Futbol(), new Categoria(Categoria.MAYORES), null, null, cupos,
				INICIO, LIMITE, 0, 20);
	}

	public static TorneoCompetitivo competitivoFutbol(int cupos, double tarifa) {
		return new TorneoCompetitivo(1, "Liga", new Futbol(), new Categoria(Categoria.MAYORES), null, null, cupos,
				INICIO, LIMITE, tarifa);
	}

	public static TorneoRecreativo recreativoTenis(int cupos, String modalidad) {
		return new TorneoRecreativo(2, "Abierto", new Tenis(DeporteIndividual.INTERMEDIO), null,
				DeporteIndividual.INTERMEDIO, modalidad, cupos, INICIO, LIMITE, 0, 15);
	}

	public static TorneoCompetitivo competitivoTenis(int cupos, String modalidad, double tarifa) {
		return new TorneoCompetitivo(3, "Gran Slam", new Tenis(DeporteIndividual.INTERMEDIO), null,
				DeporteIndividual.INTERMEDIO, modalidad, cupos, INICIO, LIMITE, tarifa);
	}

	// Atajo: inscribe un socio de tenis (sencillos) con las listas vacias.
	public static InscripcionTorneo inscribir(Torneo t, Socio s, ArrayList<Socio> socios, Date ahora) {
		return t.inscribirParticipante(s, null, null, socios, new ArrayList<Administradores.Turno>(), ahora);
	}

	public static ArrayList<Socio> lista(Socio... socios) {
		return new ArrayList<Socio>(Arrays.asList(socios));
	}
}
