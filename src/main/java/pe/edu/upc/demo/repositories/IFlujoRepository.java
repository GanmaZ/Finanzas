package pe.edu.upc.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demo.entities.Flujo;

public interface IFlujoRepository extends JpaRepository<Flujo, Integer> {
	
	@Query(value = "Select * from flujo where id_bono=:id_bono", nativeQuery = true)
	public List<Flujo> findByBono(int id_bono);
	
	@Query(value = "Delete from flujo where id_bono=:id_bono", nativeQuery = true)
	public void deleteByBono(int id_bono);
}
