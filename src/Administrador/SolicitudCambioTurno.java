package Administrador;
import java.util.Date;

import Usuario.Empleado;

public class SolicitudCambioTurno {
	private Empleado empleado;
	private Date TurnoActual;
	private Date TurnoPropuesto;
	private static final String PENDIENTE = "pendiente";
	private static final  String APROBADA = "aprovada";
	private static final String RECHAZADA = "rechazada";
	private String estado;
	public SolicitudCambioTurno(Empleado empleado, Date turnoActual, Date turnoPropuesto, String estado) {
		super();
		this.empleado = empleado;
		this.TurnoActual = turnoActual;
		this.TurnoPropuesto = turnoPropuesto;
		this.estado = estado;
	}
	public Empleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	public Date getTurnoActual() {
		return TurnoActual;
	}
	public void setTurnoActual(Date turnoActual) {
		TurnoActual = turnoActual;
	}
	public Date getTurnoPropuesto() {
		return TurnoPropuesto;
	}
	public void setTurnoPropuesto(Date turnoPropuesto) {
		TurnoPropuesto = turnoPropuesto;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		if ( (estado != PENDIENTE) || (estado != APROBADA) || (estado != RECHAZADA) ) {
			
		}
	}
	
	
	
	
	
	
	
	

}
