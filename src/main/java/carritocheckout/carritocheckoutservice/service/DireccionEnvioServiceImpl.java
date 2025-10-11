package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;
import carritocheckout.carritocheckoutservice.repository.DireccionEnvioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionEnvioServiceImpl implements DireccionEnvioService {

    private final DireccionEnvioRepository direccionRepository;

    public DireccionEnvioServiceImpl(DireccionEnvioRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    @Override
    public List<DireccionEnvio> obtenerPorUsuario(Integer idUsuario) {
        return direccionRepository.findByUsuarioEnvio_IdUsuario(idUsuario);
    }

    @Override
    public DireccionEnvio actualizarDireccion(Integer idDireccion, DireccionEnvio nueva) {
        DireccionEnvio actual = direccionRepository.findById(idDireccion)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        actual.setDireccionLinea1(nueva.getDireccionLinea1());
        actual.setDireccionLinea2(nueva.getDireccionLinea2());
        actual.setCiudad(nueva.getCiudad());
        actual.setProvincia(nueva.getProvincia());
        actual.setCodigoPostal(nueva.getCodigoPostal());
        actual.setPais(nueva.getPais());
        return direccionRepository.save(actual);
    }

    @Override
    public void eliminarDireccion(Integer idDireccion) {
        direccionRepository.deleteById(idDireccion);
    }

    @Override
    public void marcarComoPrincipal(Integer idUsuario, Integer idDireccion) {
        List<DireccionEnvio> direcciones = direccionRepository.findByUsuarioEnvio_IdUsuario(idUsuario);
        for (DireccionEnvio dir : direcciones) {
            dir.setPrincipal(dir.getId().equals(idDireccion));
        }
        direccionRepository.saveAll(direcciones);
    }
}
