package pe.edu.upc.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demo.entities.Indicador;

public interface IIndicadorRepository extends JpaRepository<Indicador, Integer> {

	@Query(value = "Select * from indicador where id_bono=:id_bono", nativeQuery = true)
	public Indicador findByBono(int id_bono);
	
	@Query(value = "Delete from indicador where id_bono=:id_bono", nativeQuery = true)
	public void deleteByBono(int id_bono);
}
