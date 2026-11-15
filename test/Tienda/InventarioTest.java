package Tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventarioTest {
	private Inventario inventario;
	private Articulo camiseta;
	private Articulo balon;
	private Ubicacion bodega;
	private Ubicacion tienda;

	@BeforeEach
	public void setUp() {
		inventario = new Inventario();
		camiseta = new Articulo("Camiseta", 50000, "Ropa");
		balon = new Articulo("Balon", 80000, "Equipo");
		bodega = new Ubicacion("Bodega");
		tienda = new Ubicacion("Tienda");
	}

	@Test
	public void inventarioNuevoEstaVacio() {
		assertTrue(inventario.getExistencias().isEmpty());
		assertEquals(0, inventario.consultarDisponible(camiseta, bodega));
		assertNull(inventario.buscar(camiseta, bodega));
	}

	@Test
	public void registrarExistenciaNueva() {
		inventario.registrarExistencia(camiseta, bodega, 10);
		assertEquals(1, inventario.getExistencias().size());
		assertEquals(10, inventario.consultarDisponible(camiseta, bodega));
	}

	@Test
	public void registrarOtraVezSumaALaExistenciaSinDuplicar() {
		inventario.registrarExistencia(camiseta, bodega, 10);
		inventario.registrarExistencia(camiseta, bodega, 5);
		assertEquals(1, inventario.getExistencias().size());
		assertEquals(15, inventario.consultarDisponible(camiseta, bodega));
	}

	@Test
	public void registrarCeroCreaLaExistenciaPeroNoSuma() {
		inventario.registrarExistencia(camiseta, bodega, 0);
		assertEquals(1, inventario.getExistencias().size());
		assertEquals(0, inventario.consultarDisponible(camiseta, bodega));
		inventario.registrarExistencia(camiseta, bodega, 0);
		assertEquals(1, inventario.getExistencias().size());
	}

	@Test
	public void cantidadNegativaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> inventario.registrarExistencia(camiseta, bodega, -1));
		assertTrue(inventario.getExistencias().isEmpty());
	}

	@Test
	public void mismoArticuloEnDistintasUbicacionesSonExistenciasSeparadas() {
		inventario.registrarExistencia(camiseta, bodega, 10);
		inventario.registrarExistencia(camiseta, tienda, 3);
		assertEquals(2, inventario.getExistencias().size());
		assertEquals(10, inventario.consultarDisponible(camiseta, bodega));
		assertEquals(3, inventario.consultarDisponible(camiseta, tienda));
	}

	@Test
	public void distintosArticulosEnLaMismaUbicacion() {
		inventario.registrarExistencia(camiseta, bodega, 10);
		inventario.registrarExistencia(balon, bodega, 2);
		assertEquals(2, inventario.consultarDisponible(balon, bodega));
		assertEquals(10, inventario.consultarDisponible(camiseta, bodega));
	}

	@Test
	public void buscarComparaPorNombreYNoPorReferencia() {
		inventario.registrarExistencia(camiseta, bodega, 10);
		Articulo otraCamiseta = new Articulo("Camiseta", 1, "Otra");
		Ubicacion otraBodega = new Ubicacion("Bodega");
		assertNotNull(inventario.buscar(otraCamiseta, otraBodega));
		assertEquals(10, inventario.consultarDisponible(otraCamiseta, otraBodega));
	}

}
