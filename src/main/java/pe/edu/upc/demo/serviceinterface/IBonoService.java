package pe.edu.upc.demo.serviceinterface;

import java.util.List;

import pe.edu.upc.demo.entities.Bono;

public interface IBonoService {

	public void insert(Bono bono);
	
	public List<Bono> list();
}
