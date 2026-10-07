package Deportes;

public class EstadisticasTenisYPadel extends EstadisticaPractica{
	private int gamesGanados;
	private int aces;
	private int erroresNoForzados;
	
	public EstadisticasTenisYPadel() {
		super();
	}
	public int getGamesGanados() {
		return gamesGanados;
	}
	public void setGamesGanados(int gamesGanados) {
		this.gamesGanados = gamesGanados;
	}
	public int getAces() {
		return aces;
	}
	public void setAces(int aces) {
		this.aces = aces;
	}
	public int getErroresNoForzados() {
		return erroresNoForzados;
	}
	public void setErroresNoForzado(int erroresNoForzado) {
		this.erroresNoForzados = erroresNoForzado;
	}
	@Override
	public String resumen() {
		return getSetsGanados() + " sets, " + gamesGanados + " games, " + aces + " aces, " + erroresNoForzados + "errores no forzados";
	}
	
	

}
