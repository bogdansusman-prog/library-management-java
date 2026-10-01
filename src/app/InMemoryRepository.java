package app;

import java.util.*;
public class InMemoryRepository<ID, T extends Identifiable<ID>>{
    private Map<ID,T> storage = new HashMap<>();

    public void save(T entity){
        storage.put(entity.getId(),entity);
    }

    public Optional<T> findById(ID id){
        return Optional.ofNullable(storage.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(ID id) {
        storage.remove(id);
    }

    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

}
