package Administrador;
import java.util.Date;

import Usuario.Empleado;

public class Turno {
	private Empleado empleado;
	private String diaSemana;
	private int horaInicio;
	private int horaFin;
	public Turno(Empleado empleado, String diaSemana, int horaInicio, int horaFin) throws IllegalArgumentException{
		if (horaInicio >= horaFin) {
		    throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin");
		}
		this.empleado = empleado;
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}
	public Empleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	public String getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}
	public int getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}
	public int getHoraFin() {
		return horaFin;
	}
	public void setHoraFin(int horaFin) {
		this.horaFin = horaFin;
	}
	
	public boolean seCruzaCon(Turno otroTurno) {
		if (!this.diaSemana.equals(otroTurno.diaSemana)) {
			return false;
		}
		else {
			return this.horaInicio < otroTurno.horaFin && otroTurno.horaInicio < this.horaFin;
			
		}
		
	}
	
	
}
