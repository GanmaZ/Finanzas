package pe.edu.upc.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.upc.demo.entities.Flujo;

public interface IFlujoRepository extends JpaRepository<Flujo, Integer> {

}
