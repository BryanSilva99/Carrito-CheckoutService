package carritocheckout.carritocheckoutservice.service;

import carritocheckout.carritocheckoutservice.entities.Item;
import carritocheckout.carritocheckoutservice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void agregarItem(Item item) {
        itemRepository.save(item);
    }

    @Override
    public Item buscarItemPorId(Integer id) {
        return itemRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Item> listaDeItems() {
        return itemRepository.findAll();
    }

    @Override
    public void actualziarItem(Item item) {
        itemRepository.save(item);
    }

    @Override
    public void borrarItem(Integer id) {
        itemRepository.deleteById(id);
    }
}
