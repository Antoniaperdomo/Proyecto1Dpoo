package Consola;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Deportes.Baloncesto;
import Deportes.Categoria;
import Deportes.Deporte;
import Deportes.DeporteConjunto;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.InscripcionIndividual;
import Deportes.Padel;
import Deportes.Tenis;
import Deportes.TenisMesa;
import Deportes.Voleibol;
import Tienda.Articulo;
import Tienda.Bebida;
import Tienda.Producto;
import Tienda.Snack;
import Tienda.Ubicacion;
import Torneos.CatalogoTorneos;
import Torneos.EncuentroTorneo;
import Torneos.InscripcionTorneo;
import Torneos.Torneo;
import Usuario.Administrador;
import Usuario.Empleado;
import Usuario.Entrenador;
import Usuario.Externo;
import Usuario.Participante;
import Usuario.Socio;
import Usuario.Usuario;

public class ConsolaAdmin extends ConsolaBase{
	public static void main(String[] args) {
		new ConsolaAdmin().correr();
	}

	private static final long DIA_MS = 24L * 60 * 60 * 1000;
	private static final String[] DEPORTES_CONJUNTO = { "FUTBOL", "BALONCESTO", "VOLEIBOL" };
	private static final String[] CATEGORIAS = { Categoria.SUB13, Categoria.SUB15, Categoria.SUB17,
			Categoria.MAYORES };

	private static final String[] OPCIONES = { 
			"Turnos de empleados", 
			"Sugerencias de productos", 
			"Socios", 
			"Equipos",
			"Inventario y catálogo de productos", 
			"Reportes de ventas", 
			"Reportes de partidos y rankings",
			"Reportes de entrenamiento y desempeño", 
			"Reportes de instalaciones",
			"Torneo"};

	@Override
	protected String titulo() {
		return "Menú del Administrador";
	}

	@Override
	protected boolean usuarioPermitido(Usuario u) {
		return u instanceof Administrador;
	}

	@Override
	protected String[] opciones() {
		return OPCIONES;
	}

	@Override
	protected void ejecutar(int opcion) {
		switch (opcion) {
		case 1:
			menuTurnos();
			break;
		case 2:
			evaluarSugerencias();
			break;
		case 3:
			menuSocios();
			break;
		case 4:
			menuEquipos();
			break;
		case 5:
			menuInventario();
			break;
		case 6:
			menuReporteVentas();
			break;
		case 7:
			menuReportePartidos();
			break;
		case 8:
			menuReporteEntrenamiento();
			break;
		case 9:
			menuReporteInstalaciones();
			break;
		case 10:
			menuTorneos();
			break;
		}
		
	}


	private Administrador admin() {
		return (Administrador) usuarioActual;
	}

	private <T> T elegir(String titulo, ArrayList<T> items, Function<T, String> descripcion) {
		ArrayList<String> descripciones = new ArrayList<String>();
		for (T item : items) {
			descripciones.add(descripcion.apply(item));
		}
		int pos = entrada.elegirIndice(titulo, descripciones);
		if (pos < 0) {
			return null;
		}
		return items.get(pos);
	}

	private String cargo(Empleado e) {
		return (e instanceof Entrenador) ? "Entrenador" : "Fisioterapeuta";
	}

	private ArrayList<Empleado> empleados() {
		ArrayList<Empleado> lista = new ArrayList<Empleado>();
		for (Usuario u : datos.getUsuarios()) {
			if (u instanceof Empleado) {
				lista.add((Empleado) u);
			}
		}
		return lista;
	}

	private Empleado elegirEmpleado(String titulo) {
		return elegir(titulo, empleados(), e -> e.getNombre() + " (" + cargo(e) + ", id " + e.getId() + ")");
	}

	private String horario(Turno t) {
		return t.getDiaSemana() + " de " + t.getHoraInicio() + ":00 a " + t.getHoraFin() + ":00";
	}

	private int siguienteIdUsuario() {
		int max = 0;
		for (Usuario u : datos.getUsuarios()) {
			if (u.getId() > max) {
				max = u.getId();
			}
		}
		return max + 1;
	}

	private Date finDelDia(Date fecha) {
		return new Date(fecha.getTime() + DIA_MS - 1);
	}

	private ArrayList<Articulo> articulos() {
		ArrayList<Articulo> lista = new ArrayList<Articulo>();
		for (Producto p : datos.getProductos()) {
			if (p instanceof Articulo) {
				lista.add((Articulo) p);
			}
		}
		return lista;
	}

	private void mostrarReporte(String reporte) {
		info("");
		info(reporte.trim());
	}


	private void menuTurnos() {
		String[] ops = { "Evaluar solicitudes de cambio de turno", "Asignar un turno a un empleado",
				"Ver los turnos de los empleados" };
		int op = entrada.elegirOpccion("Turnos de empleados", ops);
		if (op == 1) {
			evaluarSolicitudes();
		} else if (op == 2) {
			asignarTurno();
		} else if (op == 3) {
			verTurnos();
		}
	}

	private void evaluarSolicitudes() {
		ArrayList<SolicitudCambioTurno> pendientes = admin().getSolicitudesPendientes(datos.getSolicitudes());
		SolicitudCambioTurno solicitud = elegir("Solicitudes pendientes", pendientes,
				s -> s.getEmpleado().getNombre() + " (" + cargo(s.getEmpleado()) + "): " + horario(s.getTurnoActual())
						+ "  ->  " + horario(s.getTurnoPropuesto()));
		if (solicitud == null) {
			return;
		}
		admin().evaluarSolicitud(solicitud, datos.getTurnos());
		if (solicitud.getEstado().equals(SolicitudCambioTurno.APROBADA)) {
			info("Solicitud APROBADA. El turno del empleado fue actualizado.");
		} else {
			info("Solicitud RECHAZADA: el nuevo horario se cruza con otro turno del empleado o dejaría el horario "
					+ "actual sin el personal mínimo (2 entrenadores y 1 fisioterapeuta).");
		}
	}

	private void asignarTurno() {
		Empleado empleado = elegirEmpleado("¿A qué empleado le asigna el turno?");
		if (empleado == null) {
			return;
		}
		String dia = elegirTexto("Día de la semana", DIAS);
		if (dia == null) {
			return;
		}
		int inicio = entrada.leerEntero("Hora de inicio", 0, 23);
		int fin = entrada.leerEntero("Hora de fin", 1, 24);
		Turno turno = admin().asignarTurno(empleado, dia, inicio, fin, datos.getTurnos());
		info("Turno asignado a " + empleado.getNombre() + ": " + horario(turno));
	}

	private void verTurnos() {
		boolean hay = false;
		for (Empleado e : empleados()) {
			ArrayList<Turno> turnos = new ArrayList<Turno>();
			for (Turno t : datos.getTurnos()) {
				if (t.getEmpleado().getId() == e.getId()) {
					turnos.add(t);
				}
			}
			if (!turnos.isEmpty()) {
				hay = true;
				info(e.getNombre() + " (" + cargo(e) + "):");
				for (Turno t : turnos) {
					info("  - " + horario(t));
				}
			}
		}
		if (!hay) {
			info("No hay turnos asignados.");
		}
	}


	private void evaluarSugerencias() {
		ArrayList<SugerenciaProducto> pendientes = admin().getSugerenciasPendientes(datos.getSugerencias());
		SugerenciaProducto sugerencia = elegir("Sugerencias pendientes", pendientes,
				s -> s.getNombre() + " (" + s.getCategoria() + ") - sugerido por " + s.getEmpleado().getNombre());
		if (sugerencia == null) {
			return;
		}
		boolean aprobar = entrada.leerSiNo("¿Aprobar la sugerencia \"" + sugerencia.getNombre() + "\"?");
		admin().evaluarSugerencia(sugerencia, aprobar);
		if (aprobar) {
			info("Sugerencia aprobada. Puede agregar el producto en: Inventario y catálogo.");
		} else {
			info("Sugerencia rechazada.");
		}
	}


	private void menuSocios() {
		String[] ops = { "Crear un socio", "Dar de baja a un socio", "Ver la lista de socios" };
		int op = entrada.elegirOpccion("Socios", ops);
		if (op == 1) {
			crearSocio();
		} else if (op == 2) {
			darDeBajaSocio();
		} else if (op == 3) {
			verSocios();
		}
	}

	private void crearSocio() {
		String nombre = entrada.leerTexto("Nombre completo");
		String login = entrada.leerTexto("Login");
		String password = entrada.leerTexto("Contraseña");
		Date nacimiento = entrada.leerFcha("Fecha de nacimiento");
		Socio socio = admin().crearSocio(siguienteIdUsuario(), nombre, login, password, nacimiento,
				datos.getUsuarios());
		info("Socio creado: " + socio.getNombre() + " (id " + socio.getId() + ")");
	}

	private void darDeBajaSocio() {
		Socio socio = elegirSocio("¿Qué socio da de baja?", sociosActivos());
		if (socio == null) {
			return;
		}
		if (!entrada.leerSiNo("¿Confirma dar de baja a " + socio.getNombre() + "?")) {
			return;
		}
		admin().darDeBajaSocio(socio, todosLosSocios());
		info(socio.getNombre() + " fue dado de baja y ya no podrá iniciar sesión.");
	}

	private void verSocios() {
		ArrayList<Socio> socios = todosLosSocios();
		if (socios.isEmpty()) {
			info("No hay socios registrados.");
			return;
		}
		for (Socio s : socios) {
			info(s.getNombre() + " (id " + s.getId() + ") | " + (s.isActivo() ? "activo" : "de baja") + " | puntos "
					+ s.getPuntosFidelidad() + " | equipos " + s.getEquipos().size());
		}
	}


	private void menuEquipos() {
		String[] ops = { "Crear un equipo", "Inscribir un socio en un equipo", "Dar de baja un equipo",
				"Ver los equipos" };
		int op = entrada.elegirOpccion("Equipos", ops);
		if (op == 1) {
			crearEquipo();
		} else if (op == 2) {
			inscribirEnEquipo();
		} else if (op == 3) {
			darDeBajaEquipo();
		} else if (op == 4) {
			verEquipos();
		}
	}

	private ArrayList<Equipo> equiposActivos() {
		ArrayList<Equipo> activos = new ArrayList<Equipo>();
		for (Equipo e : datos.getEquipos()) {
			if (e.isActivo()) {
				activos.add(e);
			}
		}
		return activos;
	}

	private DeporteConjunto crearDeporteConjunto(String nombre) {
		if (nombre.equals("FUTBOL")) {
			return new Futbol();
		} else if (nombre.equals("BALONCESTO")) {
			return new Baloncesto();
		}
		return new Voleibol();
	}

	private void crearEquipo() {
		String nombre = entrada.leerTexto("Nombre del equipo");
		String nombreDeporte = elegirTexto("Deporte", DEPORTES_CONJUNTO);
		if (nombreDeporte == null) {
			return;
		}
		String nombreCategoria = elegirTexto("Categoría", CATEGORIAS);
		if (nombreCategoria == null) {
			return;
		}
		int cupo = entrada.leerEntero("Cupo máximo de jugadores", 1, 50);
		Equipo equipo = admin().crearEquipo(nombre, crearDeporteConjunto(nombreDeporte), cupo,
				new Categoria(nombreCategoria), datos.getEquipos());
		info("Equipo creado: " + equipo.getNombre() + " (" + nombreDeporte + ", " + nombreCategoria + ")");
	}

	private void inscribirEnEquipo() {
		Equipo equipo = elegirEquipo("¿En qué equipo inscribe?", equiposActivos());
		if (equipo == null) {
			return;
		}
		Socio socio = elegirSocio("¿Qué socio inscribe?", sociosActivos());
		if (socio == null) {
			return;
		}
		String posicion = entrada.leerTexto("Posición");
		int camiseta = entrada.leerEntero("Número de camiseta", 1, 99);
		admin().inscribirEnEquipo(socio, equipo, posicion, camiseta, datos.getEquipos());
		socio.getEquipos().add(equipo);
		info(socio.getNombre() + " inscrito en " + equipo.getNombre() + " con la camiseta " + camiseta + ".");
	}

	private void darDeBajaEquipo() {
		Equipo equipo = elegirEquipo("¿Qué equipo da de baja?", equiposActivos());
		if (equipo == null) {
			return;
		}
		if (!entrada.leerSiNo("¿Confirma dar de baja el equipo " + equipo.getNombre() + "?")) {
			return;
		}
		admin().darDeBajaEquipo(equipo);
		info("Equipo dado de baja.");
	}

	private void verEquipos() {
		if (datos.getEquipos().isEmpty()) {
			info("No hay equipos registrados.");
			return;
		}
		for (Equipo e : datos.getEquipos()) {
			info(e.getNombre() + " | " + e.getDeporte().getNombre() + " | " + e.getCategoria().getNombre()
					+ " | jugadores " + e.getJugadores().size() + "/" + e.getCupoMax() + " | "
					+ (e.isActivo() ? "activo" : "de baja"));
		}
	}


	private void menuInventario() {
		String[] ops = { "Ver el estado del inventario", "Reabastecer un artículo", "Mover un artículo entre ubicaciones",
				"Agregar un producto al catálogo", "Crear una ubicación" };
		int op = entrada.elegirOpccion("Inventario y catálogo", ops);
		if (op == 1) {
			mostrarReporte(admin().reporteEstadoInventario(datos.getInventario()));
		} else if (op == 2) {
			reabastecer();
		} else if (op == 3) {
			moverArticulo();
		} else if (op == 4) {
			agregarProducto();
		} else if (op == 5) {
			crearUbicacion();
		}
	}

	private Ubicacion elegirUbicacion(String titulo) {
		return elegir(titulo, datos.getUbicaciones(), u -> u.getNombre());
	}

	private Articulo elegirArticulo(String titulo) {
		return elegir(titulo, articulos(), a -> a.getNombre() + " - $" + a.getPrecio());
	}

	private void reabastecer() {
		Articulo articulo = elegirArticulo("¿Qué artículo reabastece?");
		if (articulo == null) {
			return;
		}
		Ubicacion ubicacion = elegirUbicacion("¿En qué ubicación?");
		if (ubicacion == null) {
			return;
		}
		int cantidad = entrada.leerEntero("Cantidad a agregar", 1, 100000);
		admin().reabastecer(articulo, ubicacion, cantidad, datos.getInventario().getExistencias());
		info("Ahora hay " + datos.getInventario().consultarDisponible(articulo, ubicacion) + " unidades de "
				+ articulo.getNombre() + " en " + ubicacion.getNombre() + ".");
	}

	private void moverArticulo() {
		Articulo articulo = elegirArticulo("¿Qué artículo mueve?");
		if (articulo == null) {
			return;
		}
		Ubicacion origen = elegirUbicacion("Ubicación de origen");
		if (origen == null) {
			return;
		}
		Ubicacion destino = elegirUbicacion("Ubicación de destino");
		if (destino == null) {
			return;
		}
		int cantidad = entrada.leerEntero("Cantidad a mover", 1, 100000);
		admin().moverArticulo(articulo, origen, destino, cantidad, datos.getInventario().getExistencias());
		info("Movidas " + cantidad + " unidades de " + articulo.getNombre() + ": " + origen.getNombre() + " -> "
				+ destino.getNombre());
	}

	private void agregarProducto() {
		String[] tipos = { "Artículo de la tienda", "Bebida de la cafetería", "Snack de la cafetería" };
		String tipo = elegirTexto("Tipo de producto", tipos);
		if (tipo == null) {
			return;
		}
		String nombre = entrada.leerTexto("Nombre del producto");
		if (datos.buscarProducto(nombre) != null) {
			info("Ya existe un producto con ese nombre.");
			return;
		}
		double precio = entrada.leerDecimal("Precio", 1);
		Producto producto;
		if (tipo.equals(tipos[0])) {
			String categoria = entrada.leerTexto("Categoría (ej. Ropa, Equipo)");
			producto = new Articulo(nombre, precio, categoria);
		} else if (tipo.equals(tipos[1])) {
			String temperatura = elegirTexto("Tipo de bebida", new String[] { Bebida.FRIA, Bebida.CALIENTE });
			if (temperatura == null) {
				return;
			}
			producto = new Bebida(nombre, precio, temperatura);
		} else {
			ArrayList<String> alergenos = new ArrayList<String>();
			while (entrada.leerSiNo("¿Agregar un alérgeno?")) {
				alergenos.add(entrada.leerTexto("Alérgeno"));
			}
			producto = new Snack(nombre, precio, alergenos);
		}
		datos.getProductos().add(producto);
		info("Producto agregado al catálogo: " + producto.getNombre());
	}

	private void crearUbicacion() {
		String nombre = entrada.leerTexto("Nombre de la ubicación");
		if (datos.buscarUbicacion(nombre) != null) {
			info("Ya existe una ubicación con ese nombre.");
			return;
		}
		datos.getUbicaciones().add(new Ubicacion(nombre));
		info("Ubicación creada: " + nombre);
	}


	private void menuReporteVentas() {
		String[] ops = { "Ventas entre dos fechas", "Ventas por periodo (diario, semanal, mensual)" };
		int op = entrada.elegirOpccion("Reportes de ventas", ops);
		if (op == 1) {
			Date desde = entrada.leerFcha("Desde");
			Date hasta = finDelDia(entrada.leerFcha("Hasta"));
			mostrarReporte(admin().reporteVentas(datos.getRegistroVentas().getVentas(), desde, hasta));
			mostrarRubros(desde, hasta);
		} else if (op == 2) {
			String[] periodos = { Administrador.DIARIO, Administrador.SEMANAL, Administrador.MENSUAL };
			String periodo = elegirTexto("Periodo", periodos);
			if (periodo == null) {
				return;
			}
			Date referencia = finDelDia(entrada.leerFcha("Fecha de referencia (fin del periodo)"));
			mostrarReporte(admin().reporteVentasPorPeriodo(datos.getRegistroVentas().getVentas(), referencia, periodo));
		}
	}

	private void mostrarRubros(Date desde, Date hasta) {
		java.util.Map<String, Double> rubros = datos.getRegistroVentas().totalesPorRubro(desde, hasta);
		if (rubros.isEmpty()) {
			return;
		}
		info("TOTALES POR RUBRO");
		for (java.util.Map.Entry<String, Double> e : rubros.entrySet()) {
			info("  " + e.getKey() + ": $" + String.format("%.2f", e.getValue()));
		}
	}


	private void menuReportePartidos() {
		String[] ops = { "Resultados de los partidos", "Evolución del ranking de una disciplina",
				"Historial de partidos" };
		int op = entrada.elegirOpccion("Partidos y rankings", ops);
		if (op == 1) {
			mostrarReporte(admin().reporteResultadosPartidos(datos.getPartidos()));
		} else if (op == 2) {
			reporteRanking();
		} else if (op == 3) {
			mostrarReporte(admin().historialPartidos(datos.getPartidos(), datos.getPartidosPractica()));
		}
	}

	private DeporteIndividual deporteIndividual(String disciplina) {
		if (disciplina.equals("TENIS")) {
			return new Tenis(DeporteIndividual.PRINCIPIANTE);
		} else if (disciplina.equals("PADEL")) {
			return new Padel(DeporteIndividual.PRINCIPIANTE);
		}
		return new TenisMesa(DeporteIndividual.PRINCIPIANTE);
	}

	private void reporteRanking() {
		String disciplina = elegirTexto("Disciplina", DISCIPLINAS);
		if (disciplina == null) {
			return;
		}
		ArrayList<InscripcionIndividual> inscripciones = new ArrayList<InscripcionIndividual>();
		for (Socio s : todosLosSocios()) {
			inscripciones.addAll(s.getInscripciones());
		}
		mostrarReporte(admin().reporteEvolucionRanking(inscripciones, deporteIndividual(disciplina)));
	}


	private void menuReporteEntrenamiento() {
		String[] ops = { "Asistencia a entrenamientos entre dos fechas", "Desempeño de un jugador",
				"Desempeño de todos los socios activos", "Historial de sesiones de entrenamiento" };
		int op = entrada.elegirOpccion("Entrenamiento y desempeño", ops);
		if (op == 1) {
			Date desde = entrada.leerFcha("Desde");
			Date hasta = finDelDia(entrada.leerFcha("Hasta"));
			mostrarReporte(admin().reporteAsistenciaEntrenamientos(datos.getSesiones(), desde, hasta));
		} else if (op == 2) {
			Socio socio = elegirSocio("¿De qué jugador quiere el reporte?", todosLosSocios());
			if (socio == null) {
				return;
			}
			Date desde = entrada.leerFcha("Desde");
			Date hasta = finDelDia(entrada.leerFcha("Hasta"));
			mostrarReporte(admin().reporteDesempenoJugador(socio, desde, hasta, datos.getSesiones(),
					datos.getPartidos(), datos.getPartidosPractica()));
		} else if (op == 3) {
			Date desde = entrada.leerFcha("Desde");
			Date hasta = finDelDia(entrada.leerFcha("Hasta"));
			mostrarReporte(admin().reporteDesempenoSocios(todosLosSocios(), desde, hasta, datos.getSesiones(),
					datos.getPartidos(), datos.getPartidosPractica()));
		} else if (op == 4) {
			mostrarReporte(admin().historialSesiones(datos.getSesiones()));
		}
	}


	private void menuReporteInstalaciones() {
		String[] ops = { "Estado de las instalaciones en una fecha y hora", "Historial de reservas" };
		int op = entrada.elegirOpccion("Instalaciones", ops);
		if (op == 1) {
			Date fecha = entrada.leerFcha("Fecha");
			int hora = entrada.leerEntero("Hora del bloque", 0, 23);
			mostrarReporte(admin().reporteEstadoInstalaciones(datos.getInstalaciones(), fecha,
					bloqueHorario(fecha, hora)));
		} else if (op == 2) {
			mostrarReporte(admin().historialReservas(datos.getInstalaciones()));
		}
	}
	
	// Torneos (HU16 a HU21)

		private static final String[] TIPOS_TORNEO = { "RECREATIVO", "COMPETITIVO" };
		private static final String[] DEPORTES_TORNEO = { "FUTBOL", "BALONCESTO", "VOLEIBOL", "TENIS", "PADEL",
				"TENISDEMESA" };
		private static final String[] NIVELES = { DeporteIndividual.PRINCIPIANTE, DeporteIndividual.INTERMEDIO,
				DeporteIndividual.AVANZADO };
		private static final String[] MODALIDADES = { Torneo.SENCILLOS, Torneo.DOBLES };
		private static final String TODOS = "TODOS";

		private void menuTorneos() {
			String[] ops = { "Crear un torneo", "Ver los torneos y su historial", "Iniciar un torneo",
					"Registrar el resultado de un encuentro", "Finalizar un torneo", "Registrar un participante externo",
					"Inscribir a un externo en un torneo", "Retirar a un externo de un torneo" };
			int op = entrada.elegirOpccion("Torneos", ops);
			if (op == 1) {
				crearTorneo();
			} else if (op == 2) {
				verTorneos();
			} else if (op == 3) {
				iniciarTorneo();
			} else if (op == 4) {
				registrarResultadoEncuentro();
			} else if (op == 5) {
				finalizarTorneo();
			} else if (op == 6) {
				registrarExterno();
			} else if (op == 7) {
				inscribirExterno();
			} else if (op == 8) {
				retirarExterno();
			}
		}

		private ArrayList<Torneo> torneosEnEstado(String estado) {
			return CatalogoTorneos.filtrar(datos.getTorneos(), estado, null, null);
		}

		private Torneo elegirTorneo(String titulo, ArrayList<Torneo> candidatos) {
			return elegir(titulo, candidatos, t -> CatalogoTorneos.resumen(t));
		}

		// HU16
		private void crearTorneo() {
			String tipo = elegirTexto("Tipo de torneo", TIPOS_TORNEO);
			if (tipo == null) {
				return;
			}
			String nombre = entrada.leerTexto("Nombre del torneo");
			String nombreDeporte = elegirTexto("Deporte o disciplina", DEPORTES_TORNEO);
			if (nombreDeporte == null) {
				return;
			}
			Deporte deporte;
			Categoria categoria = null;
			String nivel = null;
			String modalidad = null;
			if (Arrays.asList(DISCIPLINAS).contains(nombreDeporte)) {
				deporte = deporteIndividual(nombreDeporte);
				nivel = elegirTexto("Nivel del torneo", NIVELES);
				if (nivel == null) {
					return;
				}
				modalidad = elegirTexto("Modalidad", MODALIDADES);
				if (modalidad == null) {
					return;
				}
			} else {
				deporte = crearDeporteConjunto(nombreDeporte);
				String nombreCategoria = elegirTexto("Categoría", CATEGORIAS);
				if (nombreCategoria == null) {
					return;
				}
				categoria = new Categoria(nombreCategoria);
			}
			int cupos = entrada.leerEntero("Cupos totales", 2, 1000);
			Date inicio = entrada.leerFcha("Fecha de inicio del torneo");
			Date limite = entrada.leerFcha("Fecha límite de reserva (hasta ahí hay cupos preferenciales)");
			Torneo torneo;
			if (tipo.equals("RECREATIVO")) {
				double tarifa = entrada.leerDecimal("Tarifa de inscripción (0 si es gratis)", 0);
				int bono = entrada.leerEntero("Porcentaje del bono de descuento para el ganador", 1, 100);
				torneo = admin().crearTorneoRecreativo(datos.getTorneos(), nombre, deporte, categoria, nivel, modalidad,
						cupos, inicio, limite, tarifa, bono);
			} else {
				double tarifa = entrada.leerDecimal("Tarifa de inscripción", 1);
				torneo = admin().crearTorneoCompetitivo(datos.getTorneos(), nombre, deporte, categoria, nivel, modalidad,
						cupos, inicio, limite, tarifa);
			}
			info("Torneo creado:");
			info(CatalogoTorneos.resumen(torneo));
		}

		// HU17
		private void verTorneos() {
			String[] filtros = { TODOS, Torneo.ABIERTO, Torneo.EN_CURSO, Torneo.FINALIZADO };
			String filtro = elegirTexto("¿Qué torneos quiere ver?", filtros);
			if (filtro == null) {
				return;
			}
			ArrayList<Torneo> lista = torneosEnEstado(filtro.equals(TODOS) ? null : filtro);
			mostrarReporte(CatalogoTorneos.listar(lista));
			if (lista.isEmpty() || !entrada.leerSiNo("¿Ver el historial detallado de alguno?")) {
				return;
			}
			Torneo torneo = elegirTorneo("Torneo", lista);
			if (torneo != null) {
				mostrarReporte(admin().historialTorneo(datos.getTorneos(), torneo.getId()));
			}
		}

		// HU18
		private void iniciarTorneo() {
			Torneo torneo = elegirTorneo("¿Qué torneo inicia?", torneosEnEstado(Torneo.ABIERTO));
			if (torneo == null) {
				return;
			}
			info("Inscritos: " + torneo.getInscripciones().size() + " de " + torneo.getCupos() + " cupos.");
			if (!entrada.leerSiNo("Al iniciar se cierran las inscripciones. ¿Continuar?")) {
				return;
			}
			admin().iniciarTorneo(torneo);
			info("Torneo iniciado. Encuentros generados: " + torneo.getEncuentros().size());
		}

		// HU19
		private void registrarResultadoEncuentro() {
			Torneo torneo = elegirTorneo("¿En qué torneo registra el resultado?", torneosEnEstado(Torneo.EN_CURSO));
			if (torneo == null) {
				return;
			}
			ArrayList<EncuentroTorneo> pendientes = new ArrayList<EncuentroTorneo>();
			for (EncuentroTorneo e : torneo.getEncuentros()) {
				if (!e.isJugado()) {
					pendientes.add(e);
				}
			}
			EncuentroTorneo encuentro = elegir("Encuentros sin resultado", pendientes, e -> e.toString());
			if (encuentro == null) {
				return;
			}
			int local = entrada.leerEntero("Marcador de " + encuentro.getLocal().getNombre(), 0, 999);
			int visitante = entrada.leerEntero("Marcador de " + encuentro.getVisitante().getNombre(), 0, 999);
			admin().registrarResultadoEncuentro(torneo, encuentro, local, visitante);
			info("Resultado registrado. Encuentros por jugar: " + torneo.encuentrosFaltantes());
		}

		// HU18
		private void finalizarTorneo() {
			Torneo torneo = elegirTorneo("¿Qué torneo finaliza?", torneosEnEstado(Torneo.EN_CURSO));
			if (torneo == null) {
				return;
			}
			admin().finalizarTorneo(torneo);
			info("Torneo finalizado.");
			mostrarReporte(admin().historialTorneo(datos.getTorneos(), torneo.getId()));
		}

		private ArrayList<Externo> externos() {
			ArrayList<Externo> lista = new ArrayList<Externo>();
			for (Usuario u : datos.getUsuarios()) {
				if (u instanceof Externo) {
					lista.add((Externo) u);
				}
			}
			return lista;
		}

		private Externo elegirExterno(String titulo, ArrayList<Externo> candidatos) {
			return elegir(titulo, candidatos, x -> x.getNombre() + " (id " + x.getId() + ")");
		}

		// HU20
		private void registrarExterno() {
			String nombre = entrada.leerTexto("Nombre completo");
			String login = entrada.leerTexto("Login");
			String password = entrada.leerTexto("Contraseña");
			Date nacimiento = entrada.leerFcha("Fecha de nacimiento");
			boolean estudiante = entrada.leerSiNo("¿Es estudiante de la universidad?");
			Externo externo = admin().registrarExterno(datos.getUsuarios(), siguienteIdUsuario(), nombre, login, password,
					nacimiento, estudiante);
			info("Participante externo registrado: " + externo.getNombre() + " (id " + externo.getId() + ")");
		}

		private ArrayList<Torneo> torneosIndividualesAbiertos() {
			ArrayList<Torneo> lista = new ArrayList<Torneo>();
			for (Torneo t : torneosEnEstado(Torneo.ABIERTO)) {
				if (t.esIndividual()) {
					lista.add(t);
				}
			}
			return lista;
		}

		// HU21
		private void inscribirExterno() {
			Torneo torneo = elegirTorneo("¿En qué torneo lo inscribe?", torneosIndividualesAbiertos());
			if (torneo == null) {
				return;
			}
			Externo externo = elegirExterno("¿Qué externo inscribe?", externos());
			if (externo == null) {
				return;
			}
			String nivel = elegirTexto("Nivel declarado por el externo", NIVELES);
			if (nivel == null) {
				return;
			}
			Participante companero = null;
			if (Torneo.DOBLES.equals(torneo.getModalidad())) {
				ArrayList<Participante> posibles = new ArrayList<Participante>();
				posibles.addAll(sociosActivos());
				for (Externo x : externos()) {
					if (x.getId() != externo.getId()) {
						posibles.add(x);
					}
				}
				companero = elegir("¿Quién es su compañero?", posibles, p -> p.getNombre() + " (id " + p.getId() + ")");
				if (companero == null) {
					return;
				}
			}
			InscripcionTorneo inscripcion = admin().inscribirExterno(torneo, externo, nivel, companero, datos.getUsuarios(),
					todosLosSocios(), new Date());
			info("Inscripción realizada en cupo " + inscripcion.getTipoCupo().toLowerCase() + ". Tarifa: $"
					+ inscripcion.getTarifaPagada());
		}

		// HU21
		private void retirarExterno() {
			Torneo torneo = elegirTorneo("¿De qué torneo lo retira?", torneosIndividualesAbiertos());
			if (torneo == null) {
				return;
			}
			ArrayList<Externo> inscritos = new ArrayList<Externo>();
			for (Externo x : externos()) {
				if (torneo.buscarInscripcion(x) != null) {
					inscritos.add(x);
				}
			}
			Externo externo = elegirExterno("¿Qué externo retira?", inscritos);
			if (externo == null) {
				return;
			}
			InscripcionTorneo retirada = admin().retirarExterno(torneo, externo);
			info(torneo.mensajeRetiro(retirada));
		}

}
