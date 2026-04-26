package com.RFF.VN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Repository {

	//PANTALLA LOGIN
	
	//Intenta registrar un usuario: 
	//Return:
	//  - Id Usuario si tiene exito
	//  - -1 Si el nombre ya existe
	//  - -2 Error general
	public int registrarUsuario(String nombre, String password, String pregunta, String respuesta) {
	    String passwordHasheada = Seguridad.hashear(password);
	    String respuestaHasheada = Seguridad.hashear(respuesta); 
	    
	    String sql = "INSERT INTO USUARIO (Nombre_usuario, Password_usuario, Pregunta_seguridad, Respuesta_seguridad) VALUES (?, ?, ?, ?)";
	    
	    try (Connection con = ConexionBD.obtenerConexion();
	        PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        
	        stmt.setString(1, nombre);
	        stmt.setString(2, passwordHasheada);
	        stmt.setString(3, pregunta);
	        stmt.setString(4, respuestaHasheada);
	        
	        stmt.executeUpdate();
	        
	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) return rs.getInt(1); // Éxito: devuelve ID
	        
	    } catch (SQLException e) {
	        if (e.getErrorCode() == 1062) return -1; // Error: Usuario duplicado
	        e.printStackTrace();
	    }
	    return -2; // Error general
	}
	
	//Intenta registrar un usuario: 
	//Return:
	//  - Id Usuario si es correcto
	//  - 0 Si no existe/error
	//  - -1 Si la password es incorrecta
	public int validarLogin(String nombre, String password) {
		String sql = "SELECT Id_usuario, Password_usuario FROM USUARIO WHERE Nombre_usuario = ?";
		
		try(Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			
			stmt.setString(1, nombre);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				String hashBD = rs.getString("Password_usuario");
				if(Seguridad.verificar(password, hashBD)) {
					return rs.getInt("Id_usuario");
				}else {
					return -1; //Password erronea
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0; //Usuario no existe o error de conexion
	}
	
	//Obtener pregunta de usuario al intentar recuperar contraseña
	public String obtenerPreguntaUsuario(String nombreUsuario) {
        String sql = "SELECT Pregunta_seguridad FROM USUARIO WHERE Nombre_usuario = ?";
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Pregunta_seguridad");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no existe el usuario
    }
	
	//Verificar respuesta y actualizar contraseña
	public boolean actualizarPassword(String nombre, String respuestaEntrada, String nuevaPass) {
        String sql = "SELECT Respuesta_seguridad FROM USUARIO WHERE Nombre_usuario = ?";
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String respuestaBD = rs.getString("Respuesta_seguridad");
                
                if (Seguridad.verificar(respuestaEntrada, respuestaBD)) {
                   
                    String sqlUpdate = "UPDATE USUARIO SET Password_usuario = ? WHERE Nombre_usuario = ?";
                    try (PreparedStatement stmtUp = con.prepareStatement(sqlUpdate)) {
                        stmtUp.setString(1, Seguridad.hashear(nuevaPass));
                        stmtUp.setString(2, nombre);
                        return stmtUp.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	
	//PANTALLA MENU
	
	//Consultamos ultimo capitulo guardado
	public int obtenerUltimoCapitulo(int idUsuario) {
		int idCapitulo = 0;
		String sql = "SELECT Ult_capitulo FROM USUARIO WHERE Id_usuario = ?";
		
		try(Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
				
			stmt.setInt(1, idUsuario);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				int temp = rs.getInt("Ult_capitulo");
				if(!rs.wasNull()) {
					idCapitulo = temp;
				}
			}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		return idCapitulo;
	}
	
	//Ejecuta reset del progreso o actualiza el capitulo
	public void actualizarProgreso(int idUsuario, int idCapitulo) {
		String sql = "UPDATE USUARIO SET Ult_capitulo = ? WHERE Id_usuario = ?";
		try (Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			
			stmt.setInt(1, idCapitulo);
			stmt.setInt(2, idUsuario);
			stmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	//PANTALLA JUEGO
	
	public NarracionDTO obtenerNarracion(int id) {
	    String sql = "SELECT Id_narracion, Id_capitulo, Descripcion, Id_nar_post, Fondo, Personaje_Izq, Personaje_Der, Musica, Sonido_Efecto FROM NARRACION WHERE Id_narracion = ?";
	    try(Connection con = ConexionBD.obtenerConexion();
	        PreparedStatement stmt = con.prepareStatement(sql)){
	        
	        stmt.setInt(1, id);
	        ResultSet rs = stmt.executeQuery();
	        
	        if(rs.next()) {
	            String desc = rs.getString("Descripcion");
	            int cap = rs.getInt("Id_capitulo");
	            
	            int sig = rs.getInt("Id_nar_post");
	            Integer idSig = rs.wasNull() ? null : sig;
	         
	            String fondo = rs.getString("Fondo");
	            String pIzq = rs.getString("Personaje_Izq");
	            String pDer = rs.getString("Personaje_Der");
	            String musica = rs.getString("Musica");
	            String sonido = rs.getString("Sonido_Efecto");
 
	            return new NarracionDTO(desc, idSig, cap, fondo, pIzq, pDer, musica, sonido);
	        }
	    } catch(SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public int obtenerIdInicialPorCapitulo(int idCapitulo) {
		String sql = "SELECT Id_narracion FROM NARRACION WHERE Id_capitulo = ? ORDER BY Id_narracion ASC LIMIT 1";
		try (Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			
			stmt.setInt(1, idCapitulo);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("Id_narracion");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public List<OpcionDTO> obtenerOpciones(int idNarracionActual) {
		List<OpcionDTO> lista = new ArrayList<>();
		String sql = "SELECT o.Id_opcion, o.Descripcion, o.Id_narracion_post, l.Id_logro FROM OPCION o LEFT JOIN LOGRO l ON o.Id_opcion = l.Id_opcion WHERE o.Id_narracion_ant = ?";
		
		try(Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			
			stmt.setInt(1, idNarracionActual);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				int idLog = rs.getInt("Id_logro");
				Integer idLogroFinal = rs.wasNull() ? null : idLog;
				
				lista.add(new OpcionDTO(
					rs.getInt("Id_opcion"),
					rs.getString("Descripcion"),
					rs.getInt("Id_narracion_post"),
					idLogroFinal
				));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public boolean registrarLogro(int idUsuario, int idLogro) {
	    String sql = "INSERT IGNORE INTO LOGRO_CONSEGUIDO (Id_usuario, Id_logro, Fecha) VALUES (?, ?, CURRENT_TIMESTAMP)";
	    
	    try (Connection con = ConexionBD.obtenerConexion();
	         PreparedStatement stmt = con.prepareStatement(sql)) {
	        
	        stmt.setInt(1, idUsuario);
	        stmt.setInt(2, idLogro);
	        
	        int filasAfectadas = stmt.executeUpdate();
	        
	        return filasAfectadas > 0; 
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public String obtenerNombreLogro(int idLogro) {
	    String sql = "SELECT Nombre FROM LOGRO WHERE Id_logro = ?";
	    try (Connection con = ConexionBD.obtenerConexion();
	         PreparedStatement stmt = con.prepareStatement(sql)) {
	        stmt.setInt(1, idLogro);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) return rs.getString("Nombre");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "Logro Desbloqueado";
	}
	
	public List<LogroDetalleDTO> obtenerListaLogros(int idUsuario){
		List<LogroDetalleDTO> lista = new ArrayList<>();
		String sql = "SELECT l.Nombre, l.Descripcion, MIN(lc.Fecha) as Fecha FROM LOGRO l LEFT JOIN LOGRO_CONSEGUIDO lc ON l.Id_logro = lc.Id_logro AND lc.Id_usuario = ? GROUP BY l.Id_logro, l.Nombre, l.Descripcion ORDER BY l.Id_logro ASC";
		
		try(Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			
			stmt.setInt(1, idUsuario);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				String fecha = rs.getString("Fecha");
				boolean conseguido = (fecha != null);
				
				lista.add(new LogroDetalleDTO(
					rs.getString("Nombre"),
					rs.getString("Descripcion"),
					fecha,
					conseguido
				));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
		
	}
	
	//Guardar las opciones seleccionadas
	public void guardarDecision(int idUsuario, int idOpcion) {
		String sql = "INSERT INTO HISTORIAL_OPCIONES (Id_usuario, Id_opcion) VALUES (?,?)";
		try (Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setInt(1, idUsuario);
			stmt.setInt(2, idOpcion);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Borrar el historial de opciones cuando empezamos una nueva partida
	public void borrarHistorialUsuario(int idUsuario) {
		String sql = "DELETE FROM HISTORIAL_OPCIONES WHERE Id_usuario = ?";
		try (Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setInt(1, idUsuario);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Consultar las opciones elegidas para calcular el final
	public boolean haElegidoOpcion(int idUsuario, int idOpcionBuscada) {
		String sql = "SELECT 1 FROM HISTORIAL_OPCIONES WHERE Id_usuario = ? AND Id_opcion = ? LIMIT 1";
		try (Connection con = ConexionBD.obtenerConexion();
			PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setInt(1, idUsuario);
			stmt.setInt(2, idOpcionBuscada);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	} 
	
	
	
	
	
}
