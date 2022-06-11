package pe.edu.upc.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.upc.demo.entities.Role;

public interface IRoleRepository extends JpaRepository<Role, Long>{

}
