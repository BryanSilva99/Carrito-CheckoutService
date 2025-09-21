package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.ItemCarrito;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void agregarItem(ItemCarrito item) {
        itemRepository.save(item);
    }

    @Override
    public ItemCarrito buscarItemPorId(Integer id) {
        return itemRepository.findById(id).orElseThrow();
    }

    @Override
    public List<ItemCarrito> listaDeItems() {
        return itemRepository.findAll();
    }

    @Override
    public void actualziarItem(ItemCarrito item) {
        itemRepository.save(item);
    }

    @Override
    public void borrarItem(Integer id) {
        itemRepository.deleteById(id);
    }
}
