package cl.duoc.administrador.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import cl.duoc.administrador.mapper.AdminMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import cl.duoc.administrador.dto.CreateRequestAdmin;
import cl.duoc.administrador.model.Admin;
import cl.duoc.administrador.service.AdminService;
import cl.duoc.administrador.dto.UpdateRequestAdmin;
import cl.duoc.administrador.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1/admin")


public class AdminController {

private final AdminService adminService;
private final WebClient  webClient;

public AdminController(
        AdminService adminService,
        WebClient webClient
) {
    this.adminService = adminService;
    this.webClient = webClient;
}

///Endpoint CRUD 


@GetMapping
public ResponseEntity<List<Admin>> listarAdmins() {
    List<Admin> admins = adminService.getAdmins();
    return ResponseEntity.ok(admins);
}
// CREAR ADMINISTRADOR

@PostMapping
public ResponseEntity<Map<String, Object>> agregarAdmin(
        @Valid @RequestBody CreateRequestAdmin request
) {

    Admin nuevoAdmin =
            adminService.saveAdmin(
                    AdminMapper.toAdmin(request)
            );

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Administrador creado correctamente");
    response.put("id", nuevoAdmin.getId());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
// BUSCAR ADMINISTRADOR POR ID

@GetMapping("/{id}")
public ResponseEntity<Admin> buscarAdmin(
        @PathVariable int id
) {

    Admin admin = adminService.getAdminId(id);

    if (admin == null) {
        throw new ResourceNotFoundException(
                "Administrador con id=" + id + " no encontrado"
        );
    }

    return ResponseEntity.ok(admin);
}
// ACTUALIZAR ADMINISTRADOR

@PutMapping("/{id}")
public ResponseEntity<Map<String, Object>> actualizarAdmin(
        @PathVariable int id,
        @Valid @RequestBody UpdateRequestAdmin request
) {

    Admin adminActualizado =
            adminService.updateAdmin(id, request);

    if (adminActualizado == null) {
        throw new ResourceNotFoundException(
                "Administrador con id=" + id + " no encontrado"
        );
    }

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Administrador actualizado correctamente");
    response.put("id", adminActualizado.getId());

    return ResponseEntity.ok(response);
}
// ELIMINAR ADMINISTRADOR

@DeleteMapping("/{id}")
public ResponseEntity<Map<String, String>> eliminarAdmin(
        @PathVariable int id
) {

    boolean eliminado = adminService.deleteAdmin(id);

    if (!eliminado) {
        throw new ResourceNotFoundException(
                "Administrador con id=" + id + " no encontrado"
        );
    }

    Map<String, String> response = new HashMap<>();
    response.put("mensaje", "Administrador eliminado correctamente");

    return ResponseEntity.ok(response);
}



//endpoint DE HISTRIASDE USUARIOS---------------

// APROBAR VENDEDOR con observaciones
@PutMapping("/vendedores/{id}/aprobar")
public ResponseEntity<Map<String, String>> aprobarVendedor(
        @PathVariable int id,
        @RequestBody Map<String, String> request // ◄--- Agregado para capturar el JSON de Postman
) {
    // Si en Postman no escribes nada, usará este texto por defecto
    String observaciones = request != null ? request.getOrDefault("observaciones", "Sin observaciones de administración") : "Aprobado";
    
    // Llamamos al método del servicio pasándole la observación
    adminService.aprobarVendedorConObservaciones(id, observaciones);

    Map<String, String> response = new HashMap<>();
    response.put("mensaje", "Proceso de aprobación enviado correctamente");
    response.put("observaciones", observaciones);

    return ResponseEntity.ok(response);
}

//endpoint de rechazar 

@PutMapping("/vendedores/{id}/rechazar")
public ResponseEntity<Map<String, String>> rechazarVendedor(
        @PathVariable int id,
        @RequestBody Map<String, String> request // ◄--- Agregado para capturar el JSON de Postman
) {
    String observaciones = request != null ? request.getOrDefault("observaciones", "Sin observaciones de administración") : "Rechazado";
    
    adminService.rechazarVendedorConObservaciones(id, observaciones);

    Map<String, String> response = new HashMap<>();
    response.put("mensaje", "Proceso de rechazo enviado correctamente");
    response.put("observaciones", observaciones);

    return ResponseEntity.ok(response);
}
//endpoint de reclamos de compradores 

@GetMapping("/reclamos")
public ResponseEntity<Map<String, Object>> obtenerReclamos() {

    List<String> reclamos = adminService.obtenerReclamos();

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Reclamos obtenidos correctamente");
    response.put("total", reclamos.size());
    response.put("reclamos", reclamos);

    return ResponseEntity.ok(response);
}

//endpoint de reportes semanales de ventas

@GetMapping("/reportes/semanales")
public ResponseEntity<Map<String, Object>> obtenerReporteSemanal() {

    Map<String, Object> reporte = adminService.obtenerReporteSemanal();

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Reporte semanal generado correctamente");
    response.put("reporte", reporte);

    return ResponseEntity.ok(response);
}
// Endpoint de administrador que llama a vendedor :  PUT http://localhost:8082/api/v1/admin/vendedores/1/aprobar
 public void aprobarVendedor(Integer id) {

    System.out.println("ADMIN: enviando solicitud al vendedor ID " + id);
webClient.put()
    .uri("http://localhost:8083/api/v1/vendedores/aprobar/{id}", id)
    .retrieve()
    .onStatus(status -> status.is4xxClientError(), response ->
            response.bodyToMono(String.class)
                    .flatMap(msg -> Mono.error(
                            new ResourceNotFoundException(msg)
                    ))
    )
    .onStatus(status -> status.is5xxServerError(), response ->
            response.bodyToMono(String.class)
                    .flatMap(msg -> Mono.error(
                            new RuntimeException(msg)
                    ))
    )
    .bodyToMono(Void.class)
    .block();
    
//PUT http://localhost:8082/api/v1/admin/vendedores/1/rechazar

}
}