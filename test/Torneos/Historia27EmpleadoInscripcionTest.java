package Torneos;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Administradores.Turno;
import Deportes.Equipo;
import Deportes.Partido;
import Usuario.Entrenador;
import Usuario.Fisioterapeuta;
import Usuario.Socio;

// HU27 Empleado: inscribirse en un torneo individual en un dia sin turno. 
public class Historia27EmpleadoInscripcionTest {
	private ArrayList<Turno> turnos;
	private String diaDelTorneo;

	@BeforeEach
	public void setUp() {
		turnos = new ArrayList<Turno>();
		diaDelTorneo = Torneo.diaDeSemana(Fixture.INICIO);
	}

	@Test
	public void elDiaSeCalculaConElMismoFormatoQueLosTurnos() {
		assertEquals("LUNES", Torneo.diaDeSemana(Fixture.fecha("2027-03-01")));
		assertEquals("DOMINGO", Torneo.diaDeSemana(Fixture.fecha("2027-03-07")));
	}

	@Test
	public void sinTurnoEseDiaOcupaCupoRegular() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Entrenador e = Fixture.entrenador(50);
		String otroDia = diaDelTorneo.equals("MARTES") ? "MIERCOLES" : "MARTES";
		turnos.add(new Turno(e, otroDia, 8, 12));
		InscripcionTorneo i = t.inscribirParticipante(e, null, null, new ArrayList<Socio>(), turnos,
				Fixture.ANTES_DEL_LIMITE);
		assertEquals(InscripcionTorneo.REGULAR, i.getTipoCupo());
	}

	@Test
	public void conTurnoEseDiaSeRechazaIndicandoElTurno() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Fisioterapeuta f = new Fisioterapeuta(60, "Fisio", "fisio", "1234", Fixture.fecha("1988-01-01"),
				Fixture.fecha("2020-01-01"));
		turnos.add(new Turno(f, diaDelTorneo, 8, 12));
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> t.inscribirParticipante(f, null,
				null, new ArrayList<Socio>(), turnos, Fixture.ANTES_DEL_LIMITE));
		assertTrue(ex.getMessage().contains(diaDelTorneo));
		assertTrue(ex.getMessage().contains("8 a 12"));
	}

	@Test
	public void elTurnoDeOtroEmpleadoNoImpide() {
		Torneo t = Fixture.recreativoTenis(8, Torneo.SENCILLOS);
		Entrenador e = Fixture.entrenador(50);
		turnos.add(new Turno(Fixture.entrenador(51), diaDelTorneo, 8, 12));
		t.inscribirParticipante(e, null, null, new ArrayList<Socio>(), turnos, Fixture.ANTES_DEL_LIMITE);
		assertEquals(1, t.getInscripciones().size());
	}

	@Test
	public void torneoDeConjuntoSeRechaza() {
		Torneo t = Fixture.recreativoFutbol(8);
		Entrenador e = Fixture.entrenador(50);
		assertThrows(IllegalArgumentException.class, () -> t.inscribirParticipante(e, null, null,
				new ArrayList<Socio>(), turnos, Fixture.ANTES_DEL_LIMITE));
	}

	@Test
	public void competitivoNoCobraTarifaNiSumaAlRecaudado() {
		Torneo t = Fixture.competitivoTenis(8, Torneo.SENCILLOS, 40000);
		Entrenador e = Fixture.entrenador(50);
		InscripcionTorneo i = t.inscribirParticipante(e, null, null, new ArrayList<Socio>(), turnos,
				Fixture.ANTES_DEL_LIMITE);
		assertEquals(0, i.getTarifaPagada(), 0.0001);
		assertEquals(0, t.getTotalRecaudado(), 0.0001);
	}

	@Test
	public void yaInscritoLlenoOIniciadoSeRechaza() {
		Torneo t = Fixture.recreativoTenis(2, Torneo.SENCILLOS);
		Entrenador e = Fixture.entrenador(50);
		Entrenador otro = Fixture.entrenador(51);
		Entrenador tercero = Fixture.entrenador(52);
		t.inscribirParticipante(e, null, null, new ArrayList<Socio>(), turnos, Fixture.DESPUES_DEL_LIMITE);
		assertThrows(IllegalStateException.class, () -> t.inscribirParticipante(e, null, null,
				new ArrayList<Socio>(), turnos, Fixture.DESPUES_DEL_LIMITE));
		t.inscribirParticipante(otro, null, null, new ArrayList<Socio>(), turnos, Fixture.DESPUES_DEL_LIMITE);
		assertThrows(IllegalStateException.class, () -> t.inscribirParticipante(tercero, null, null,
				new ArrayList<Socio>(), turnos, Fixture.DESPUES_DEL_LIMITE));
		t.iniciar();
		assertThrows(IllegalStateException.class, () -> t.inscribirParticipante(tercero, null, null,
				new ArrayList<Socio>(), turnos, Fixture.DESPUES_DEL_LIMITE));
	}

	@Test
	public void unEmpleadoNoTieneRankingYNoOcupaCupoPreferencial() {
		Torneo t = Fixture.recreativoTenis(5, Torneo.SENCILLOS);
		for (int i = 0; i < 4; i++) {
			t.inscribirParticipante(Fixture.entrenador(70 + i), null, null, new ArrayList<Socio>(), turnos,
					Fixture.ANTES_DEL_LIMITE);
		}
		assertThrows(IllegalStateException.class, () -> t.inscribirParticipante(Fixture.entrenador(80), null, null,
				new ArrayList<Socio>(), turnos, Fixture.ANTES_DEL_LIMITE));
	}
}
