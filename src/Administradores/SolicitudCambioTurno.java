package Administradores;

import Usuario.Empleado;

public class SolicitudCambioTurno {
	private Empleado empleado;
	private Turno turnoActual;
	private Turno turnoPropuesto;
	public static final String PENDIENTE = "pendiente";
	public static final String APROBADA = "aprobada";
	public static final String RECHAZADA = "rechazada";
	private String estado;

	public SolicitudCambioTurno(Empleado empleado, Turno turnoActual, Turno turnoPropuesto) {
		this.empleado = empleado;
		this.turnoActual = turnoActual;
		this.turnoPropuesto = turnoPropuesto;
		this.estado = PENDIENTE;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Turno getTurnoActual() {
		return turnoActual;
	}

	public void setTurnoActual(Turno turnoActual) {
		this.turnoActual = turnoActual;
	}

	public Turno getTurnoPropuesto() {
		return turnoPropuesto;
	}

	public void setTurnoPropuesto(Turno turnoPropuesto) {
		this.turnoPropuesto = turnoPropuesto;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		if ((estado.equals(PENDIENTE)) || (estado.equals(APROBADA)) || (estado.equals(RECHAZADA))) {
			this.estado = estado;
		} else {
			throw new IllegalArgumentException("Estado inválido");
		}
	}

}
