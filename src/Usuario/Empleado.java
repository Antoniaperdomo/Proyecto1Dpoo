package Usuario;

import java.util.ArrayList;
import java.util.Date;

import Administradores.SolicitudCambioTurno;
import Administradores.SugerenciaProducto;
import Administradores.Turno;

public abstract class Empleado extends Usuario implements Comprador {

	private Date fechaContratacion;
	private int puntosFidelidad;

	public Empleado(int id, String nombre, String login, String password, Date fechaNacimiento,
			Date fechaContratacion) {
		super(id, nombre, login, password, fechaNacimiento);
		this.fechaContratacion = fechaContratacion;
	}

	public Date getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(Date fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}

	public void setPuntosFidelidad(int puntosFidelidad) {
		this.puntosFidelidad = puntosFidelidad;
	}

	public SolicitudCambioTurno solicitarCambioTurno(Turno turnoActual, Turno turnoSolicitado,
			ArrayList<SolicitudCambioTurno> todasLasSolicitudes) throws IllegalArgumentException {
		if (turnoActual.getEmpleado() != this || turnoSolicitado.getEmpleado() != this) {
			throw new IllegalArgumentException("El turno debe de ser del empleador que solicita el cambio");
		}
		SolicitudCambioTurno solicitud = new SolicitudCambioTurno(this, turnoActual, turnoSolicitado);
		todasLasSolicitudes.add(solicitud);
		return solicitud;
	}

	public SugerenciaProducto sugerirProducto(String nombre, String categoria,
			ArrayList<SugerenciaProducto> todasLasSugerencias) {
		SugerenciaProducto sugerencia = new SugerenciaProducto(nombre, categoria, this);
		todasLasSugerencias.add(sugerencia);
		return sugerencia;
	}

	public double getDescuentoPropio() {
		return 0.15;
	}
}
