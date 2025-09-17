package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.ItemCarrito;

import java.util.List;

public interface ItemService {
    void agregarItem(ItemCarrito item);
    ItemCarrito buscarItemPorId(Integer id);
    List<ItemCarrito> listaDeItems();
    void actualziarItem(ItemCarrito item);
    void borrarItem(Integer id);
}
