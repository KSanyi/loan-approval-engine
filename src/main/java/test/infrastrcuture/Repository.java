package test.infrastrcuture;

import test.Entity;
import test.Factory;

public class Repository extends Factory {

    public Entity load() {
        return this.create("id", "name");
    }
    
}
