package com.RFF.VN;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {

	private static final String URL = "jdbc:mariadb://localhost:3307/VN_database?characterEncoding=UTF-8";
	private static final String USER = "alumno";
	private static final String PASS = "alumno";
	
	public static Connection obtenerConexion() {
		Connection con = null;
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			con = DriverManager.getConnection(URL,USER,PASS);
			System.out.println("¡CONEXIÓN EXITOSA CON DOCKER!");
			
		}catch (Exception e) {
			System.out.println("Error de conexión: " + e.getMessage());
			e.printStackTrace();
		}
		return con;
	}
	
}
