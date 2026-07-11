package cl.duoc.administrador.service;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.administrador.model.Admin;
import cl.duoc.administrador.repository.AdminRepository;
import cl.duoc.administrador.dto.UpdateRequestAdmin;
import cl.duoc.administrador.exception.ResourceNotFoundException;
import cl.duoc.administrador.mapper.AdminMapper;


@Service
public class AdminService {

    private AdminRepository adminRepository;
    private final WebClient webClient;

    public AdminService(
            AdminRepository adminRepository,
            WebClient webClient
    ) {
        this.adminRepository = adminRepository;
        this.webClient = webClient;
    }

    // LISTAR
    public List<Admin> getAdmins() {
        return adminRepository.findAll();
    }

    // GUARDAR
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // BUSCAR POR ID
    public Admin getAdminId(int id) {
        return adminRepository.findById(id)
                .orElse(null);
    }

    // ACTUALIZAR
    public Admin updateAdmin(
            int id,
            UpdateRequestAdmin request
    ) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Administrador no encontrado"));

        AdminMapper.updateAdmin(admin, request);

        return adminRepository.save(admin);
    }

    // ELIMINAR
    public boolean deleteAdmin(int id) {

        Optional<Admin> adminOpt =
                adminRepository.findById(id);

        if (adminOpt.isPresent()) {

            adminRepository.delete(adminOpt.get());
            return true;

        } else {

            throw new ResourceNotFoundException(
                    "Administrador con id=" + id + " no encontrado"
            );
        }
    }


// APROBAR VENDEDOR
public void aprobarVendedorConObservaciones(Integer id, String observaciones) {
    System.out.println("ADMIN: enviando solicitud de aprobación al vendedor ID " + id);
    
    // Creamos un mapa para empaquetar la observación en un JSON
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("observaciones", observaciones);

    webClient.put()
        .uri("https://vendedor-service.onrender.com/api/v1/vendedores/aprobar/{id}", id) 
        .bodyValue(requestBody) 
        .retrieve()
        .bodyToMono(Void.class)
        .block();
        System.out.println(" ADMIN: La solicitud de aprobación para el vendedor ID " + id + " fue enviada con éxito.");
}

//rechazar
public void rechazarVendedorConObservaciones(Integer id, String observaciones) {

    System.out.println("📤 ADMIN rechazando solicitud del VENDEDOR ID: " + id);
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("observaciones", observaciones);
    webClient.put()
        .uri("https://vendedor-service.onrender.com/api/v1/vendedores/rechazar/{id}", id)
        .bodyValue(requestBody)
        .retrieve()
        .bodyToMono(Void.class)
        .block();

    System.out.println(" ADMIN:El Vendedor ID " + id + " fue rechazado con éxito .");
}

public List<String> obtenerReclamos() {

    List<String> reclamos = new ArrayList<>();

    reclamos.add("Pedido llegó tarde");
    reclamos.add("Producto defectuoso");
    reclamos.add("Cliente reporta error en pago");
    reclamos.add("Producto no recibido");

    return reclamos;
}

public Map<String, Object> obtenerReporteSemanal() {

    Map<String, Object> reporte = new HashMap<>();

    reporte.put("ventas", 150);
    reporte.put("ingresos", 750000);
    reporte.put("nuevosUsuarios", 32);

    return reporte;
}

}



