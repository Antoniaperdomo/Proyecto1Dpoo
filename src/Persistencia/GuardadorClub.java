package Persistencia;

import static Persistencia.Formato.esc;
import static Persistencia.Formato.fecha;
import static Persistencia.Formato.linea;
import static Persistencia.Formato.lista;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Competencias.ASCUN;
import Competencias.Competencia;
import Competencias.NominaCompetencia;
import Competencias.TorneoCerros;
import Deportes.Equipo;
import Deportes.EstadisticaBaloncesto;
import Deportes.EstadisticaFutbol;
import Deportes.EstadisticaJugadorPartido;
import Deportes.EstadisticaPractica;
import Deportes.EstadisticaTenisDeMesa;
import Deportes.EstadisticaVoleibol;
import Deportes.EstadisticasTenisYPadel;
import Deportes.InscripcionIndividual;
import Deportes.Instalacion;
import Deportes.JugadorEquipo;
import Deportes.CanchaPadel;
import Deportes.CanchaTenis;
import Deportes.MesaTenisDeMesa;
import Deportes.Partido;
import Deportes.PartidoCompetencia;
import Deportes.PartidoInterno;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;
import Deportes.Suspension;
import Entrenamiento.RegistroAsistencia;
import Entrenamiento.SesionEntrenamiento;
import Tienda.Articulo;
import Tienda.Bebida;
import Tienda.ExistenciaArticulo;
import Tienda.ItemVenta;
import Tienda.Producto;
import Tienda.Snack;
import Tienda.Ubicacion;
import Tienda.Venta;
import Tienda.VentaCafeteria;
import Tienda.VentaTienda;
import Usuario.Administrador;
import Usuario.Empleado;
import Usuario.Entrenador;
import Usuario.Fisioterapeuta;
import Usuario.Socio;
import Usuario.Usuario;

/**
 * Convierte el estado en memoria ({@link DatosClub}) en archivos de texto.
 * Cada archivo se escribe primero a un temporal y luego se renombra, para no
 * dejar un archivo a medias si algo falla en la mitad de la escritura.
 */
public class GuardadorClub {

	public static void guardar(DatosClub d, Path carpeta) throws IOException {
		Files.createDirectories(carpeta);

		usuarios(d, carpeta);
		ubicaciones(d, carpeta);
		productos(d, carpeta);
		existencias(d, carpeta);
		equipos(d, carpeta);
		inscripciones(d, carpeta);
		instalaciones(d, carpeta);
		reservas(d, carpeta);
		partidosPractica(d, carpeta);
		competencias(d, carpeta);
		partidos(d, carpeta);
		sesiones(d, carpeta);
		turnos(d, carpeta);
		sugerencias(d, carpeta);
		ventas(d, carpeta);
	}


	// Usuarios

	private static void usuarios(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (Usuario u : d.getUsuarios()) {
			String tipo;
			String contratacion = "";
			String puntos = "";
			String activo = "";
			if (u instanceof Administrador) {
				tipo = "ADMINISTRADOR";
			} else if (u instanceof Socio) {
				tipo = "SOCIO";
				puntos = String.valueOf(((Socio) u).getPuntosFidelidad());
				activo = String.valueOf(((Socio) u).isActivo());
			} else if (u instanceof Entrenador) {
				tipo = "ENTRENADOR";
				contratacion = fecha(((Entrenador) u).getFechaContratacion());
				puntos = String.valueOf(((Entrenador) u).getPuntosFidelidad());
			} else if (u instanceof Fisioterapeuta) {
				tipo = "FISIOTERAPEUTA";
				contratacion = fecha(((Fisioterapeuta) u).getFechaContratacion());
				puntos = String.valueOf(((Fisioterapeuta) u).getPuntosFidelidad());
			} else {
				throw new IOException("Tipo de usuario no soportado: " + u.getClass().getName());
			}
			filas.add(linea(tipo, esc(u.getId()), esc(u.getNombre()), esc(u.getLogin()), esc(u.getPassword()),
					fecha(u.getFechaNacimiento()), contratacion, puntos, activo));
		}
		escribir(carpeta, PersistenciaClub.USUARIOS,
				"tipo;id;nombre;login;password;fechaNacimiento;fechaContratacion;puntosFidelidad;activo", filas);
	}

	// Tienda: ubicaciones, productos, existencias

	private static void ubicaciones(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (Ubicacion u : d.getUbicaciones()) {
			filas.add(linea(esc(u.getNombre())));
		}
		escribir(carpeta, PersistenciaClub.UBICACIONES, "nombre", filas);
	}

	private static void productos(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (Producto p : d.getProductos()) {
			String tipo;
			String extra;
			if (p instanceof Articulo) {
				tipo = "ARTICULO";
				extra = esc(((Articulo) p).getCategoria());
			} else if (p instanceof Bebida) {
				tipo = "BEBIDA";
				extra = esc(((Bebida) p).getTipo());
			} else if (p instanceof Snack) {
				tipo = "SNACK";
				extra = lista(((Snack) p).getAlergenos());
			} else {
				throw new IOException("Tipo de producto no soportado: " + p.getClass().getName());
			}
			filas.add(linea(tipo, esc(p.getNombre()), String.valueOf(p.getPrecio()), extra));
		}
		escribir(carpeta, PersistenciaClub.PRODUCTOS, "tipo;nombre;precio;categoria|tipoBebida|alergenos", filas);
	}

	private static void existencias(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (ExistenciaArticulo e : d.getInventario().getExistencias()) {
			filas.add(linea(esc(e.getArticulo().getNombre()), esc(e.getUbicacion().getNombre()),
					String.valueOf(e.getCantidadDisponible())));
		}
		escribir(carpeta, PersistenciaClub.EXISTENCIAS, "articulo;ubicacion;cantidad", filas);
	}

	// Equipos

	private static void equipos(DatosClub d, Path carpeta) throws IOException {
		List<String> filasEquipos = new ArrayList<String>();
		List<String> filasJugadores = new ArrayList<String>();
		List<String> filasSuspensiones = new ArrayList<String>();
		for (Equipo e : d.getEquipos()) {
			filasEquipos.add(linea(esc(e.getNombre()), esc(e.getDeporte().getNombre()),
					esc(e.getCategoria().getNombre()), String.valueOf(e.getCupoMax()),
					String.valueOf(e.isActivo())));
			for (JugadorEquipo j : e.getJugadores()) {
				filasJugadores.add(linea(esc(e.getNombre()), esc(j.getSocio().getId()), esc(j.getPosiccion()),
						String.valueOf(j.getNumeroCamiseta()), String.valueOf(j.getAmarillas(Suspension.INTERNO)),
						String.valueOf(j.getAmarillas(Suspension.COMPETENCIA))));
			}
			for (Suspension s : e.getSuspensiones()) {
				filasSuspensiones.add(linea(esc(e.getNombre()), esc(s.getJugador().getId()),
						esc(s.getMotivoSuspension()), esc(s.getAmbito()), String.valueOf(s.isCumplida())));
			}
		}
		escribir(carpeta, PersistenciaClub.EQUIPOS, "nombre;deporte;categoria;cupoMax;activo", filasEquipos);
		escribir(carpeta, PersistenciaClub.JUGADORES_EQUIPO,
				"equipo;socioId;posicion;numeroCamiseta;amarillasInternas;amarillasCompetencia", filasJugadores);
		escribir(carpeta, PersistenciaClub.SUSPENSIONES, "equipo;socioId;motivo;ambito;cumplida", filasSuspensiones);
	}

	// ------------------------------------------------------------------
	// Deportes individuales
	// ------------------------------------------------------------------
	private static void inscripciones(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (Usuario u : d.getUsuarios()) {
			if (u instanceof Socio) {
				for (InscripcionIndividual i : ((Socio) u).getInscripciones()) {
					filas.add(linea(esc(u.getId()), esc(i.getDeporte().getNombre()),
							String.valueOf(i.getRankingELO()), lista(i.getHistorialElo())));
				}
			}
		}
		escribir(carpeta, PersistenciaClub.INSCRIPCIONES, "socioId;disciplina;rankingELO;historialELO", filas);
	}

	private static void instalaciones(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (Instalacion i : d.getInstalaciones()) {
			String tipo;
			String techada = "";
			if (i instanceof CanchaTenis) {
				tipo = "TENIS";
			} else if (i instanceof CanchaPadel) {
				tipo = "PADEL";
			} else if (i instanceof MesaTenisDeMesa) {
				tipo = "TENISDEMESA";
				techada = String.valueOf(((MesaTenisDeMesa) i).isTechada());
			} else {
				throw new IOException("Tipo de instalación no soportado: " + i.getClass().getName());
			}
			filas.add(linea(tipo, esc(i.getNombre()), techada));
		}
		escribir(carpeta, PersistenciaClub.INSTALACIONES, "tipo;nombre;techada", filas);
	}

	private static void reservas(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (Instalacion i : d.getInstalaciones()) {
			for (ReservaInstalacion r : i.getReservas()) {
				filas.add(linea(esc(r.getSocio().getId()), esc(i.getNombre()), fecha(r.getFecha()),
						fecha(r.getBloqueHorario()), esc(r.getModalidad()), String.valueOf(r.getNumJugadores())));
			}
		}
		escribir(carpeta, PersistenciaClub.RESERVAS, "socioId;instalacion;fecha;bloqueHorario;modalidad;numJugadores",
				filas);
	}

	private static void partidosPractica(DatosClub d, Path carpeta) throws IOException {
		List<String> filasPartidos = new ArrayList<String>();
		List<String> filasEstadisticas = new ArrayList<String>();
		int id = 1;
		for (PartidoPractica p : d.getPartidosPractica()) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (Socio s : p.getJugadoresParticipantes()) {
				ids.add(s.getId());
			}
			filasPartidos.add(linea(String.valueOf(id), fecha(p.getFecha()), esc(p.getDisciplina()),
					esc(p.getInstalacion().getNombre()), String.valueOf(p.isProgramadoPorEntrenador()),
					String.valueOf(p.isFinalizado()), lista(ids)));

			// HashMap no garantiza orden: se ordena por id para que el archivo sea estable
			ArrayList<Map.Entry<Socio, EstadisticaPractica>> entradas = new ArrayList<Map.Entry<Socio, EstadisticaPractica>>(
					p.getEstadisticasPorJugador().entrySet());
			Collections.sort(entradas, new Comparator<Map.Entry<Socio, EstadisticaPractica>>() {
				public int compare(Map.Entry<Socio, EstadisticaPractica> x, Map.Entry<Socio, EstadisticaPractica> y) {
					return Integer.compare(x.getKey().getId(), y.getKey().getId());
				}
			});
			for (Map.Entry<Socio, EstadisticaPractica> e : entradas) {
				EstadisticaPractica est = e.getValue();
				int games = 0, aces = 0, errores = 0, puntos = 0;
				if (est instanceof EstadisticasTenisYPadel) {
					EstadisticasTenisYPadel t = (EstadisticasTenisYPadel) est;
					games = t.getGamesGanados();
					aces = t.getAces();
					errores = t.getErroresNoForzados();
				} else if (est instanceof EstadisticaTenisDeMesa) {
					puntos = ((EstadisticaTenisDeMesa) est).getPuntosTotales();
				} else {
					throw new IOException("Tipo de estadística no soportado: " + est.getClass().getName());
				}
				filasEstadisticas.add(linea(String.valueOf(id), esc(e.getKey().getId()),
						String.valueOf(est.getSetsGanados()), String.valueOf(games), String.valueOf(aces),
						String.valueOf(errores), String.valueOf(puntos)));
			}
			id++;
		}
		escribir(carpeta, PersistenciaClub.PARTIDOS_PRACTICA,
				"id;fecha;disciplina;instalacion;programadoPorEntrenador;finalizado;jugadores", filasPartidos);
		escribir(carpeta, PersistenciaClub.ESTADISTICAS_PRACTICA,
				"partidoId;socioId;setsGanados;gamesGanados;aces;erroresNoForzados;puntosTotales", filasEstadisticas);
	}


	// Competencias

	private static void competencias(DatosClub d, Path carpeta) throws IOException {
		List<String> filasCompetencias = new ArrayList<String>();
		List<String> filasTabla = new ArrayList<String>();
		for (Competencia c : d.getCompetencias()) {
			String tipo;
			String fase = "";
			if (c instanceof TorneoCerros) {
				tipo = "TORNEOCERROS";
				ArrayList<Map.Entry<Equipo, Integer>> puntos = new ArrayList<Map.Entry<Equipo, Integer>>(
						((TorneoCerros) c).getTablaPosiciones().getPuntosPorEquipo().entrySet());
				Collections.sort(puntos, new Comparator<Map.Entry<Equipo, Integer>>() {
					public int compare(Map.Entry<Equipo, Integer> x, Map.Entry<Equipo, Integer> y) {
						return x.getKey().getNombre().compareTo(y.getKey().getNombre());
					}
				});
				for (Map.Entry<Equipo, Integer> e : puntos) {
					filasTabla.add(linea(esc(c.getNombre()), esc(e.getKey().getNombre()),
							String.valueOf(e.getValue())));
				}
			} else if (c instanceof ASCUN) {
				tipo = "ASCUN";
				fase = esc(((ASCUN) c).getFaseActual());
			} else {
				throw new IOException("Tipo de competencia no soportado: " + c.getClass().getName());
			}
			filasCompetencias.add(linea(tipo, esc(c.getNombre()), esc(c.getEntidadOrganizadora()),
					esc(c.getTemporadaOAño()), esc(c.getDeporte().getNombre()), esc(c.getCategoria().getNombre()),
					fase));
		}
		List<String> filasNominas = new ArrayList<String>();
		for (NominaCompetencia n : d.getNominas()) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (Socio s : n.getJugadoresHabilitados()) {
				ids.add(s.getId());
			}
			filasNominas.add(linea(esc(n.getCompetencia().getNombre()), esc(n.getEquipo().getNombre()), lista(ids)));
		}
		escribir(carpeta, PersistenciaClub.COMPETENCIAS,
				"tipo;nombre;entidadOrganizadora;temporada;deporte;categoria;faseActual", filasCompetencias);
		escribir(carpeta, PersistenciaClub.NOMINAS, "competencia;equipo;jugadoresHabilitados", filasNominas);
		escribir(carpeta, PersistenciaClub.TABLA_POSICIONES, "competencia;equipo;puntos", filasTabla);
	}

	// Partidos de equipos (internos y de competencia)

	private static void partidos(DatosClub d, Path carpeta) throws IOException {
		List<String> filasPartidos = new ArrayList<String>();
		List<String> filasEstadisticas = new ArrayList<String>();
		int id = 1;
		for (Partido p : d.getPartidos()) {
			String tipo;
			String equipo2 = "";
			String institucion = "";
			String escenario = "";
			String competencia = "";
			if (p instanceof PartidoInterno) {
				tipo = "INTERNO";
				equipo2 = esc(((PartidoInterno) p).getEquipo2().getNombre());
			} else if (p instanceof PartidoCompetencia) {
				PartidoCompetencia pc = (PartidoCompetencia) p;
				tipo = "COMPETENCIA";
				institucion = esc(pc.getInstitucion().getNombre());
				escenario = esc(pc.getEscenarioJugador());
				if (pc.getCompetencia() != null) {
					competencia = esc(pc.getCompetencia().getNombre());
				}
			} else {
				throw new IOException("Tipo de partido no soportado: " + p.getClass().getName());
			}
			filasPartidos.add(linea(String.valueOf(id), tipo, fecha(p.getFecha()), esc(p.getDeeporte().getNombre()),
					esc(p.getEquipo1().getNombre()), equipo2, institucion, escenario, competencia,
					String.valueOf(p.getMarcadorEquipo1()), String.valueOf(p.getMarcadorEquipo2()),
					String.valueOf(p.isJugado())));

			for (EstadisticaJugadorPartido est : p.getEstadisticas()) {
				String a, b, c = "0", e = "0";
				if (est instanceof EstadisticaFutbol) {
					EstadisticaFutbol f = (EstadisticaFutbol) est;
					a = String.valueOf(f.getGoles());
					b = String.valueOf(f.getAsistencias());
					c = String.valueOf(f.getTarjetasAmarillas());
					e = String.valueOf(f.getTarjetasRojas());
				} else if (est instanceof EstadisticaBaloncesto) {
					EstadisticaBaloncesto bl = (EstadisticaBaloncesto) est;
					a = String.valueOf(bl.getPuntos());
					b = String.valueOf(bl.getRebotes());
				} else if (est instanceof EstadisticaVoleibol) {
					EstadisticaVoleibol v = (EstadisticaVoleibol) est;
					a = String.valueOf(v.getAces());
					b = String.valueOf(v.getBloqueos());
					c = String.valueOf(v.getSets());
				} else {
					throw new IOException("Tipo de estadística no soportado: " + est.getClass().getName());
				}
				filasEstadisticas.add(linea(String.valueOf(id), esc(est.getJugador().getId()), a, b, c, e));
			}
			id++;
		}
		escribir(carpeta, PersistenciaClub.PARTIDOS,
				"id;tipo;fecha;deporte;equipo1;equipo2;institucionRival;escenario;competencia;marcador1;marcador2;jugado",
				filasPartidos);
		escribir(carpeta, PersistenciaClub.ESTADISTICAS_PARTIDO,
				"partidoId;socioId;v1;v2;v3;v4  (futbol: goles,asistencias,amarillas,rojas | baloncesto: puntos,rebotes | voleibol: aces,bloqueos,sets)",
				filasEstadisticas);
	}

	// Entrenamiento

	private static void sesiones(DatosClub d, Path carpeta) throws IOException {
		List<String> filasSesiones = new ArrayList<String>();
		List<String> filasAsistencias = new ArrayList<String>();
		int id = 1;
		for (SesionEntrenamiento s : d.getSesiones()) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (Socio socio : s.getListaDeJugadoresConvocados()) {
				ids.add(socio.getId());
			}
			filasSesiones.add(linea(String.valueOf(id), fecha(s.getFecha()), String.valueOf(s.getHora()),
					String.valueOf(s.getDuracion()), esc(s.getEntrenadorResponsable().getId()),
					esc(s.getInstalacionUtilizada()), String.valueOf(s.isFinalizada()), lista(ids)));
			for (RegistroAsistencia r : s.getRegistrosAsistencia()) {
				filasAsistencias.add(linea(String.valueOf(id), esc(r.getSocio().getId()), esc(r.getEstado()),
						r.getEvaluacion() == null ? "" : String.valueOf(r.getEvaluacion()), esc(r.getComentario())));
			}
			id++;
		}
		escribir(carpeta, PersistenciaClub.SESIONES,
				"id;fecha;hora;duracion;entrenadorId;instalacion;finalizada;convocados", filasSesiones);
		escribir(carpeta, PersistenciaClub.ASISTENCIAS, "sesionId;socioId;estado;evaluacion;comentario",
				filasAsistencias);
	}

	// Administración: turnos, solicitudes, sugerencias

	private static void turnos(DatosClub d, Path carpeta) throws IOException {
		List<String> filasTurnos = new ArrayList<String>();
		for (Turno t : d.getTurnos()) {
			filasTurnos.add(linea(esc(t.getEmpleado().getId()), esc(t.getDiaSemana()),
					String.valueOf(t.getHoraInicio()), String.valueOf(t.getHoraFin())));
		}
		// El turno actual y el propuesto van dentro de la solicitud: una solicitud
		// pendiente tiene un turno propuesto que aún no está en la lista de turnos.
		List<String> filasSolicitudes = new ArrayList<String>();
		for (SolicitudCambioTurno s : d.getSolicitudes()) {
			Turno a = s.getTurnoActual();
			Turno p = s.getTurnoPropuesto();
			filasSolicitudes.add(linea(esc(s.getEmpleado().getId()), esc(a.getDiaSemana()),
					String.valueOf(a.getHoraInicio()), String.valueOf(a.getHoraFin()), esc(p.getDiaSemana()),
					String.valueOf(p.getHoraInicio()), String.valueOf(p.getHoraFin()), esc(s.getEstado())));
		}
		escribir(carpeta, PersistenciaClub.TURNOS, "empleadoId;diaSemana;horaInicio;horaFin", filasTurnos);
		escribir(carpeta, PersistenciaClub.SOLICITUDES_TURNO,
				"empleadoId;diaActual;inicioActual;finActual;diaPropuesto;inicioPropuesto;finPropuesto;estado",
				filasSolicitudes);
	}

	private static void sugerencias(DatosClub d, Path carpeta) throws IOException {
		List<String> filas = new ArrayList<String>();
		for (SugerenciaProducto s : d.getSugerencias()) {
			filas.add(linea(esc(s.getNombre()), esc(s.getCategoria()), esc(s.getEstado()),
					esc(s.getEmpleado().getId())));
		}
		escribir(carpeta, PersistenciaClub.SUGERENCIAS, "nombre;categoria;estado;empleadoId", filas);
	}

	// Ventas

	private static void ventas(DatosClub d, Path carpeta) throws IOException {
		List<String> filasVentas = new ArrayList<String>();
		List<String> filasItems = new ArrayList<String>();
		int id = 1;
		for (Venta v : d.getRegistroVentas().getVentas()) {
			String tipo;
			String propina = "";
			if (v instanceof VentaTienda) {
				tipo = "TIENDA";
			} else if (v instanceof VentaCafeteria) {
				tipo = "CAFETERIA";
				propina = String.valueOf(((VentaCafeteria) v).getPorcentajePropina());
			} else {
				throw new IOException("Tipo de venta no soportado: " + v.getClass().getName());
			}
			filasVentas.add(linea(String.valueOf(id), tipo, fecha(v.getFecha()), esc(v.getComprador().getId()),
					String.valueOf(v.getPorcentajeDescuento()), String.valueOf(v.getPuntosAplicados()), propina));
			for (ItemVenta item : v.getItems()) {
				filasItems.add(linea(String.valueOf(id), esc(item.getProducto().getNombre()),
						String.valueOf(item.getCantidad())));
			}
			id++;
		}
		escribir(carpeta, PersistenciaClub.VENTAS,
				"id;tipo;fecha;compradorId;porcentajeDescuento;puntosRedimidos;porcentajePropina", filasVentas);
		escribir(carpeta, PersistenciaClub.VENTAS_ITEMS, "ventaId;producto;cantidad", filasItems);
	}

	// Escritura de un archivo (temporal + renombrar)

	private static void escribir(Path carpeta, String nombre, String encabezado, List<String> filas)
			throws IOException {
		List<String> contenido = new ArrayList<String>();
		contenido.add("# " + encabezado);
		contenido.addAll(filas);
		Path destino = carpeta.resolve(nombre);
		Path temporal = carpeta.resolve(nombre + ".tmp");
		Files.write(temporal, contenido, StandardCharsets.UTF_8);
		Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
	}
}
