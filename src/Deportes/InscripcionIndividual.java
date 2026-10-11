package Deportes;

import Usuario.Socio;

public class InscripcionIndividual {
	private Socio socio;
	private DeporteIndividual deporte;
	private int rankingELO;

	public InscripcionIndividual(Socio socio, DeporteIndividual deporte) {
		this.socio = socio;
		this.deporte = deporte;
		this.rankingELO = 1000;
	}

	public int getRankingELO() {
		return rankingELO;
	}

	public void setRankingELO(int rankingELO) {
		this.rankingELO = rankingELO;
	}

}
