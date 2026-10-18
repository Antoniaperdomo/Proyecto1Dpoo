package Persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Competencias.ASCUN;
import Competencias.Competencia;
import Competencias.NominaCompetencia;
import Competencias.TablaPosiciones;
import Competencias.TorneoCerros;
import Deportes.Baloncesto;
import Deportes.CanchaPadel;
import Deportes.CanchaTenis;
import Deportes.Categoria;
import Deportes.DeporteConjunto;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.EstadisticaBaloncesto;
import Deportes.EstadisticaFutbol;
import Deportes.EstadisticaJugadorPartido;
import Deportes.EstadisticaPractica;
import Deportes.EstadisticaTenisDeMesa;
import Deportes.EstadisticaVoleibol;
import Deportes.EstadisticasTenisYPadel;
import Deportes.Futbol;
import Deportes.InscripcionIndividual;
import Deportes.Institucion;
import Deportes.Instalacion;
import Deportes.JugadorEquipo;
import Deportes.MesaTenisDeMesa;
import Deportes.Padel;
import Deportes.Partido;
import Deportes.PartidoCompetencia;
import Deportes.PartidoInterno;
import Deportes.PartidoPractica;
import Deportes.ReservaInstalacion;
import Deportes.Suspension;
import Deportes.Tenis;
import Deportes.TenisMesa;
import Deportes.Voleibol;
import Entrenamiento.SesionEntrenamiento;
import Tienda.Articulo;
import Tienda.Bebida;
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
 * Lee los archivos de la carpeta de datos y reconstruye el estado del club.
 * 
 * Los archivos se leen en un orden fijo (primero lo que no depende de nada,
 * luego lo que referencia a lo anterior). Los datos se cargan tal como fueron
 * guardados: NO se vuelven a ejecutar las reglas de negocio de "agregar" (edad,
 * cupo, etc.) porque, por ejemplo, un socio puede haber cumplido años después
 * de inscribirse. Sí se valida la integridad: llaves repetidas y referencias a
 * cosas que no existen. Si un archivo no existe se asume vacío.
 */
public class CargadorClub {

	private interface ProcesadorFila {
		void procesar(Fila f) throws IOException;
	}

	public static DatosClub cargar(Path carpeta) throws IOException {
		DatosClub d = new DatosClub();

		usuarios(d, carpeta);
		ubicaciones(d, carpeta);
		productos(d, carpeta);
		existencias(d, carpeta);
		equipos(d, carpeta);
		jugadoresEquipo(d, carpeta);
		suspensiones(d, carpeta);
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
		return d;
	}

	// Usuarios

	private static void usuarios(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.USUARIOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				String tipo = f.texto(0).trim().toUpperCase();
				int id = f.entero(1);
				String nombre = f.texto(2);
				String login = f.texto(3);
				String password = f.texto(4);
				Date nacimiento = f.fecha(5);

				if (d.buscarUsuario(id) != null) {
					throw f.error("el id " + id + " ya existe");
				}
				if (d.buscarUsuarioPorLogin(login) != null) {
					throw f.error("el login '" + login + "' ya existe");
				}

				Usuario u;
				if (tipo.equals("ADMINISTRADOR")) {
					u = new Administrador(id, nombre, login, password, nacimiento);
				} else if (tipo.equals("SOCIO")) {
					u = new Socio(id, nombre, login, password, nacimiento, f.enteroOpcional(7, 0),
							f.booleanoOpcional(8, true));
				} else if (tipo.equals("ENTRENADOR")) {
					Entrenador e = new Entrenador(id, nombre, login, password, nacimiento, f.fecha(6));
					e.setPuntosFidelidad(f.enteroOpcional(7, 0));
					u = e;
				} else if (tipo.equals("FISIOTERAPEUTA")) {
					Fisioterapeuta e = new Fisioterapeuta(id, nombre, login, password, nacimiento, f.fecha(6));
					e.setPuntosFidelidad(f.enteroOpcional(7, 0));
					u = e;
				} else {
					throw f.error("tipo de usuario desconocido: " + tipo);
				}
				d.getUsuarios().add(u);
			}
		});
	}

	// Tienda

	private static void ubicaciones(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.UBICACIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				String nombre = f.texto(0);
				if (d.buscarUbicacion(nombre) != null) {
					throw f.error("la ubicación '" + nombre + "' está repetida");
				}
				d.getUbicaciones().add(new Ubicacion(nombre));
			}
		});
	}

	private static void productos(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.PRODUCTOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				String tipo = f.texto(0).trim().toUpperCase();
				String nombre = f.texto(1);
				double precio = f.decimal(2);
				if (d.buscarProducto(nombre) != null) {
					throw f.error("el producto '" + nombre + "' está repetido");
				}
				Producto p;
				if (tipo.equals("ARTICULO")) {
					p = new Articulo(nombre, precio, f.texto(3));
				} else if (tipo.equals("BEBIDA")) {
					p = new Bebida(nombre, precio, f.texto(3));
				} else if (tipo.equals("SNACK")) {
					p = new Snack(nombre, precio, f.lista(3));
				} else {
					throw f.error("tipo de producto desconocido: " + tipo);
				}
				d.getProductos().add(p);
			}
		});
	}

	private static void existencias(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.EXISTENCIAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Producto p = d.buscarProducto(f.texto(0));
				if (!(p instanceof Articulo)) {
					throw f.error("'" + f.texto(0) + "' no es un artículo registrado");
				}
				Ubicacion u = d.buscarUbicacion(f.texto(1));
				if (u == null) {
					throw f.error("la ubicación '" + f.texto(1) + "' no existe");
				}
				d.getInventario().registrarExistencia((Articulo) p, u, f.entero(2));
			}
		});
	}

	// Equipos

	private static void equipos(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.EQUIPOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				String nombre = f.texto(0);
				if (d.buscarEquipo(nombre) != null) {
					throw f.error("el equipo '" + nombre + "' está repetido");
				}
				DeporteConjunto deporte = deporteConjunto(f, f.texto(1));
				Equipo e = new Equipo(nombre, deporte, f.entero(3), new Categoria(f.texto(2).trim().toUpperCase()),
						new ArrayList<JugadorEquipo>(), new ArrayList<Suspension>(), f.booleanoOpcional(4, true));
				d.getEquipos().add(e);
			}
		});
	}

	private static void jugadoresEquipo(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.JUGADORES_EQUIPO, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Equipo e = equipo(d, f, f.texto(0));
				Socio s = socio(d, f, f.entero(1));
				int camiseta = f.entero(3);
				if (e.tieneJugador(s)) {
					throw f.error(s.getNombre() + " ya está en el equipo " + e.getNombre());
				}
				if (e.getJugadores().size() >= e.getCupoMax()) {
					throw f.error("el equipo " + e.getNombre() + " excede su cupo máximo");
				}
				for (JugadorEquipo otro : e.getJugadores()) {
					if (otro.getNumeroCamiseta() == camiseta) {
						throw f.error("la camiseta " + camiseta + " está repetida en " + e.getNombre());
					}
				}
				JugadorEquipo j = new JugadorEquipo(s, f.texto(2), e, camiseta);
				j.setAmarillas(Suspension.INTERNO, f.enteroOpcional(4, 0));
				j.setAmarillas(Suspension.COMPETENCIA, f.enteroOpcional(5, 0));
				e.getJugadores().add(j);
				// El socio también conoce sus equipos
				s.getEquipos().add(e);
			}
		});
	}

	private static void suspensiones(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.SUSPENSIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Equipo e = equipo(d, f, f.texto(0));
				Suspension s = new Suspension(socio(d, f, f.entero(1)), f.texto(2), f.texto(3).trim().toUpperCase());
				if (f.booleanoOpcional(4, false)) {
					s.cumplir();
				}
				e.getSuspensiones().add(s);
			}
		});
	}

	// Deportes individuales

	private static void inscripciones(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.INSCRIPCIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Socio s = socio(d, f, f.entero(0));
				String disciplina = f.texto(1).trim().toUpperCase();
				if (s.buscarInscripcion(disciplina) != null) {
					throw f.error(s.getNombre() + " ya está inscrito en " + disciplina);
				}
				InscripcionIndividual i = new InscripcionIndividual(s, deporteIndividual(f, disciplina));
				// el nivel se recalcula a partir del ELO
				i.restaurar(f.entero(2), f.listaEnteros(3));
				s.getInscripciones().add(i);
			}
		});
	}

	private static void instalaciones(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.INSTALACIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				String tipo = f.texto(0).trim().toUpperCase();
				String nombre = f.texto(1);
				if (d.buscarInstalacion(nombre) != null) {
					throw f.error("la instalación '" + nombre + "' está repetida");
				}
				Instalacion i;
				if (tipo.equals("TENIS")) {
					i = new CanchaTenis(nombre);
				} else if (tipo.equals("PADEL")) {
					i = new CanchaPadel(nombre);
				} else if (tipo.equals("TENISDEMESA")) {
					i = new MesaTenisDeMesa(nombre, f.booleanoOpcional(2, false));
				} else {
					throw f.error("tipo de instalación desconocido: " + tipo);
				}
				d.getInstalaciones().add(i);
			}
		});
	}

	private static void reservas(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.RESERVAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Socio s = socio(d, f, f.entero(0));
				Instalacion i = instalacion(d, f, f.texto(1));
				ReservaInstalacion r = new ReservaInstalacion(s, i, f.fecha(2), f.fecha(3), f.texto(4), f.entero(5));
				i.agregarReserva(r);
				s.getReservas().add(r);
			}
		});
	}

	private static void partidosPractica(final DatosClub d, Path carpeta) throws IOException {
		final HashMap<Integer, PartidoPractica> porId = new HashMap<Integer, PartidoPractica>();
		recorrer(carpeta, PersistenciaClub.PARTIDOS_PRACTICA, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				int id = f.entero(0);
				if (porId.containsKey(id)) {
					throw f.error("el id " + id + " está repetido");
				}
				ArrayList<Socio> jugadores = new ArrayList<Socio>();
				for (int idSocio : f.listaEnteros(6)) {
					jugadores.add(socio(d, f, idSocio));
				}
				PartidoPractica p = new PartidoPractica(f.fecha(1), f.texto(2).trim().toUpperCase(),
						instalacion(d, f, f.texto(3)), jugadores, f.booleano(4));
				p.setFinalizado(f.booleano(5));
				p.registrarEnJugadores();
				d.getPartidosPractica().add(p);
				porId.put(id, p);
			}
		});
		recorrer(carpeta, PersistenciaClub.ESTADISTICAS_PRACTICA, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				PartidoPractica p = porId.get(f.entero(0));
				if (p == null) {
					throw f.error("el partido de práctica " + f.entero(0) + " no existe");
				}
				Socio s = socio(d, f, f.entero(1));
				EstadisticaPractica est = s.buscarInscripcion(p.getDisciplina()).getDeporte().crearEstadistica();
				est.setSetsGanados(f.entero(2));
				if (est instanceof EstadisticasTenisYPadel) {
					EstadisticasTenisYPadel t = (EstadisticasTenisYPadel) est;
					t.setGamesGanados(f.enteroOpcional(3, 0));
					t.setAces(f.enteroOpcional(4, 0));
					t.setErroresNoForzado(f.enteroOpcional(5, 0));
				} else if (est instanceof EstadisticaTenisDeMesa) {
					((EstadisticaTenisDeMesa) est).setPuntosTotales(f.enteroOpcional(6, 0));
				}
				p.registrarEstadisticas(s, est);
			}
		});
	}

	// Competencias

	private static void competencias(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.COMPETENCIAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				String tipo = f.texto(0).trim().toUpperCase();
				String nombre = f.texto(1);
				if (d.buscarCompetencia(nombre) != null) {
					throw f.error("la competencia '" + nombre + "' está repetida");
				}
				DeporteConjunto deporte = deporteConjunto(f, f.texto(4));
				Categoria categoria = new Categoria(f.texto(5).trim().toUpperCase());
				Competencia c;
				if (tipo.equals("TORNEOCERROS")) {
					c = new TorneoCerros(nombre, f.texto(2), f.texto(3), deporte, categoria, new TablaPosiciones());
				} else if (tipo.equals("ASCUN")) {
					c = new ASCUN(nombre, f.texto(2), f.texto(3), deporte, categoria, f.textoOpcional(6));
				} else {
					throw f.error("tipo de competencia desconocido: " + tipo);
				}
				d.getCompetencias().add(c);
			}
		});
		recorrer(carpeta, PersistenciaClub.NOMINAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Competencia c = competencia(d, f, f.texto(0));
				Equipo e = equipo(d, f, f.texto(1));
				ArrayList<Socio> jugadores = new ArrayList<Socio>();
				for (int id : f.listaEnteros(2)) {
					jugadores.add(socio(d, f, id));
				}
				d.getNominas().add(new NominaCompetencia(e, c, jugadores));
			}
		});
		recorrer(carpeta, PersistenciaClub.TABLA_POSICIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Competencia c = competencia(d, f, f.texto(0));
				if (!(c instanceof TorneoCerros)) {
					throw f.error("'" + c.getNombre() + "' no tiene tabla de posiciones (no es liga)");
				}
				((TorneoCerros) c).getTablaPosiciones().getPuntosPorEquipo().put(equipo(d, f, f.texto(1)),
						f.entero(2));
			}
		});
	}

	// Partidos de equipos

	private static void partidos(final DatosClub d, Path carpeta) throws IOException {
		final HashMap<Integer, Partido> porId = new HashMap<Integer, Partido>();
		recorrer(carpeta, PersistenciaClub.PARTIDOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				int id = f.entero(0);
				if (porId.containsKey(id)) {
					throw f.error("el id " + id + " está repetido");
				}
				String tipo = f.texto(1).trim().toUpperCase();
				DeporteConjunto deporte = deporteConjunto(f, f.texto(3));
				Equipo equipo1 = equipo(d, f, f.texto(4));
				Partido p;
				if (tipo.equals("INTERNO")) {
					p = new PartidoInterno(f.fecha(2), deporte, equipo1, equipo(d, f, f.texto(5)));
				} else if (tipo.equals("COMPETENCIA")) {
					PartidoCompetencia pc = new PartidoCompetencia(f.fecha(2), deporte, equipo1,
							new Institucion(f.texto(6)), f.textoOpcional(7));
					if (f.textoOpcional(8) != null) {
						pc.setCompetencia(competencia(d, f, f.texto(8)));
					}
					p = pc;
				} else {
					throw f.error("tipo de partido desconocido: " + tipo);
				}
				p.registrarMarcador(f.enteroOpcional(9, 0), f.enteroOpcional(10, 0));
				p.setJugado(f.booleanoOpcional(11, false));
				d.getPartidos().add(p);
				porId.put(id, p);
			}
		});
		recorrer(carpeta, PersistenciaClub.ESTADISTICAS_PARTIDO, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Partido p = porId.get(f.entero(0));
				if (p == null) {
					throw f.error("el partido " + f.entero(0) + " no existe");
				}
				EstadisticaJugadorPartido est = p.getDeeporte().crearEstadistica(socio(d, f, f.entero(1)));
				if (est instanceof EstadisticaFutbol) {
					EstadisticaFutbol e = (EstadisticaFutbol) est;
					e.setGoles(f.enteroOpcional(2, 0));
					e.setAsistencias(f.enteroOpcional(3, 0));
					e.setTarjetasAmarillas(f.enteroOpcional(4, 0));
					e.setTarjetasRojas(f.enteroOpcional(5, 0));
				} else if (est instanceof EstadisticaBaloncesto) {
					EstadisticaBaloncesto e = (EstadisticaBaloncesto) est;
					e.setPuntos(f.enteroOpcional(2, 0));
					e.setRebotes(f.enteroOpcional(3, 0));
				} else if (est instanceof EstadisticaVoleibol) {
					EstadisticaVoleibol e = (EstadisticaVoleibol) est;
					e.setAces(f.enteroOpcional(2, 0));
					e.setBloqueos(f.enteroOpcional(3, 0));
					e.setSets(f.enteroOpcional(4, 0));
				}
				p.getEstadisticas().add(est);
			}
		});
	}

	// Entrenamiento

	private static void sesiones(final DatosClub d, Path carpeta) throws IOException {
		final HashMap<Integer, SesionEntrenamiento> porId = new HashMap<Integer, SesionEntrenamiento>();
		recorrer(carpeta, PersistenciaClub.SESIONES, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				int id = f.entero(0);
				if (porId.containsKey(id)) {
					throw f.error("el id " + id + " está repetido");
				}
				Entrenador entrenador = d.buscarEntrenador(f.entero(4));
				if (entrenador == null) {
					throw f.error("el entrenador " + f.entero(4) + " no existe");
				}
				SesionEntrenamiento s = new SesionEntrenamiento(f.fecha(1), f.entero(2), f.decimal(3), entrenador,
						f.texto(5));
				for (int idSocio : f.listaEnteros(7)) {
					// Se agrega directo a la lista: convocarJugador() rechaza socios dados de baja
					// y una sesión histórica debe conservar a todos los que fueron convocados.
					s.getListaDeJugadoresConvocados().add(socio(d, f, idSocio));
				}
				if (f.booleano(6)) {
					s.finalizarSesion();
				}
				d.getSesiones().add(s);
				porId.put(id, s);
			}
		});
		recorrer(carpeta, PersistenciaClub.ASISTENCIAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				SesionEntrenamiento s = porId.get(f.entero(0));
				if (s == null) {
					throw f.error("la sesión " + f.entero(0) + " no existe");
				}
				s.registrarAsistencia(socio(d, f, f.entero(1)), f.texto(2), f.decimalOpcional(3), f.textoOpcional(4));
			}
		});
	}


	// Administración

	private static void turnos(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.TURNOS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				d.getTurnos().add(new Turno(empleado(d, f, f.entero(0)), f.texto(1), f.entero(2), f.entero(3)));
			}
		});
		recorrer(carpeta, PersistenciaClub.SOLICITUDES_TURNO, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Empleado e = empleado(d, f, f.entero(0));
				// Se reutiliza el objeto Turno de la lista cuando existe: el administrador
				// compara turnos por referencia (t != solicitud.getTurnoActual()).
				Turno actual = turnoIgualOCrear(d, e, f.texto(1), f.entero(2), f.entero(3));
				Turno propuesto = turnoIgualOCrear(d, e, f.texto(4), f.entero(5), f.entero(6));
				SolicitudCambioTurno s = new SolicitudCambioTurno(e, actual, propuesto);
				s.setEstado(f.texto(7));
				d.getSolicitudes().add(s);
			}
		});
	}

	private static Turno turnoIgualOCrear(DatosClub d, Empleado e, String dia, int inicio, int fin) {
		for (Turno t : d.getTurnos()) {
			if (t.getEmpleado() == e && t.getDiaSemana().equals(dia) && t.getHoraInicio() == inicio
					&& t.getHoraFin() == fin) {
				return t;
			}
		}
		return new Turno(e, dia, inicio, fin);
	}

	private static void sugerencias(final DatosClub d, Path carpeta) throws IOException {
		recorrer(carpeta, PersistenciaClub.SUGERENCIAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				SugerenciaProducto s = new SugerenciaProducto(f.texto(0), f.texto(1), empleado(d, f, f.entero(3)));
				s.setEstado(f.texto(2));
				d.getSugerencias().add(s);
			}
		});
	}

	// Ventas

	private static void ventas(final DatosClub d, Path carpeta) throws IOException {
		final HashMap<Integer, ArrayList<ItemVenta>> itemsPorVenta = new HashMap<Integer, ArrayList<ItemVenta>>();
		recorrer(carpeta, PersistenciaClub.VENTAS_ITEMS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				Producto p = d.buscarProducto(f.texto(1));
				if (p == null) {
					throw f.error("el producto '" + f.texto(1) + "' no existe");
				}
				int idVenta = f.entero(0);
				if (!itemsPorVenta.containsKey(idVenta)) {
					itemsPorVenta.put(idVenta, new ArrayList<ItemVenta>());
				}
				itemsPorVenta.get(idVenta).add(new ItemVenta(p, f.entero(2)));
			}
		});
		recorrer(carpeta, PersistenciaClub.VENTAS, new ProcesadorFila() {
			public void procesar(Fila f) throws IOException {
				int id = f.entero(0);
				String tipo = f.texto(1).trim().toUpperCase();
				Usuario comprador = d.buscarUsuario(f.entero(3));
				if (comprador == null) {
					throw f.error("el comprador " + f.entero(3) + " no existe");
				}
				ArrayList<ItemVenta> items = itemsPorVenta.get(id);
				if (items == null) {
					items = new ArrayList<ItemVenta>();
				}
				Venta v;
				if (tipo.equals("TIENDA")) {
					v = new VentaTienda(f.fecha(2), comprador, items, f.enteroOpcional(4, 0));
				} else if (tipo.equals("CAFETERIA")) {
					v = new VentaCafeteria(f.fecha(2), comprador, items, f.enteroOpcional(4, 0), f.decimal(6));
				} else {
					throw f.error("tipo de venta desconocido: " + tipo);
				}
				v.redimirPuntos(f.enteroOpcional(5, 0));
				d.getRegistroVentas().registrar(v);
			}
		});
	}

	// Búsquedas con error claro si la referencia no existe

	private static Socio socio(DatosClub d, Fila f, int id) throws IOException {
		Socio s = d.buscarSocio(id);
		if (s == null) {
			throw f.error("el socio " + id + " no existe");
		}
		return s;
	}

	private static Empleado empleado(DatosClub d, Fila f, int id) throws IOException {
		Empleado e = d.buscarEmpleado(id);
		if (e == null) {
			throw f.error("el empleado " + id + " no existe");
		}
		return e;
	}

	private static Equipo equipo(DatosClub d, Fila f, String nombre) throws IOException {
		Equipo e = d.buscarEquipo(nombre);
		if (e == null) {
			throw f.error("el equipo '" + nombre + "' no existe");
		}
		return e;
	}

	private static Competencia competencia(DatosClub d, Fila f, String nombre) throws IOException {
		Competencia c = d.buscarCompetencia(nombre);
		if (c == null) {
			throw f.error("la competencia '" + nombre + "' no existe");
		}
		return c;
	}

	private static Instalacion instalacion(DatosClub d, Fila f, String nombre) throws IOException {
		Instalacion i = d.buscarInstalacion(nombre);
		if (i == null) {
			throw f.error("la instalación '" + nombre + "' no existe");
		}
		return i;
	}


	// Fábricas de deportes

	private static DeporteConjunto deporteConjunto(Fila f, String nombre) throws IOException {
		String n = nombre.trim().toUpperCase();
		if (n.equals("FUTBOL")) {
			return new Futbol();
		}
		if (n.equals("BALONCESTO")) {
			return new Baloncesto();
		}
		if (n.equals("VOLEIBOL")) {
			return new Voleibol();
		}
		throw f.error("deporte de conjunto desconocido: " + nombre);
	}

	private static DeporteIndividual deporteIndividual(Fila f, String nombre) throws IOException {
		if (nombre.equals("TENIS")) {
			return new Tenis(DeporteIndividual.PRINCIPIANTE);
		}
		if (nombre.equals("PADEL")) {
			return new Padel(DeporteIndividual.PRINCIPIANTE);
		}
		if (nombre.equals("TENISDEMESA")) {
			return new TenisMesa(DeporteIndividual.PRINCIPIANTE);
		}
		throw f.error("disciplina desconocida: " + nombre);
	}


	// Lectura de archivos

	private static void recorrer(Path carpeta, String nombreArchivo, ProcesadorFila procesador) throws IOException {
		Path archivo = carpeta.resolve(nombreArchivo);
		if (!Files.exists(archivo)) {
			return; // archivo ausente = sin datos
		}
		List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
		for (int i = 0; i < lineas.size(); i++) {
			String texto = lineas.get(i);
			if (texto.trim().isEmpty() || texto.startsWith("#")) {
				continue;
			}
			Fila fila = new Fila(nombreArchivo, i + 1, Formato.dividirCrudo(texto, Formato.SEPARADOR_COLUMNA));
			try {
				procesador.procesar(fila);
			} catch (IOException e) {
				throw e;
			} catch (RuntimeException e) {
				// Las reglas del dominio lanzan IllegalArgument/IllegalState: se reportan con archivo y línea
				throw fila.error(e.getMessage());
			}
		}
	}
}
