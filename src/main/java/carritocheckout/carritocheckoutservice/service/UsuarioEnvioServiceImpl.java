package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.dtos.UsuarioEnvioDTO;
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
    public UsuarioEnvioDTO obtenerPorIdUsuario(Integer idUsuario) {
        UsuarioEnvio usuario = usuarioEnvioRepository.findByIdUsuario(idUsuario).orElse(null);
        if (usuario == null)
            return null;

        return new UsuarioEnvioDTO(
                usuario.getId(),
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                usuario.getTelefono());
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
