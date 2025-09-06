package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Item;

import java.util.List;

public interface ItemService {
    void agregarItem(Item item);
    Item buscarItemPorId(Integer id);
    List<Item> listaDeItems();
    void actualziarItem(Item item);
    void borrarItem(Integer id);
}
