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

public void aprobarVendedor(Integer id) {
    System.out.println("📤 ADMIN enviando ID: " + id);

    webClient.put()
        .uri("http://localhost:8083/api/v1/vendedores/aprobar/{id}", id)
        .retrieve()
        .bodyToMono(Void.class)
        .block();

    System.out.println("📨 ADMIN respuesta recibida");
}

//rechazar
public void rechazarVendedor(Integer id) {

    System.out.println("📤 ADMIN rechazando VENDEDOR ID: " + id);

    webClient.put()
            .uri("http://localhost:8083/api/v1/vendedores/rechazar/{id}", id)
            .retrieve()
            .bodyToMono(Void.class)
            .block();

    System.out.println("📨 ADMIN recibió respuesta de rechazo");
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



