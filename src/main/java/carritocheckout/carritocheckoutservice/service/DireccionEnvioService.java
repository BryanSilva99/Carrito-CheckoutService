package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.DireccionEnvio;

import java.util.List;

public interface DireccionEnvioService {
    List<DireccionEnvio> obtenerPorUsuario(Integer idUsuario);
    DireccionEnvio actualizarDireccion(Integer idDireccion, DireccionEnvio direccion);
    void eliminarDireccion(Integer idDireccion);
    void marcarComoPrincipal(Integer idUsuario, Integer idDireccion);
}
