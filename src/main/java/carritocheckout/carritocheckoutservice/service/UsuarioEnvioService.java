package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.UsuarioEnvio;
import carritocheckout.carritocheckoutservice.dtos.UsuarioEnvioDTO;
import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;

public interface UsuarioEnvioService {
    UsuarioEnvio crearUsuarioEnvio(UsuarioEnvio usuarioEnvio);
    UsuarioEnvioDTO obtenerPorIdUsuario(Integer idUsuario);
    UsuarioEnvio agregarDireccion(Integer idUsuario, DireccionEnvio direccion);
}
