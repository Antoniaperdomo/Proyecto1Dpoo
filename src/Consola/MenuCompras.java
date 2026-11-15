package Consola;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import Tienda.Articulo;
import Tienda.CodigoDescuento;
import Tienda.ItemVenta;
import Tienda.Producto;
import Tienda.ProductoCafeteria;
import Tienda.Ubicacion;
import Tienda.Venta;
import Tienda.VentaCafeteria;
import Tienda.VentaTienda;
import Persistencia.DatosClub;
import Usuario.Comprador;
import Usuario.Empleado;
import Usuario.Usuario;

public class MenuCompras {

	private final Entrada entrada;
	private final DatosClub datos;
	private final Usuario comprador;

	public MenuCompras(Entrada entrada, DatosClub datos, Usuario comprador) {
		super();
		this.entrada = entrada;
		this.datos = datos;
		this.comprador = comprador;
	}
	
	private ArrayList<ItemVenta> armarItems(boolean esTienda){
		ArrayList<Producto> disponibles = new ArrayList<Producto>();
		ArrayList<String> descripciones = new ArrayList<String>();
		for(Producto p : datos.getProductos()) {
			boolean sirve;
			if (esTienda) {
				sirve = p instanceof Articulo;
			}else {
				sirve = p instanceof ProductoCafeteria;
			}
			if (sirve) {
				disponibles.add(p);
				descripciones.add(p.getNombre() + " - $" + p.getPrecio());
			}
		}
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		boolean seguir = true;
		while (seguir)
	}	
	
	
	
	
	public void comprar() {
		String[] lugares = { "Tienda", "Cafetería" };
		int lugar = entrada.elegirOpccion("¿Dónde desea comprar?", lugares);
		if(lugar == 0) {
			return;
		}
		boolean esTienda = (lugar == 1);
		
		ArrayList<ItemVenta> items = armarItems(esTienda);
	}

}
