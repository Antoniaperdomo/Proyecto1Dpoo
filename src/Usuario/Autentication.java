package Usuario;

import java.util.ArrayList;

public class Autentication {
	
	public Usuario iniciarSesion(String login, String password, ArrayList<Usuario> todosLosUsuarios) {
		for(Usuario u:todosLosUsuarios) {
			if(u.getLogin().equals(login)) {
				throw new IllegalArgumentException("Credenciales incorrectas");
			}
			if (u instanceof Socio && !((Socio) u).isActivo()) {
				throw new IllegalStateException("El socio está dado de baja");
			}
			return u;
		}
		throw new IllegalArgumentException("Credenciales incorrectas");
	}

}
