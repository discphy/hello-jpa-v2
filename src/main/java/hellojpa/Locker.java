package hellojpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Locker {

    @Id
    @GeneratedValue
    private long id;

    private String name;
}
