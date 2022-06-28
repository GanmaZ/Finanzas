package pe.edu.upc.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demo.entities.Bono;

public interface IBonoRepository extends JpaRepository<Bono, Integer> {
	
	@Query(value = "Select * from bono where id_bono=:id_bono", nativeQuery = true)
	public Bono findByBono(int id_bono);
	
	@Query(value = "Select * from bono where id_usuario=:id_usuario", nativeQuery = true)
	public List<Bono> findByid_usuario(int id_usuario);
}
