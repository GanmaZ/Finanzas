package pe.edu.upc.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demo.entities.Valoracion;

public interface IValoracionRepository extends JpaRepository<Valoracion, Integer> {

	@Query(value = "Select * from valoracion where id_bono=:id_bono", nativeQuery = true)
	public Valoracion findByValoracion(int id_bono);
	
	@Query(value = "Delete from valoracion where id_bono=:id_bono", nativeQuery = true)
	public void deleteByBono(int id_bono);
}
