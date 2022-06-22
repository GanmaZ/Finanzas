package pe.edu.upc.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demo.entities.Bono;

public interface IBonoRepository extends JpaRepository<Bono, Integer> {
	@Query(value = "Select * from bono where id_bono=id_bono", nativeQuery = true)
	public List<Bono> findByidBono(int id_bono);
}
