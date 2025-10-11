package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.UsuarioEnvio;
import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;

public interface UsuarioEnvioService {
    UsuarioEnvio crearUsuarioEnvio(UsuarioEnvio usuarioEnvio);
    UsuarioEnvio obtenerPorIdUsuario(Integer idUsuario);
    UsuarioEnvio agregarDireccion(Integer idUsuario, DireccionEnvio direccion);
}
