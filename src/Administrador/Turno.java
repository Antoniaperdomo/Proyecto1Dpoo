package Administrador;
import java.util.Date;

import Usuario.Empleado;

public class Turno {
	private Empleado empleado;
	private String diaSemana;
	private Date horaInicio;
	private Date horaFin;
	public Turno(Empleado empleado, String diaSemana, Date horaInicio, Date horaFin) {
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
	public Date getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}
	public Date getHoraFin() {
		return horaFin;
	}
	public void setHoraFin(Date horaFin) {
		this.horaFin = horaFin;
	}
	
	
}
