package cl.duoc.administrador.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UpdateRequestAdmin(

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    String correo,

    @NotNull(message = "debe indicar si el admin está activo o no")
    Boolean activo

) {}


