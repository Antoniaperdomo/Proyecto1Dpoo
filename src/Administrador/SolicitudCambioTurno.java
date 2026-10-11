package Administrador;

import Usuario.Empleado;

public class SolicitudCambioTurno {
	private Empleado empleado;
	private Turno TurnoActual;
	private Turno TurnoPropuesto;
	public static final String PENDIENTE = "pendiente";
	public static final  String APROBADA = "aprobada";
	public static final String RECHAZADA = "rechazada";
	private String estado;
	public SolicitudCambioTurno(Empleado empleado, Turno turnoActual, Turno turnoPropuesto) {
		this.empleado = empleado;
		this.TurnoActual = turnoActual;
		this.TurnoPropuesto = turnoPropuesto;
		this.estado = PENDIENTE;
	}
	public Empleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	public Turno getTurnoActual() {
		return TurnoActual;
	}
	public void setTurnoActual(Turno turnoActual) {
		TurnoActual = turnoActual;
	}
	public Turno getTurnoPropuesto() {
		return TurnoPropuesto;
	}
	public void setTurnoPropuesto(Turno turnoPropuesto) {
		TurnoPropuesto = turnoPropuesto;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		if ( (estado.equals(PENDIENTE)) || (estado.equals(APROBADA)) || (estado.equals(RECHAZADA)) ) {
			this.estado = estado;
		}
		else {
			throw new IllegalArgumentException("Estado inválido");
		}
	}
	
	
	
	
	
	
	
	

}
