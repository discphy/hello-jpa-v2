package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Address address = new Address("city", "street", "home");

			Member member = new Member();
			member.setUsername("hello");
			member.setHomeAddress(address);
			em.persist(member);

			Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());

			Member member2 = new Member();
			member2.setUsername("hello2");
			member2.setHomeAddress(copyAddress);
			em.persist(member2);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}

		emf.close();
	}
}
