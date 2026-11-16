package Torneos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Administradores.Turno;
import Deportes.Categoria;
import Deportes.Deporte;
import Deportes.DeporteConjunto;
import Deportes.DeporteIndividual;
import Deportes.Equipo;
import Deportes.Futbol;
import Deportes.InscripcionIndividual;
import Deportes.Pareja;
import Deportes.Partido;
import Deportes.Suspension;
import Usuario.Empleado;
import Usuario.Externo;
import Usuario.Participante;
import Usuario.Socio;

public abstract class Torneo {
	
	
	public static final String ABIERTO = "ABIERTO";
	public static final String EN_CURSO = "EN_CURSO";
	public static final String FINALIZADO = "FINALIZADO";

	public static final String SENCILLOS = "SENCILLOS";
	public static final String DOBLES = "DOBLES";

	/** Porcentaje de cupos reservado a los mejores del ranking (se redondea hacia arriba). */
	public static final int PORCENTAJE_PREFERENCIAL = 20;

	private static final String[] DIAS = { "DOMINGO", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO" };

	private final int id;
	private final String nombre;
	private final Deporte deporte;
	private final Categoria categoria; // solo deportes de conjunto
	private final String nivel; // solo disciplinas individuales
	private final String modalidad; // solo disciplinas individuales
	private final int cupos;
	private final Date fechaInicio;
	private final Date fechaLimiteReserva;

	private String estado;
	private ArrayList<InscripcionTorneo> inscripciones;
	private ArrayList<EncuentroTorneo> encuentros;
	private ArrayList<String> premiosEntregados;

	// Eliminacion directa: ronda en juego y quienes avanzan de ella
	private int rondaActual;
	private ArrayList<InscripcionTorneo> avanzan;

	protected Torneo(int id, String nombre, Deporte deporte, Categoria categoria, String nivel, String modalidad,
			int cupos, Date fechaInicio, Date fechaLimiteReserva) {
		if (nombre == null || nombre.trim().isEmpty()) {
			throw new IllegalArgumentException("El torneo necesita un nombre");
		}
		if (deporte == null) {
			throw new IllegalArgumentException("El deporte o disciplina del torneo no existe");
		}
		if (cupos < 2) {
			throw new IllegalArgumentException("Un torneo necesita al menos 2 cupos");
		}
		if (fechaInicio == null || fechaLimiteReserva == null) {
			throw new IllegalArgumentException("Las fechas de inicio y de reserva son obligatorias");
		}
		if (fechaLimiteReserva.after(fechaInicio)) {
			throw new IllegalArgumentException("La fecha limite de reserva no puede ser posterior al inicio");
		}
		if (deporte.esIndividual()) {
			if (!DeporteIndividual.esNivelValido(nivel)) {
				throw new IllegalArgumentException("El nivel no existe: " + nivel);
			}
			if (!SENCILLOS.equals(modalidad) && !DOBLES.equals(modalidad)) {
				throw new IllegalArgumentException("La modalidad debe ser sencillos o dobles");
			}
			this.categoria = null;
			this.nivel = nivel;
			this.modalidad = modalidad;
		} else {
			if (categoria == null) {
				throw new IllegalArgumentException("La categoria no existe");
			}
			this.categoria = categoria;
			this.nivel = null;
			this.modalidad = null;
		}
		this.id = id;
		this.nombre = nombre;
		this.deporte = deporte;
		this.cupos = cupos;
		this.fechaInicio = fechaInicio;
		this.fechaLimiteReserva = fechaLimiteReserva;
		this.estado = ABIERTO;
		this.inscripciones = new ArrayList<InscripcionTorneo>();
		this.encuentros = new ArrayList<EncuentroTorneo>();
		this.premiosEntregados = new ArrayList<String>();
		this.avanzan = new ArrayList<InscripcionTorneo>();
	}

	// datos

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public Deporte getDeporte() {
		return deporte;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public String getNivel() {
		return nivel;
	}

	public String getModalidad() {
		return modalidad;
	}

	public int getCupos() {
		return cupos;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public Date getFechaLimiteReserva() {
		return fechaLimiteReserva;
	}

	public String getEstado() {
		return estado;
	}

	public ArrayList<InscripcionTorneo> getInscripciones() {
		return inscripciones;
	}

	public ArrayList<EncuentroTorneo> getEncuentros() {
		return encuentros;
	}

	public ArrayList<String> getPremiosEntregados() {
		return premiosEntregados;
	}

	public boolean esIndividual() {
		return deporte.esIndividual();
	}

	/** "RECREATIVO" o "COMPETITIVO". */
	public abstract String getTipo();

	/** Tarifa de inscripcion (0 en los recreativos). */
	public abstract double getTarifa();

	/** Lo que cobra el torneo a una inscripcion (los empleados juegan gratis). */
	protected abstract double tarifaPara(boolean exentoDeTarifa);

	/** Entrega los premios a partir de la clasificacion final (primero = ganador). */
	protected abstract void entregarPremios(ArrayList<InscripcionTorneo> clasificacion);

	/** Aviso al salirse del torneo (HU26): en competitivos el reembolso es externo. */
	public String mensajeRetiro(InscripcionTorneo inscripcion) {
		return "Inscripcion retirada.";
	}

	// ----------------------------------------------------------------- cupos

	/** 20% de los cupos, redondeado hacia arriba (11 cupos reservan 3). */
	public int getCuposPreferenciales() {
		return (cupos * PORCENTAJE_PREFERENCIAL + 99) / 100;
	}

	public int getCuposRegulares() {
		return cupos - getCuposPreferenciales();
	}

	public int contarOcupados(String tipoCupo) {
		int total = 0;
		for (InscripcionTorneo i : inscripciones) {
			if (i.getTipoCupo().equals(tipoCupo)) {
				total++;
			}
		}
		return total;
	}

	public double getTotalRecaudado() {
		double total = 0;
		for (InscripcionTorneo i : inscripciones) {
			total += i.getTarifaPagada();
		}
		return total;
	}

	/**
	 * Decide que cupo recibe una inscripcion nueva (HU23, HU24, HU25).
	 * Antes de la fecha limite los preferenciales solo son para los mejores del
	 * ranking; despues se liberan y pasan a ser regulares.
	 */
	private String asignarTipoCupo(boolean entreLosMejores, Date ahora) {
		if (inscripciones.size() >= cupos) {
			throw new IllegalStateException("El torneo esta lleno");
		}
		if (ahora.after(fechaLimiteReserva)) {
			return InscripcionTorneo.REGULAR;
		}
		if (contarOcupados(InscripcionTorneo.REGULAR) < getCuposRegulares()) {
			return InscripcionTorneo.REGULAR;
		}
		if (entreLosMejores && contarOcupados(InscripcionTorneo.PREFERENCIAL) < getCuposPreferenciales()) {
			return InscripcionTorneo.PREFERENCIAL;
		}
		throw new IllegalStateException("Solo quedan cupos reservados para los mejores del ranking");
	}

	// ---------------------------------------------------------- inscripcion

	/** HU23 y HU25 (conjunto): un socio integrante inscribe a su equipo completo. */
	public InscripcionTorneo inscribirEquipo(Equipo equipo, Socio solicitante, ArrayList<Equipo> equiposDelClub,
			ArrayList<Partido> partidosJugados, Date ahora) {
		validarAbierto();
		if (esIndividual()) {
			throw new IllegalArgumentException("En un torneo individual no se inscriben equipos");
		}
		if (equipo == null || solicitante == null) {
			throw new IllegalArgumentException("El equipo y el socio son obligatorios");
		}
		if (!equipo.isActivo()) {
			throw new IllegalStateException("El equipo esta dado de baja");
		}
		if (!equipo.getDeporte().getNombre().equals(deporte.getNombre())
				|| !equipo.getCategoria().esIgual(categoria)) {
			throw new IllegalArgumentException(
					"El torneo es de " + deporte.getNombre() + " categoria " + categoria.getNombre());
		}
		if (!equipo.tieneJugador(solicitante)) {
			throw new IllegalStateException("El socio no es integrante del equipo");
		}
		if (buscarInscripcion(equipo) != null) {
			throw new IllegalStateException("El equipo ya esta inscrito");
		}
		int minimo = ((DeporteConjunto) deporte).getMinJugadores();
		int habilitados = equipo.contarHabilitados(Suspension.INTERNO);
		if (habilitados < minimo) {
			throw new IllegalStateException("Al equipo le faltan " + (minimo - habilitados)
					+ " jugadores habilitados (minimo " + minimo + ")");
		}
		boolean entreLosMejores = RankingTorneo.equipoEntreLosMejores(equipo, equiposDelClub, partidosJugados,
				getCuposPreferenciales());
		String tipo = asignarTipoCupo(entreLosMejores, ahora);
		InscripcionTorneo inscripcion = new InscripcionTorneo(equipo, tipo, tarifaPara(false));
		inscripciones.add(inscripcion);
		return inscripcion;
	}

	/**
	 * HU21, HU24, HU25 y HU27 (individual): inscribe a un socio, un externo o un
	 * empleado; en dobles se indica el companero y ambos ocupan un solo cupo.
	 *
	 * @param nivelDeclarado solo lo usan los externos (los socios lo derivan de su ELO)
	 * @param turnos         turnos del club, para el cruce de los empleados
	 */
	public InscripcionTorneo inscribirParticipante(Participante principal, Participante companero,
			String nivelDeclarado, ArrayList<Socio> todosLosSocios, ArrayList<Turno> turnos, Date ahora) {
		validarAbierto();
		if (!esIndividual()) {
			throw new IllegalArgumentException("En los torneos de conjunto solo se inscriben equipos del club");
		}
		if (principal == null) {
			throw new IllegalArgumentException("El participante es obligatorio");
		}
		boolean dobles = DOBLES.equals(modalidad);
		if (dobles && companero == null) {
			throw new IllegalArgumentException("En un torneo de dobles se debe indicar al companero");
		}
		if (!dobles && companero != null) {
			throw new IllegalArgumentException("En un torneo de sencillos no hay companero");
		}
		if (companero != null && companero.getId() == principal.getId()) {
			throw new IllegalArgumentException("El companero debe ser otra persona");
		}
		if (buscarInscripcion(principal) != null || (companero != null && buscarInscripcion(companero) != null)) {
			throw new IllegalStateException("El participante ya esta inscrito");
		}
		validarElegibilidad(principal, nivelDeclarado, turnos);
		if (companero != null) {
			validarElegibilidad(companero, nivelDeclarado, turnos);
		}
		int mejores = getCuposPreferenciales();
		boolean entreLosMejores = RankingTorneo.estaEntreLosMejores(principal, deporte.getNombre(), nivel, mejores,
				todosLosSocios)
				&& (companero == null || RankingTorneo.estaEntreLosMejores(companero, deporte.getNombre(), nivel,
						mejores, todosLosSocios));
		String tipo = asignarTipoCupo(entreLosMejores, ahora);
		double tarifa = tarifaPara(principal instanceof Empleado);
		InscripcionTorneo inscripcion;
		if (dobles) {
			inscripcion = new InscripcionTorneo(new Pareja(principal, companero), tipo, tarifa);
		} else {
			inscripcion = new InscripcionTorneo(principal, tipo, tarifa);
		}
		inscripciones.add(inscripcion);
		return inscripcion;
	}

	/** Requisitos de cada tipo de participante para jugar este torneo individual. */
	private void validarElegibilidad(Participante p, String nivelDeclarado, ArrayList<Turno> turnos) {
		if (p instanceof Socio) {
			Socio socio = (Socio) p;
			if (!socio.isActivo()) {
				throw new IllegalStateException("El socio esta dado de baja");
			}
			InscripcionIndividual insc = socio.buscarInscripcion(deporte.getNombre());
			if (insc == null) {
				throw new IllegalStateException(
						"Primero debe inscribirse en la academia de " + deporte.getNombre());
			}
			if (!nivel.equals(insc.getDeporte().getNivel())) {
				throw new IllegalStateException("El torneo es de nivel " + nivel);
			}
		} else if (p instanceof Externo) {
			if (!nivel.equals(nivelDeclarado)) {
				throw new IllegalStateException("El nivel declarado debe ser " + nivel);
			}
		} else if (p instanceof Empleado) {
			String dia = diaDeSemana(fechaInicio);
			for (Turno t : turnos) {
				if (t.getEmpleado().getId() == p.getId() && t.getDiaSemana().equals(dia)) {
					throw new IllegalStateException("Tiene turno el " + dia + " de " + t.getHoraInicio() + " a "
							+ t.getHoraFin() + " y el torneo es ese dia");
				}
			}
		} else {
			throw new IllegalArgumentException("Tipo de participante no admitido");
		}
	}

	/** HU26: un participante (o su equipo o pareja) se sale antes de que inicie. */
	public InscripcionTorneo retirarParticipante(Participante p) {
		validarNoIniciado();
		InscripcionTorneo inscripcion = buscarInscripcion(p);
		if (inscripcion == null) {
			throw new IllegalStateException("No esta inscrito en el torneo");
		}
		inscripciones.remove(inscripcion);
		return inscripcion;
	}

	public InscripcionTorneo retirarEquipo(Equipo equipo, Socio solicitante) {
		validarNoIniciado();
		InscripcionTorneo inscripcion = buscarInscripcion(equipo);
		if (inscripcion == null) {
			throw new IllegalStateException("El equipo no esta inscrito en el torneo");
		}
		if (!equipo.tieneJugador(solicitante)) {
			throw new IllegalStateException("El socio no es integrante del equipo");
		}
		inscripciones.remove(inscripcion);
		return inscripcion;
	}

	public InscripcionTorneo buscarInscripcion(Participante p) {
		for (InscripcionTorneo i : inscripciones) {
			if (i.getEquipo() == null && i.incluye(p)) {
				return i;
			}
		}
		return null;
	}

	public InscripcionTorneo buscarInscripcion(Equipo equipo) {
		for (InscripcionTorneo i : inscripciones) {
			if (i.esDeEquipo(equipo)) {
				return i;
			}
		}
		return null;
	}

	private void validarAbierto() {
		if (!ABIERTO.equals(estado)) {
			throw new IllegalStateException("El torneo no esta abierto");
		}
	}

	private void validarNoIniciado() {
		if (!ABIERTO.equals(estado)) {
			throw new IllegalStateException("El torneo ya inicio");
		}
	}

	// ----------------------------------------------------------- ciclo de vida

	/** HU18: cierra las inscripciones y genera los encuentros. */
	public void iniciar() {
		if (!ABIERTO.equals(estado)) {
			throw new IllegalStateException("El torneo no esta abierto, esta " + estado);
		}
		if (inscripciones.size() < 2) {
			throw new IllegalStateException(
					"Se necesitan al menos 2 inscripciones y hay " + inscripciones.size());
		}
		estado = EN_CURSO;
		if (esIndividual()) {
			generarRonda(new ArrayList<InscripcionTorneo>(inscripciones));
		} else {
			for (int i = 0; i < inscripciones.size(); i++) {
				for (int j = i + 1; j < inscripciones.size(); j++) {
					encuentros.add(new EncuentroTorneo(1, inscripciones.get(i), inscripciones.get(j), fechaInicio));
				}
			}
		}
	}

	/** Eliminacion directa: si la ronda es impar, el primero pasa libre. */
	private void generarRonda(ArrayList<InscripcionTorneo> participantes) {
		rondaActual++;
		avanzan = new ArrayList<InscripcionTorneo>();
		int i = 0;
		if (participantes.size() % 2 == 1) {
			avanzan.add(participantes.get(0));
			i = 1;
		}
		for (; i + 1 < participantes.size(); i += 2) {
			encuentros.add(new EncuentroTorneo(rondaActual, participantes.get(i), participantes.get(i + 1),
					fechaInicio));
		}
	}

	/** HU19: registra un marcador y actualiza posiciones (conjunto) o cuadro (individual). */
	public void registrarResultado(EncuentroTorneo encuentro, int marcadorLocal, int marcadorVisitante) {
		if (!EN_CURSO.equals(estado)) {
			throw new IllegalStateException("El torneo no esta en curso");
		}
		if (encuentro == null || !encuentros.contains(encuentro)) {
			throw new IllegalArgumentException("El encuentro no pertenece al torneo");
		}
		if (encuentro.isJugado()) {
			throw new IllegalStateException("El resultado de este encuentro ya fue registrado");
		}
		if (marcadorLocal < 0 || marcadorVisitante < 0) {
			throw new IllegalArgumentException("El marcador no puede ser negativo");
		}
		if (marcadorLocal == marcadorVisitante && !admiteEmpates()) {
			throw new IllegalArgumentException("Este deporte no admite empates");
		}
		encuentro.registrar(marcadorLocal, marcadorVisitante);
		if (esIndividual()) {
			avanzan.add(encuentro.getGanador());
			if (rondaTerminada() && avanzan.size() > 1) {
				generarRonda(new ArrayList<InscripcionTorneo>(avanzan));
			}
		}
	}

	/** Solo el futbol admite empates; baloncesto, voleibol y las individuales no. */
	private boolean admiteEmpates() {
		return deporte instanceof Futbol;
	}

	private boolean rondaTerminada() {
		for (EncuentroTorneo e : encuentros) {
			if (e.getRonda() == rondaActual && !e.isJugado()) {
				return false;
			}
		}
		return true;
	}

	/** Encuentros que faltan para terminar el torneo. */
	public int encuentrosFaltantes() {
		int jugados = 0;
		for (EncuentroTorneo e : encuentros) {
			if (e.isJugado()) {
				jugados++;
			}
		}
		if (esIndividual()) {
			return (inscripciones.size() - 1) - jugados; // una eliminacion directa tiene n-1 partidos
		}
		return encuentros.size() - jugados;
	}

	/** HU18: finaliza, define al ganador y entrega los premios. */
	public void finalizar() {
		if (!EN_CURSO.equals(estado)) {
			throw new IllegalStateException("El torneo no esta en curso");
		}
		int faltan = encuentrosFaltantes();
		if (faltan > 0) {
			throw new IllegalStateException("Faltan " + faltan + " encuentros por jugar");
		}
		estado = FINALIZADO;
		entregarPremios(clasificacion());
	}

	protected void registrarPremio(String descripcion) {
		premiosEntregados.add(descripcion);
	}

	// ----------------------------------------------------------- posiciones

	public int puntos(InscripcionTorneo inscripcion) {
		int total = 0;
		DeporteConjunto d = (DeporteConjunto) deporte;
		for (EncuentroTorneo e : encuentros) {
			if (!e.isJugado()) {
				continue;
			}
			if (e.getLocal() == inscripcion) {
				total += d.calcularPuntos(e.getMarcadorLocal(), e.getMarcadorVisitante());
			} else if (e.getVisitante() == inscripcion) {
				total += d.calcularPuntos(e.getMarcadorVisitante(), e.getMarcadorLocal());
			}
		}
		return total;
	}

	private int diferencia(InscripcionTorneo inscripcion) {
		int total = 0;
		for (EncuentroTorneo e : encuentros) {
			if (!e.isJugado()) {
				continue;
			}
			if (e.getLocal() == inscripcion) {
				total += e.getMarcadorLocal() - e.getMarcadorVisitante();
			} else if (e.getVisitante() == inscripcion) {
				total += e.getMarcadorVisitante() - e.getMarcadorLocal();
			}
		}
		return total;
	}

	/** Ronda en la que perdio una inscripcion (0 si no ha perdido). */
	private int rondaDeEliminacion(InscripcionTorneo inscripcion) {
		for (EncuentroTorneo e : encuentros) {
			if (e.isJugado() && e.getPerdedor() == inscripcion) {
				return e.getRonda();
			}
		}
		return 0;
	}

	/**
	 * Clasificacion del torneo, de mejor a peor. En conjunto: puntos de liga y
	 * luego diferencia de marcador; en individual: el campeon, luego quien
	 * llego mas lejos en el cuadro.
	 */
	public ArrayList<InscripcionTorneo> clasificacion() {
		final ArrayList<InscripcionTorneo> orden = new ArrayList<InscripcionTorneo>(inscripciones);
		Collections.sort(orden, new Comparator<InscripcionTorneo>() {
			@Override
			public int compare(InscripcionTorneo a, InscripcionTorneo b) {
				if (esIndividual()) {
					int ra = rondaDeEliminacion(a) == 0 ? Integer.MAX_VALUE : rondaDeEliminacion(a);
					int rb = rondaDeEliminacion(b) == 0 ? Integer.MAX_VALUE : rondaDeEliminacion(b);
					return rb - ra;
				}
				int porPuntos = puntos(b) - puntos(a);
				return porPuntos != 0 ? porPuntos : diferencia(b) - diferencia(a);
			}
		});
		return orden;
	}

	public InscripcionTorneo getGanador() {
		if (!FINALIZADO.equals(estado)) {
			return null;
		}
		return clasificacion().get(0);
	}

	// ------------------------------------------------------------- utilidades

	/** Nombre del dia de la semana, con el mismo formato que Turno.diaSemana. */
	public static String diaDeSemana(Date fecha) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		return DIAS[c.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/** Historial detallado (HU17 y HU22): participantes, encuentros, posiciones y premios. */
	public String getHistorial() {
		StringBuilder sb = new StringBuilder();
		sb.append("Torneo ").append(id).append(": ").append(nombre).append(" [").append(estado).append("]\n");
		sb.append("Participantes (").append(inscripciones.size()).append("/").append(cupos).append("):\n");
		for (InscripcionTorneo i : inscripciones) {
			sb.append("  - ").append(i.getNombre()).append(" (cupo ").append(i.getTipoCupo().toLowerCase())
					.append(")\n");
		}
		sb.append("Encuentros:\n");
		for (EncuentroTorneo e : encuentros) {
			sb.append("  ").append(e).append("\n");
		}
		if (!ABIERTO.equals(estado)) {
			sb.append("Posiciones:\n");
			int puesto = 1;
			for (InscripcionTorneo i : clasificacion()) {
				sb.append("  ").append(puesto++).append(". ").append(i.getNombre());
				if (!esIndividual()) {
					sb.append(" - ").append(puntos(i)).append(" pts");
				}
				sb.append("\n");
			}
		}
		if (FINALIZADO.equals(estado)) {
			sb.append("Ganador: ").append(getGanador().getNombre()).append("\n");
			for (String premio : premiosEntregados) {
				sb.append("Premio: ").append(premio).append("\n");
			}
		}
		return sb.toString();
	}
	
	// ------------------------------------------------------------ persistencia

		public int getRondaActual() {
			return rondaActual;
		}

		public ArrayList<InscripcionTorneo> getAvanzan() {
			return avanzan;
		}

		/** Persistencia: restaura el estado guardado sin ejecutar las reglas de negocio. */
		public void restaurar(String estado, int rondaActual, ArrayList<InscripcionTorneo> avanzan,
				ArrayList<String> premios) {
			if (!ABIERTO.equals(estado) && !EN_CURSO.equals(estado) && !FINALIZADO.equals(estado)) {
				throw new IllegalArgumentException("Estado de torneo desconocido: " + estado);
			}
			this.estado = estado;
			this.rondaActual = rondaActual;
			this.avanzan = avanzan;
			this.premiosEntregados = premios;
		}

}
