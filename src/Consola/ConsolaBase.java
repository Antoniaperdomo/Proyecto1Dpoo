package Consola;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Administradores.Turno;
import Deportes.Equipo;
import Deportes.Instalacion;
import Persistencia.DatosClub;
import Persistencia.PersistenciaClub;
import Usuario.Autentication;
import Usuario.Socio;
import Usuario.Usuario;

public abstract class ConsolaBase {
	public static final String CARPETA_DATOS = "datos";
	protected static final String[] DISCIPLINAS = { "TENIS", "PADEL", "TENISDEMESA" };
	protected static final String[] DIAS = { "lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo" };
	private static final int MAX_INTENTOS = 3;
	protected final Entrada entrada = new Entrada(System.in);
	protected DatosClub datos;
	protected Usuario usuarioActual;

	protected abstract String titulo();

	protected abstract boolean usuarioPermitido(Usuario u);

	protected abstract String[] opciones();

	protected abstract void ejecutar(int opcion);

	private boolean autenticar() {
		Autentication auth = new Autentication();
		for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
			String login = entrada.leerCredencial("Login");
			String password = entrada.leerCredencial("Password");
			try {
				Usuario u = auth.iniciarSesion(login, password, datos.getUsuarios());
				if (!usuarioPermitido(u)) {
					System.out.println("Este usuario no puede entrar por esta consola.");
					continue;
				}
				usuarioActual = u;
				return true;

			} catch (IllegalArgumentException | IllegalStateException e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println("Demasiados intentos fallidos");
		return false;
	}

	private void menuPrincipal() {
		String[] opc = opciones();
		while (true) {
			int op = entrada.elegirOpccion(titulo(), opc);
			if (op == 0) {
				return;
			}
			try {
				ejecutar(op);
			} catch (IllegalArgumentException | IllegalStateException e) {
				System.out.println("No se pudo completar la acción: " + e.getMessage());
			}
		}
	}

	private void guardar() {
		if (datos == null) {
			return;
		}
		try {
			PersistenciaClub.guardar(datos, CARPETA_DATOS);
			System.out.println("Datos guardados.");
		} catch (IOException | IllegalArgumentException e) {
			System.out.println("ERROR al guardar los datos: " + e.getMessage());
		}
	}

	public final void correr() {
		try {
			datos = PersistenciaClub.cargar(CARPETA_DATOS);
		} catch (IOException e) {
			System.out.println("No se pudieron cargar los datos: " + e.getMessage());
			return;
		}
		try {
			if (!autenticar()) {
				return;
			}
			System.out.println("\nBienvenido, " + usuarioActual.getNombre());
			menuPrincipal();
		} finally {
			guardar();
		}
	}

	protected void into(String mensaje) {
		System.out.println(mensaje);
	}

	protected String elegirTest(String titulo, String[] opciones) {
		ArrayList<String> lista = new ArrayList<String>();
		for (int i = 0; i < opciones.length; i++) {
			lista.add(opciones[i]);
		}
		int pos = entrada.elegirIndice(titulo, lista);
		if (pos < 0) {
			return null;
		}
		return opciones[pos];
	}

	protected Date bloqueHorario(Date fecha, int hora) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		c.set(Calendar.HOUR_OF_DAY, hora);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	protected ArrayList<Socio> todosLosSocios() {
		ArrayList<Socio> socios = new ArrayList<Socio>();
		for (Usuario u : datos.getUsuarios()) {
			if (u instanceof Socio) {
				socios.add((Socio) u);
			}
		}
		return socios;
	}

	protected ArrayList<Socio> sociosActivos() {
		ArrayList<Socio> activos = new ArrayList<Socio>();
		for (Socio s : todosLosSocios()) {
			if (s.isActivo()) {
				activos.add(s);
			}
		}
		return activos;
	}

	protected Socio elegirSocio(String titulo, ArrayList<Socio> candidatos) {
		ArrayList<String> descripciones = new ArrayList<String>();
		for (Socio s : candidatos) {
			descripciones.add(s.getNombre() + " (id " + s.getId() + ")");
		}
		int pos = entrada.elegirIndice(titulo, descripciones);
		if (pos < 0) {
			return null;
		}
		return candidatos.get(pos);
	}

	protected Equipo elegirEquipo(String titulo, ArrayList<Equipo> candidatos) {
		ArrayList<String> descripciones = new ArrayList<String>();
		for (Equipo e : candidatos) {
			descripciones.add(e.getNombre() + " | " + e.getDeporte().getNombre() + " | " + e.getCategoria().getNombre()
					+ " | jugadores " + e.getJugadores().size() + "/" + e.getCupoMax());
		}
		int pos = entrada.elegirIndice(titulo, descripciones);
		if (pos < 0) {
			return null;
		}
		return candidatos.get(pos);
	}

	protected Instalacion elegirInstalacion(String titulo, String disciplina) {
		ArrayList<Instalacion> candidatas = new ArrayList<Instalacion>();
		ArrayList<String> descripciones = new ArrayList<String>();
		for (Instalacion i : datos.getInstalaciones()) {
			if (disciplina == null || i.getDisciplina().equals(disciplina)) {
				candidatas.add(i);
				descripciones.add(i.getNombre() + " | " + i.getDisciplina() + " | capacidad " + i.getCapacidadMin()
						+ "-" + i.getCapacidadMax());
			}
		}
		int pos = entrada.elegirIndice(titulo, descripciones);
		if (pos < 0) {
			return null;
		}
		return candidatas.get(pos);
	}

	protected Turno elegirTurno(String titulo, ArrayList<Turno> candidatos) {
		ArrayList<String> descripciones = new ArrayList<String>();
		for (Turno t : candidatos) {
			descripciones.add(t.getEmpleado().getNombre() + " | " + t.getDiaSemana() + " " + t.getHoraInicio()
					+ ":00 a " + t.getHoraFin() + ":00");
		}
		int pos = entrada.elegirIndice(titulo, descripciones);
		if (pos < 0) {
			return null;
		}
		return candidatos.get(pos);
	}

	protected ArrayList<Socio> elegirJugadores(String disciplina, Socio incluido) {
		ArrayList<Socio> jugadores = new ArrayList<Socio>();
		if (incluido != null) {
			jugadores.add(incluido);
		}
		boolean seguir = true;
		while (seguir) {
			ArrayList<Socio> candidatos = new ArrayList<Socio>();
			for (Socio s : sociosActivos()) {
				if (!jugadores.contains(s) && s.buscarInscripcion(disciplina) != null) {
					candidatos.add(s);
				}
			}
			Socio elegido = elegirSocio("Agregar jugador (inscritos en " + disciplina + ")", candidatos);
			if (elegido == null) {
				seguir = false;
			} else {
				jugadores.add(elegido);
				seguir = entrada.leerSiNo("¿Agregar otro jugador?");
			}
		}
		return jugadores;
	}
}
