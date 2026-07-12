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



// 🟢 IMPORTACIONES DE OPENAPI / SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final WebClient webClient;

    public AdminController(AdminService adminService, WebClient webClient) {
        this.adminService = adminService;
        this.webClient = webClient;
    }

    // --- ENDPOINTS CRUD BASICOS ---

    // LISTAR ADMINISTRADORES
    @GetMapping
    @Operation(summary = "Listar administradores", description = "Recupera una lista con todos los usuarios administradores del sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de administradores obtenida con éxito")
    public ResponseEntity<List<Admin>> listarAdmins() {
        List<Admin> admins = adminService.getAdmins();
        return ResponseEntity.ok(admins);
    }

    // CREAR ADMINISTRADOR
    @PostMapping
    @Operation(summary = "Crear administrador", description = "Registra un nuevo usuario con privilegios de administración.")
    @ApiResponse(responseCode = "201", description = "Administrador creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<Map<String, Object>> agregarAdmin(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para registrar un administrador",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateRequestAdmin.class),
                examples = @ExampleObject(
                    name = "Ejemplo Nuevo Admin",
                    value = "{\n  \"nombre\": \"Carlos Silva\",\n  \"email\": \"carlos.admin@tienda.cl\",\n  \"rol\": \"SUPER_ADMIN\"\n}"
                )
            )
        )
        @Valid @RequestBody CreateRequestAdmin request
    ) {
        Admin nuevoAdmin = adminService.saveAdmin(AdminMapper.toAdmin(request));
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Administrador creado correctamente");
        response.put("id", nuevoAdmin.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // BUSCAR ADMINISTRADOR POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar administrador por ID", description = "Obtiene los detalles del perfil de un administrador usando su ID.")
    @ApiResponse(responseCode = "200", description = "Administrador encontrado")
    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    public ResponseEntity<Admin> buscarAdmin(@PathVariable int id) {
        Admin admin = adminService.getAdminId(id);
        if (admin == null) {
            throw new ResourceNotFoundException("Administrador con id=" + id + " no encontrado");
        }
        return ResponseEntity.ok(admin);
    }

    // ACTUALIZAR ADMINISTRADOR
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar administrador", description = "Permite modificar los datos de un administrador existente.")
    @ApiResponse(responseCode = "200", description = "Administrador actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    public ResponseEntity<Map<String, Object>> actualizarAdmin(
        @PathVariable int id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Campos editables del administrador",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateRequestAdmin.class),
                examples = @ExampleObject(
                    name = "Ejemplo Modificar Admin",
                    value = "{\n  \"nombre\": \"Carlos Silva Editado\",\n  \"rol\": \"ADMIN_SOPORTE\"\n}"
                )
            )
        )
        @Valid @RequestBody UpdateRequestAdmin request
    ) {
        Admin adminActualizado = adminService.updateAdmin(id, request);
        if (adminActualizado == null) {
            throw new ResourceNotFoundException("Administrador con id=" + id + " no encontrado");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Administrador actualizado correctamente");
        response.put("id", adminActualizado.getId());
        return ResponseEntity.ok(response);
    }

    // ELIMINAR ADMINISTRADOR
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar administrador", description = "Remueve permanentemente el perfil de administrador del sistema.")
    @ApiResponse(responseCode = "200", description = "Administrador eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    public ResponseEntity<Map<String, String>> eliminarAdmin(@PathVariable int id) {
        boolean eliminado = adminService.deleteAdmin(id);
        if (!eliminado) {
            throw new ResourceNotFoundException("Administrador con id=" + id + " no encontrado");
        }
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Administrador eliminado correctamente");
        return ResponseEntity.ok(response);
    }


    // --- ENDPOINTS DE HISTORIAS DE USUARIO ---

    // APROBAR VENDEDOR CON OBSERVACIONES
    @PutMapping("/vendedores/{id}/aprobar")
    @Operation(summary = "Aprobar postulación de vendedor", description = "Cambia el estado de un vendedor a APROBADO e inyecta las observaciones pertinentes.")
    @ApiResponse(responseCode = "200", description = "Proceso de aprobación enviado correctamente")
    public ResponseEntity<Map<String, String>> aprobarVendedor(
        @PathVariable int id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Observaciones opcionales para la aprobación",
            required = false,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Ejemplo Observación Aprobación",
                    value = "{\n  \"observaciones\": \"Toda la documentación legal está en regla y vigente.\"\n}"
                )
            )
        )
        @RequestBody(required = false) Map<String, String> request
    ) {
        String observaciones = request != null ? request.getOrDefault("observaciones", "Sin observaciones de administración") : "Aprobado";
        adminService.aprobarVendedorConObservaciones(id, observaciones);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Proceso de aprobación enviado correctamente");
        response.put("observaciones", observaciones);
        return ResponseEntity.ok(response);
    }

    // RECHAZAR VENDEDOR CON OBSERVACIONES
    @PutMapping("/vendedores/{id}/rechazar")
    @Operation(summary = "Rechazar postulación de vendedor", description = "Cambia el estado de un vendedor a RECHAZADO agregando el motivo del rechazo en las observaciones.")
    @ApiResponse(responseCode = "200", description = "Proceso de rechazo enviado correctamente")
    public ResponseEntity<Map<String, String>> rechazarVendedor(
        @PathVariable int id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Motivo de rechazo de la postulación",
            required = false,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Ejemplo Observación Rechazo",
                    value = "{\n  \"observaciones\": \"El documento tributario adjunto no corresponde al RUT ingresado.\"\n}"
                )
            )
        )
        @RequestBody(required = false) Map<String, String> request
    ) {
        String observaciones = request != null ? request.getOrDefault("observaciones", "Sin observaciones de administración") : "Rechazado";
        adminService.rechazarVendedorConObservaciones(id, observaciones);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Proceso de rechazo enviado correctamente");
        response.put("observaciones", observaciones);
        return ResponseEntity.ok(response);
    }

   // OBTENER RECLAMOS DE COMPRADORES
    @GetMapping("/reclamos")
    @Operation(summary = "Obtener reclamos de compradores", description = "Se conecta de forma síncrona con el microservicio externo de Carlos para listar todos los reclamos reales.")
    @ApiResponse(responseCode = "200", description = "Reclamos obtenidos correctamente")
    @ApiResponse(responseCode = "500", description = "Error de comunicación con el servicio de Reclamos")
    public ResponseEntity<Map<String, Object>> obtenerReclamos() {
        try {
            System.out.println("🛰️ Consultando microservicio externo de Reclamos en Render...");

            // LLAMADA SÍNCRONA AL SERVICIO DE CARLOS
            List<?> listaReclamos = webClient.get()
                    .uri("https://reclamo-service.onrender.com/api/reclamos")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block(); // .block() hace que espere la respuesta de internet

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reclamos obtenidos correctamente desde el servicio externo");
            response.put("total", listaReclamos != null ? listaReclamos.size() : 0);
            response.put("reclamos", listaReclamos);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ Error al conectar con Reclamos: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error de comunicación con el servicio de Reclamos externo");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // OBTENER REPORTES SEMANALES DE VENTAS
    @GetMapping("/reportes/semanales")
    @Operation(summary = "Obtener reporte semanal de ventas", description = "Genera y extrae las métricas globales de facturación y ventas de la última semana.")
    @ApiResponse(responseCode = "200", description = "Reporte semanal generado correctamente")
    public ResponseEntity<Map<String, Object>> obtenerReporteSemanal() {
        Map<String, Object> reporte = adminService.obtenerReporteSemanal();
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Reporte semanal generado correctamente");
        response.put("reporte", reporte);
        return ResponseEntity.ok(response);
    }

    // MÉTODO DE APOYO INTERNO (Sigue igual, no es un endpoint expuesto mediante HTTP anotado)
    public void aprobarVendedor(Integer id) {
        System.out.println("ADMIN: enviando solicitud al vendedor ID " + id);
        webClient.put()
            .uri("http://localhost:8083/api/v1/vendedores/aprobar/{id}", id)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), response ->
                    response.bodyToMono(String.class)
                            .flatMap(msg -> Mono.error(new ResourceNotFoundException(msg)))
            )
            .onStatus(status -> status.is5xxServerError(), response ->
                    response.bodyToMono(String.class)
                            .flatMap(msg -> Mono.error(new RuntimeException(msg)))
            )
            .bodyToMono(Void.class)
            .block();
    }
}