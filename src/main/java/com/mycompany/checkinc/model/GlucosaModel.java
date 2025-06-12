package com.mycompany.checkinc.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import com.mycompany.checkinc.utils.DatabaseConnection;
import com.mycompany.checkinc.controller.RegistroGlucosa.RegistroGlucosaDTO;

public class GlucosaModel {
    private Connection conn;
    
    public GlucosaModel() {
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error de conexión", "No se pudo conectar a la base de datos");
        }
    }
    
    public List<RegistroGlucosaDTO> obtenerRegistros(int idUsuario) {
        List<RegistroGlucosaDTO> registros = new ArrayList<>();
        String sql = "SELECT id, nivel_glucosa, fecha_hora FROM registros_glucosa WHERE id_usuario = ? ORDER BY fecha_hora DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                registros.add(new RegistroGlucosaDTO(
                    rs.getInt("id"),
                    rs.getDouble("nivel_glucosa"),
                    rs.getTimestamp("fecha_hora")
                ));
            }
        } catch (SQLException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al obtener registros", e.getMessage());
        }
        
        return registros;
    }
    
    public boolean guardarRegistro(int idUsuario, Double nivelGlucosa, Date fechaHora) {
        String sql = "INSERT INTO registros_glucosa (id_usuario, nivel_glucosa, fecha_hora) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setDouble(2, nivelGlucosa);
            stmt.setTimestamp(3, new Timestamp(fechaHora.getTime()));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarRegistro(int id, Double nivelGlucosa, Date fechaHora) {
        String sql = "UPDATE registros_glucosa SET nivel_glucosa = ?, fecha_hora = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nivelGlucosa);
            stmt.setTimestamp(2, new Timestamp(fechaHora.getTime()));
            stmt.setInt(3, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar", e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarRegistro(int id) {
        String sql = "DELETE FROM registros_glucosa WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar", e.getMessage());
            return false;
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}
