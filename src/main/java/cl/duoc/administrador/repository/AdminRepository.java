package cl.duoc.administrador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.administrador.model.Admin;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    // QUERY METHOD
    List<Admin> findByActivo(boolean activo);

    // CUSTOM QUERY
    @Query(
        value = "SELECT * FROM admins WHERE activo = :activo",
        nativeQuery = true
    )
    List<Admin> buscarPorEstado(
            @Param("activo") boolean activo
    );
}



