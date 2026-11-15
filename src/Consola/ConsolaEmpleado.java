package Consola;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Deportes.Instalacion;
import Deportes.PartidoPractica;
import Entrenamiento.AnaliticaJugador;
import Entrenamiento.RegistroAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Usuario.Empleado;
import Usuario.Entrenador;
import Usuario.Socio;
import Usuario.Usuario;


public class ConsolaEmpleado extends ConsolaBase{
	public static void main(String[] args) {
		new ConsolaEmpleado().correr();
	}

	private static final String[] OPCIONES_COMUNES = { 
			"Ver mi perfil y puntos de fidelidad", 
			"Ver mis turnos",
			"Solicitar cambio de turno", 
			"Ver mis solicitudes de cambio de turno", 
			"Sugerir un producto",
			"Ver mis sugerencias de producto", 
			"Comprar en tienda o cafetería (con descuento de empleado)" };

	private static final String[] OPCIONES_ENTRENADOR = { 
			"Programar partido de práctica",
			"Crear sesión de entrenamiento", 
			"Convocar jugadores a una sesión",
			"Registrar asistencia y evaluaciones de una sesión", 
			"Ver analítica de un jugador" };

	private static final String[] ESTADOS_ASISTENCIA = { RegistroAsistencia.ASISTIO, RegistroAsistencia.LLEGO_TARDE,
			RegistroAsistencia.FALTO };

	private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected String titulo() {
		return "Menú del Empleado";
	}

	@Override
	protected boolean usuarioPermitido(Usuario u) {
		return u instanceof Empleado;
	}

	@Override
	protected String[] opciones() {
		ArrayList<String> lista = new ArrayList<String>();
		for (String o : OPCIONES_COMUNES) {
			lista.add(o);
		}
		if (esEntrenador()) {
			for (String o : OPCIONES_ENTRENADOR) {
				lista.add(o);
			}
		}
		return lista.toArray(new String[0]);
	}

	@Override
	protected void ejecutar(int opcion) {
		if (opcion <= OPCIONES_COMUNES.length) {
			ejecutarComun(opcion);
		} else {
			ejecutarEntrenador(opcion - OPCIONES_COMUNES.length);
		}
	}

	private void ejecutarComun(int opcion) {
		switch (opcion) {
		case 1:
			verPerfil();
			break;
		case 2:
			verMisTurnos();
			break;
		case 3:
			solicitarCambioTurno();
			break;
		case 4:
			verMisSolicitudes();
			break;
		case 5:
			sugerirProducto();
			break;
		case 6:
			verMisSugerencias();
			break;
		case 7:
			new MenuCompras(entrada, datos, usuarioActual).comprar();
			break;
		}
	}

	private void ejecutarEntrenador(int opcion) {
		switch (opcion) {
		case 1:
			programarPartidoPractica();
			break;
		case 2:
			crearSesion();
			break;
		case 3:
			convocarJugadores();
			break;
		case 4:
			registrarAsistencia();
			break;
		case 5:
			verAnalitica();
			break;
		}
	}


	private Empleado empleado() {
		return (Empleado) usuarioActual;
	}

	private boolean esEntrenador() {
		return usuarioActual instanceof Entrenador;
	}

	private String tipoDeEmpleado() {
		return esEntrenador() ? "Entrenador" : "Fisioterapeuta";
	}

	private String leerTexto(String prompt) {
		while (true) {
			String s = entrada.leerCredencial(prompt).trim();
			if (!s.isEmpty()) {
				return s;
			}
			info("  El valor no puede estar vacío.");
		}
	}

	private String descripcionTurno(Turno t) {
		return t.getDiaSemana() + " de " + t.getHoraInicio() + ":00 a " + t.getHoraFin() + ":00";
	}

	private int posicionDia(String dia) {
		for (int i = 0; i < DIAS.length; i++) {
			if (DIAS[i].equals(dia)) {
				return i;
			}
		}
		return DIAS.length;
	}

	private ArrayList<Turno> misTurnos() {
		ArrayList<Turno> mios = new ArrayList<Turno>();
		for (Turno t : datos.getTurnos()) {
			if (t.getEmpleado().getId() == usuarioActual.getId()) {
				mios.add(t);
			}
		}
		Collections.sort(mios, new Comparator<Turno>() {
			public int compare(Turno a, Turno b) {
				int c = posicionDia(a.getDiaSemana()) - posicionDia(b.getDiaSemana());
				return (c != 0) ? c : a.getHoraInicio() - b.getHoraInicio();
			}
		});
		return mios;
	}

	private ArrayList<SesionEntrenamiento> misSesiones() {
		ArrayList<SesionEntrenamiento> mias = new ArrayList<SesionEntrenamiento>();
		for (SesionEntrenamiento s : datos.getSesiones()) {
			if (s.getEntrenadorResponsable().getId() == usuarioActual.getId()) {
				mias.add(s);
			}
		}
		return mias;
	}

	private String descripcionSesion(SesionEntrenamiento s) {
		String estado;
		if (!s.isFinalizada()) {
			estado = "abierta";
		} else if (s.asistenciaCompleta()) {
			estado = "finalizada";
		} else {
			estado = "finalizada, asistencia incompleta";
		}
		return formatoFecha.format(s.getFecha()) + " " + s.getHora() + ":00 | " + s.getInstalacionUtilizada()
				+ " | convocados: " + s.getListaDeJugadoresConvocados().size() + " | " + estado;
	}

	private SesionEntrenamiento elegirSesion(String titulo, ArrayList<SesionEntrenamiento> candidatas) {
		ArrayList<String> descripciones = new ArrayList<String>();
		for (SesionEntrenamiento s : candidatas) {
			descripciones.add(descripcionSesion(s));
		}
		int pos = entrada.elegirIndice(titulo, descripciones);
		if (pos < 0) {
			return null;
		}
		return candidatas.get(pos);
	}


	private void verPerfil() {
		Empleado e = empleado();
		info("");
		info("Nombre: " + e.getNombre() + " (id " + e.getId() + ")");
		info("Cargo: " + tipoDeEmpleado());
		info("Login: " + e.getLogin());
		info("Edad: " + e.getEdad() + " años");
		info("Fecha de contratación: " + formatoFecha.format(e.getFechaContratacion()));
		info("Puntos de fidelidad: " + e.getPuntosFidelidad());
		info("Descuento de empleado: " + (int) Math.round(e.getDescuentoPropio() * 100) + "%");
	}

	private void verMisTurnos() {
		ArrayList<Turno> turnos = misTurnos();
		info("");
		if (turnos.isEmpty()) {
			info("No tiene turnos asignados.");
			return;
		}
		info("Mis turnos:");
		for (Turno t : turnos) {
			info("  - " + descripcionTurno(t));
		}
	}

	private boolean tieneSolicitudPendiente(Turno turno) {
		for (SolicitudCambioTurno s : datos.getSolicitudes()) {
			if (s.getTurnoActual() == turno && s.getEstado().equals(SolicitudCambioTurno.PENDIENTE)) {
				return true;
			}
		}
		return false;
	}

	private void solicitarCambioTurno() {
		ArrayList<Turno> turnos = misTurnos();
		ArrayList<String> descripciones = new ArrayList<String>();
		for (Turno t : turnos) {
			descripciones.add(descripcionTurno(t));
		}
		int pos = entrada.elegirIndice("¿Qué turno quiere cambiar?", descripciones);
		if (pos < 0) {
			return;
		}
		Turno actual = turnos.get(pos);
		if (tieneSolicitudPendiente(actual)) {
			info("Ya hay una solicitud pendiente para ese turno.");
			return;
		}

		info("Datos del turno que propone:");
		String dia = elegirTexto("Día de la semana", DIAS);
		if (dia == null) {
			return;
		}
		int inicio = entrada.leerEntero("Hora de inicio", 0, 23);
		int fin = entrada.leerEntero("Hora de fin", 1, 24);
		Turno propuesto = new Turno(empleado(), dia, inicio, fin);

		empleado().solicitarCambioTurno(actual, propuesto, datos.getSolicitudes());
		info("Solicitud enviada. Un administrador la revisará.");
	}

	private void verMisSolicitudes() {
		boolean hay = false;
		info("");
		for (SolicitudCambioTurno s : datos.getSolicitudes()) {
			if (s.getEmpleado().getId() == usuarioActual.getId()) {
				if (!hay) {
					info("Mis solicitudes de cambio de turno:");
					hay = true;
				}
				info("  - " + descripcionTurno(s.getTurnoActual()) + "  ->  " + descripcionTurno(s.getTurnoPropuesto())
						+ " | " + s.getEstado());
			}
		}
		if (!hay) {
			info("No ha enviado solicitudes.");
		}
	}

	private void sugerirProducto() {
		String nombre = leerTexto("Nombre del producto");
		String categoria = leerTexto("Categoría (ej. Ropa, Equipo, Bebida, Snack)");
		empleado().sugerirProducto(nombre, categoria, datos.getSugerencias());
		info("Sugerencia enviada. Un administrador la evaluará.");
	}

	private void verMisSugerencias() {
		boolean hay = false;
		info("");
		for (SugerenciaProducto s : datos.getSugerencias()) {
			if (s.getEmpleado().getId() == usuarioActual.getId()) {
				if (!hay) {
					info("Mis sugerencias:");
					hay = true;
				}
				info("  - " + s.getNombre() + " (" + s.getCategoria() + ") | " + s.getEstado());
			}
		}
		if (!hay) {
			info("No ha hecho sugerencias.");
		}
	}


	private void programarPartidoPractica() {
		String disciplina = elegirTexto("Disciplina", DISCIPLINAS);
		if (disciplina == null) {
			return;
		}
		Instalacion instalacion = elegirInstalacion("Instalación", disciplina);
		if (instalacion == null) {
			return;
		}
		Date fecha = entrada.leerFechaHora("Fecha y hora del partido");
		ArrayList<Socio> jugadores = elegirJugadores(disciplina, null);
		if (jugadores.isEmpty()) {
			info("No se eligió ningún jugador. Partido cancelado.");
			return;
		}
		PartidoPractica partido = ((Entrenador) usuarioActual).programarPartido(fecha, disciplina, instalacion,
				jugadores);
		partido.registrarEnJugadores();
		datos.getPartidosPractica().add(partido);
		info("Partido de práctica programado con " + jugadores.size() + " jugadores en " + instalacion.getNombre()
				+ ".");
	}

	private void crearSesion() {
		Date fecha = entrada.leerFcha("Fecha de la sesión");
		int hora = entrada.leerEntero("Hora de inicio", 0, 23);
		double duracion = entrada.leerDecimal("Duración en horas", 0.5);
		Instalacion instalacion = elegirInstalacion("Instalación de la sesión", null);
		if (instalacion == null) {
			return;
		}
		SesionEntrenamiento sesion = new SesionEntrenamiento(fecha, hora, duracion, (Entrenador) usuarioActual,
				instalacion.getNombre());
		datos.getSesiones().add(sesion);
		info("Sesión creada. Ahora puede convocar jugadores.");
	}

	private void convocarJugadores() {
		ArrayList<SesionEntrenamiento> abiertas = new ArrayList<SesionEntrenamiento>();
		for (SesionEntrenamiento s : misSesiones()) {
			if (!s.isFinalizada()) {
				abiertas.add(s);
			}
		}
		SesionEntrenamiento sesion = elegirSesion("¿A qué sesión convoca?", abiertas);
		if (sesion == null) {
			return;
		}
		boolean seguir = true;
		while (seguir) {
			ArrayList<Socio> candidatos = new ArrayList<Socio>();
			for (Socio s : sociosActivos()) {
				if (!sesion.estaConvocado(s)) {
					candidatos.add(s);
				}
			}
			Socio elegido = elegirSocio("Socio a convocar", candidatos);
			if (elegido == null) {
				seguir = false;
			} else {
				sesion.convocarJugador(elegido);
				info("Convocado: " + elegido.getNombre());
				seguir = entrada.leerSiNo("¿Convocar a otro jugador?");
			}
		}
		info("La sesión tiene " + sesion.getListaDeJugadoresConvocados().size() + " convocados.");
	}

	private void registrarAsistencia() {
		ArrayList<SesionEntrenamiento> pendientes = new ArrayList<SesionEntrenamiento>();
		for (SesionEntrenamiento s : misSesiones()) {
			if (!s.isFinalizada() || !s.asistenciaCompleta()) {
				pendientes.add(s);
			}
		}
		SesionEntrenamiento sesion = elegirSesion("¿De qué sesión registra la asistencia?", pendientes);
		if (sesion == null) {
			return;
		}
		if (sesion.getListaDeJugadoresConvocados().isEmpty()) {
			info("La sesión no tiene jugadores convocados.");
			return;
		}
		if (!sesion.isFinalizada()) {
			if (!entrada.leerSiNo("Esto finaliza la sesión y ya no se podrán cambiar los convocados. ¿Continuar?")) {
				return;
			}
			sesion.finalizarSesion();
		}
		for (Socio socio : new ArrayList<Socio>(sesion.getListaDeJugadoresConvocados())) {
			if (sesion.obtenerRegistro(socio) != null) {
				continue;
			}
			String estado = elegirTexto("Asistencia de " + socio.getNombre(), ESTADOS_ASISTENCIA);
			if (estado == null) {
				info("Registro interrumpido. Puede continuar más tarde desde esta misma opción.");
				return;
			}
			Double nota = null;
			String comentario = null;
			if (!estado.equals(RegistroAsistencia.FALTO)) {
				nota = leerNotaOpcional();
				if (entrada.leerSiNo("¿Agregar un comentario?")) {
					comentario = leerTexto("Comentario");
				}
			}
			sesion.registrarAsistencia(socio, estado, nota, comentario);
		}
		info("Asistencia registrada. Promedio de evaluaciones: "
				+ String.format("%.2f", sesion.calcularPromedioEvaluaciones()));
	}

	private Double leerNotaOpcional() {
		if (!entrada.leerSiNo("¿Evaluar el desempeño (1 a 5)?")) {
			return null;
		}
		while (true) {
			double nota = entrada.leerDecimal("Nota (1 a 5)", 1);
			if (nota <= 5) {
				return nota;
			}
			info("  La nota no puede ser mayor a 5.");
		}
	}

	private void verAnalitica() {
		Socio socio = elegirSocio("¿De qué jugador quiere la analítica?", todosLosSocios());
		if (socio == null) {
			return;
		}
		Date desde = entrada.leerFcha("Desde");
		Date hasta = entrada.leerFcha("Hasta");
		AnaliticaJugador analitica = AnaliticaJugador.calcular(socio, desde, hasta, datos.getSesiones(),
				datos.getPartidos(), datos.getPartidosPractica());
		info("");
		info(analitica.resumen());
	}

}
