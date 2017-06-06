package test;

public class Factory {

    public Entity createNem(String name) {
        return new Entity("id1", name);
    }
    
    protected Entity create(String id, String name) {
        return new Entity(id, name);
    }
    
}
