package cl.duoc.administrador.mapper;

import cl.duoc.administrador.model.Admin;
import cl.duoc.administrador.dto.CreateRequestAdmin;
import cl.duoc.administrador.dto.UpdateRequestAdmin;

public class AdminMapper {

    // CREATE
    public static Admin toAdmin(CreateRequestAdmin request){

        Admin admin = new Admin();

        admin.setNombre(request.nombre());
        admin.setCorreo(request.correo());
        admin.setPassword(request.password());
        admin.setActivo(request.activo());

        return admin;
    }

    // UPDATE
    public static void updateAdmin(
            Admin admin,
            UpdateRequestAdmin request
    ){

        admin.setNombre(request.nombre());
        admin.setCorreo(request.correo());
        admin.setPassword(request.password());
        admin.setActivo(request.activo());
    }
}

