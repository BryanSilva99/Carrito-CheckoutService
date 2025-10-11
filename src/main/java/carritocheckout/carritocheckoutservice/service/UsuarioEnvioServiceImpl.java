package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;
import carritocheckout.carritocheckoutservice.entities.UsuarioEnvio;
import carritocheckout.carritocheckoutservice.repository.UsuarioEnvioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioEnvioServiceImpl implements UsuarioEnvioService {

    private final UsuarioEnvioRepository usuarioEnvioRepository;

    public UsuarioEnvioServiceImpl(UsuarioEnvioRepository usuarioEnvioRepository) {
        this.usuarioEnvioRepository = usuarioEnvioRepository;
    }

    @Override
    public UsuarioEnvio crearUsuarioEnvio(UsuarioEnvio usuarioEnvio) {
        return usuarioEnvioRepository.save(usuarioEnvio);
    }

    @Override
    public UsuarioEnvio obtenerPorIdUsuario(Integer idUsuario) {
        return usuarioEnvioRepository.findByIdUsuario(idUsuario).orElse(null);
    }

    @Override
    public UsuarioEnvio agregarDireccion(Integer idUsuario, DireccionEnvio direccion) {
        UsuarioEnvio usuario = usuarioEnvioRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario de envío no encontrado"));

        if (direccion.isPrincipal()) {
            for (DireccionEnvio dir : usuario.getDirecciones()) {
                dir.setPrincipal(false);
            }
        }

        direccion.setUsuarioEnvio(usuario);
        usuario.getDirecciones().add(direccion);

        return usuarioEnvioRepository.save(usuario);
    }
}
