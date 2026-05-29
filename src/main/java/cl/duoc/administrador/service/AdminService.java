package cl.duoc.administrador.service;
import java.util.List;
import java.util.Optional;
import java.util.Map;
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

public void aprobarVendedor(int vendedorId) {

    webClient.put()
            .uri("http://localhost:8082/api/v1/vendedores/"
                    + vendedorId + "/aprobar")
            .retrieve()
            .bodyToMono(Void.class)
            .block();
}

}



