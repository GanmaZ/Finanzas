package pe.edu.upc.demo.serviceimplements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import pe.edu.upc.demo.entities.Bono;
import pe.edu.upc.demo.entities.Flujo;
import pe.edu.upc.demo.entities.Usuario;
import pe.edu.upc.demo.repositories.IBonoRepository;
import pe.edu.upc.demo.repositories.IFlujoRepository;
import pe.edu.upc.demo.repositories.IUsuarioRepository;
import pe.edu.upc.demo.serviceinterface.IBonoService;

@Service
public class BonoServiceImpl implements IBonoService {

	@Autowired
	private IBonoRepository bRepository;

	@Autowired
	private IUsuarioRepository userRepository;

	@Autowired
	private IFlujoRepository fRepository;

	@Override
	public void insert(Bono bono) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		UserDetails userDetail = (UserDetails) auth.getPrincipal();

		Usuario usuario = userRepository.findByUserName(userDetail.getUsername());

		bono.setUsuario(usuario);
		bRepository.save(bono);

		if ("semestral".equals(bono.getPeriodoPago())) {

			float tasacuponsem = (bono.getTasaCupon() / 100) / 2;
			int periodsem = (int) bono.getVencimiento() * 2;
			float flujo[][] = new float[5][periodsem];
			int aux = 0;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < periodsem; j++) {
					if (i == 0) {
						flujo[i][j] = aux;
						aux++;
					} else if (i == 1) {
						flujo[i][j] = bono.getValorNominal();

						if (j == (periodsem - 1)) {
							flujo[i][j] = 0;
						}

					} else if (i == 2 && j > 0 && j < (periodsem - 1)) {
						flujo[i][j] = 0;

					} else if (i == 2 && j == (periodsem - 1)) {
						flujo[i][j] = bono.getValorNominal();

					} else if (i == 3 && j > 0) {
						flujo[i][j] = bono.getValorNominal() * tasacuponsem;

					} else if (i == 4 && j == 0) {
						flujo[i][j] = -bono.getValorNominal();

					} else if (i == 4 && j > 0 && j < (periodsem - 1)) {
						flujo[i][j] = bono.getValorNominal() * tasacuponsem;

					} else if (i == 4 && j == (periodsem - 1)) {
						flujo[i][j] = (bono.getValorNominal() * tasacuponsem) + bono.getValorNominal();
					}

				}
			}

			for (int j = 0; j < periodsem; j++) {

				Flujo flu = new Flujo();
				flu.setPeriodo(flujo[0][j]);
				flu.setCapital(flujo[1][j]);
				flu.setAmortizacion(flujo[2][j]);
				flu.setInteres(flujo[3][j]);
				flu.setCuota(flujo[4][j]);
				flu.setBono(bono);

				fRepository.save(flu);

			}

		} else if ("mensual".equals(bono.getPeriodoPago())) {
			float tasacuponmen = (bono.getTasaCupon() / 100) / 12;
			int periodmen = (int) bono.getVencimiento() * 12;
			float flujo[][] = new float[5][periodmen];
			int aux = 0;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < periodmen; j++) {
					if (i == 0) {
						flujo[i][j] = aux;
						aux++;
					} else if (i == 1) {
						flujo[i][j] = bono.getValorNominal();

						if (j == (periodmen - 1)) {
							flujo[i][j] = 0;
						}

					} else if (i == 2 && j > 0 && j < (periodmen - 1)) {
						flujo[i][j] = 0;

					} else if (i == 2 && j == (periodmen - 1)) {
						flujo[i][j] = bono.getValorNominal();

					} else if (i == 3 && j > 0) {
						flujo[i][j] = bono.getValorNominal() * tasacuponmen;

					} else if (i == 4 && j == 0) {
						flujo[i][j] = -bono.getValorNominal();

					} else if (i == 4 && j > 0 && j < (periodmen - 1)) {
						flujo[i][j] = bono.getValorNominal() * tasacuponmen;

					} else if (i == 4 && j == (periodmen - 1)) {
						flujo[i][j] = (bono.getValorNominal() * tasacuponmen) + bono.getValorNominal();
					}

				}
			}
			for (int j = 0; j < periodmen; j++) {

				Flujo flu = new Flujo();
				flu.setPeriodo(flujo[0][j]);
				flu.setCapital(flujo[1][j]);
				flu.setAmortizacion(flujo[2][j]);
				flu.setInteres(flujo[3][j]);
				flu.setCuota(flujo[4][j]);
				flu.setBono(bono);

				fRepository.save(flu);

			}
		} else {
			float flujo[][] = new float[5][(int) bono.getVencimiento()];
			int aux = 0;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < bono.getVencimiento(); j++) {
					if (i == 0) {
						flujo[i][j] = aux;
						aux++;
					} else if (i == 1) {
						flujo[i][j] = bono.getValorNominal();

						if (j == (bono.getVencimiento() - 1)) {
							flujo[i][j] = 0;
						}

					} else if (i == 2 && j > 0 && j < (bono.getVencimiento() - 1)) {
						flujo[i][j] = 0;

					} else if (i == 2 && j == (bono.getVencimiento() - 1)) {
						flujo[i][j] = bono.getValorNominal();

					} else if (i == 3 && j > 0) {
						flujo[i][j] = bono.getValorNominal() * (bono.getTasaCupon() / 100);

					} else if (i == 4 && j == 0) {
						flujo[i][j] = -bono.getValorNominal();

					} else if (i == 4 && j > 0 && j < (bono.getVencimiento() - 1)) {
						flujo[i][j] = bono.getValorNominal() * (bono.getTasaCupon() / 100);

					} else if (i == 4 && j == (bono.getVencimiento() - 1)) {
						flujo[i][j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100)) + bono.getValorNominal();
					}

				}
			}
			for (int j = 0; j < bono.getVencimiento(); j++) {

				Flujo flu = new Flujo();
				flu.setPeriodo(flujo[0][j]);
				flu.setCapital(flujo[1][j]);
				flu.setAmortizacion(flujo[2][j]);
				flu.setInteres(flujo[3][j]);
				flu.setCuota(flujo[4][j]);
				flu.setBono(bono);

				fRepository.save(flu);

			}
		}
	

	}

	@Override
	public List<Bono> list() {
		return bRepository.findAll();
	}

}
