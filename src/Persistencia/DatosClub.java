package Persistencia;

import java.util.ArrayList;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;
import Competencias.Competencia;
import Competencias.NominaCompetencia;
import Deportes.Equipo;
import Deportes.Instalacion;
import Deportes.Partido;
import Deportes.PartidoPractica;
import Entrenamiento.SesionEntrenamiento;
import Tienda.Inventario;
import Tienda.Producto;
import Tienda.RegistroVentas;
import Tienda.Ubicacion;
import Usuario.Empleado;
import Usuario.Entrenador;
import Usuario.Socio;
import Usuario.Usuario;

/**
 * Todo el estado del club en memoria. Es lo que se carga al iniciar y lo que
 * se guarda al terminar. Los objetos se relacionan entre sí por referencia; los
 * archivos los relacionan por llave (id, nombre) y el cargador reconstruye las
 * referencias.
 */
public class DatosClub {

	private final ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
	private final ArrayList<Equipo> equipos = new ArrayList<Equipo>();
	private final ArrayList<Partido> partidos = new ArrayList<Partido>();
	private final ArrayList<PartidoPractica> partidosPractica = new ArrayList<PartidoPractica>();
	private final ArrayList<Competencia> competencias = new ArrayList<Competencia>();
	private final ArrayList<NominaCompetencia> nominas = new ArrayList<NominaCompetencia>();
	private final ArrayList<Instalacion> instalaciones = new ArrayList<Instalacion>();
	private final ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
	private final ArrayList<Turno> turnos = new ArrayList<Turno>();
	private final ArrayList<SolicitudCambioTurno> solicitudes = new ArrayList<SolicitudCambioTurno>();
	private final ArrayList<SugerenciaProducto> sugerencias = new ArrayList<SugerenciaProducto>();
	private final ArrayList<Producto> productos = new ArrayList<Producto>();
	private final ArrayList<Ubicacion> ubicaciones = new ArrayList<Ubicacion>();
	private final Inventario inventario = new Inventario();
	private final RegistroVentas registroVentas = new RegistroVentas();

	public ArrayList<Usuario> getUsuarios() {
		return usuarios;
	}

	public ArrayList<Equipo> getEquipos() {
		return equipos;
	}

	public ArrayList<Partido> getPartidos() {
		return partidos;
	}

	public ArrayList<PartidoPractica> getPartidosPractica() {
		return partidosPractica;
	}

	public ArrayList<Competencia> getCompetencias() {
		return competencias;
	}

	public ArrayList<NominaCompetencia> getNominas() {
		return nominas;
	}

	public ArrayList<Instalacion> getInstalaciones() {
		return instalaciones;
	}

	public ArrayList<SesionEntrenamiento> getSesiones() {
		return sesiones;
	}

	public ArrayList<Turno> getTurnos() {
		return turnos;
	}

	public ArrayList<SolicitudCambioTurno> getSolicitudes() {
		return solicitudes;
	}

	public ArrayList<SugerenciaProducto> getSugerencias() {
		return sugerencias;
	}

	public ArrayList<Producto> getProductos() {
		return productos;
	}

	public ArrayList<Ubicacion> getUbicaciones() {
		return ubicaciones;
	}

	public Inventario getInventario() {
		return inventario;
	}

	public RegistroVentas getRegistroVentas() {
		return registroVentas;
	}

	// ---- búsquedas por llave (devuelven null si no existe) ----

	public Usuario buscarUsuario(int id) {
		for (Usuario u : usuarios) {
			if (u.getId() == id) {
				return u;
			}
		}
		return null;
	}

	public Usuario buscarUsuarioPorLogin(String login) {
		for (Usuario u : usuarios) {
			if (u.getLogin().equals(login)) {
				return u;
			}
		}
		return null;
	}

	public Socio buscarSocio(int id) {
		Usuario u = buscarUsuario(id);
		return (u instanceof Socio) ? (Socio) u : null;
	}

	public Empleado buscarEmpleado(int id) {
		Usuario u = buscarUsuario(id);
		return (u instanceof Empleado) ? (Empleado) u : null;
	}

	public Entrenador buscarEntrenador(int id) {
		Usuario u = buscarUsuario(id);
		return (u instanceof Entrenador) ? (Entrenador) u : null;
	}

	public Equipo buscarEquipo(String nombre) {
		for (Equipo e : equipos) {
			if (e.getNombre().equals(nombre)) {
				return e;
			}
		}
		return null;
	}

	public Competencia buscarCompetencia(String nombre) {
		for (Competencia c : competencias) {
			if (c.getNombre().equals(nombre)) {
				return c;
			}
		}
		return null;
	}

	public Instalacion buscarInstalacion(String nombre) {
		for (Instalacion i : instalaciones) {
			if (i.getNombre().equals(nombre)) {
				return i;
			}
		}
		return null;
	}

	public Producto buscarProducto(String nombre) {
		for (Producto p : productos) {
			if (p.getNombre().equals(nombre)) {
				return p;
			}
		}
		return null;
	}

	public Ubicacion buscarUbicacion(String nombre) {
		for (Ubicacion u : ubicaciones) {
			if (u.getNombre().equals(nombre)) {
				return u;
			}
		}
		return null;
	}
}
