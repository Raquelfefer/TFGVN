package com.RFF.VN;

import org.mindrot.jbcrypt.BCrypt;

public class Seguridad {
	// Convierte la contraseña a formato Hash
	public static String hashear(String passwordPlano) {
		return BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
	}
	
	// Compara el texto introducido con el hash de base de datos
	public static boolean verificar(String passwordPlano, String hashed) {
		try {
			return BCrypt.checkpw(passwordPlano, hashed);
		} catch (Exception e) {
			return false;
		}
	}
}
