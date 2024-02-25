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

			member.getFavoriteFoods().add("치킨");
			member.getFavoriteFoods().add("족발");
			member.getFavoriteFoods().add("피자");

			member.getAddressHistory().add(new AddressEntity("old1", "street", "home"));
			member.getAddressHistory().add(new AddressEntity("old2", "street", "home"));

			em.persist(member);

			em.flush();
			em.clear();

			System.out.println("=============================");
			Member findMember = em.find(Member.class, member.getId());

			findMember.setHomeAddress(new Address("newCity", address.getStreet(), address.getZipcode()));

			findMember.getFavoriteFoods().remove("치킨");
			findMember.getFavoriteFoods().add("한식");

			findMember.getAddressHistory().remove(new AddressEntity("old1", "street", "home"));
			findMember.getAddressHistory().add(new AddressEntity("new1", "street", "home"));

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
