package pe.edu.upc.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.upc.demo.entities.Bono;

public interface IBonoRepository extends JpaRepository<Bono, Integer> {

}
