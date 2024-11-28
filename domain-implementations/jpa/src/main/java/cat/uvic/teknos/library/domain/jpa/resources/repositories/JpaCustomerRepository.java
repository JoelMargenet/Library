package cat.uvic.teknos.library.domain.jpa.resources.repositories;

import cat.uvic.teknos.library.domain.jpa.resources.models.Customer;
import cat.uvic.teknos.library.repositories.CustomerRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaCustomerRepository implements CustomerRepository {

    private final EntityManager entityManager;

    public JpaCustomerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.Customer model) {
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else if (!entityManager.contains(model)) {
                var customer = entityManager.find(Customer.class, model.getId());
                if (model.getFirstName() == null || model.getFirstName().isEmpty()) {
                    model.setFirstName(customer.getFirstName());
                }
                if (model.getLastName() == null || model.getLastName().isEmpty()) {
                    model.setLastName(customer.getLastName());
                }
                if (model.getEmail() == null || model.getEmail().isEmpty()) {
                    model.setEmail(customer.getEmail());
                }
                if (model.getAddress() == null || model.getAddress().isEmpty()) {
                    model.setAddress(customer.getAddress());
                }
                if (model.getPhoneNumber() == null || model.getPhoneNumber().isEmpty()) {
                    model.setPhoneNumber(customer.getPhoneNumber());
                }
                if (model.getLoans().isEmpty()) {
                    model.setLoans(customer.getLoans());
                }
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Customer model) {
        try {
            entityManager.getTransaction().begin();
            cat.uvic.teknos.library.models.Customer customer = entityManager.find(Customer.class, model.getId());
            entityManager.remove(customer);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("ERROR");
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Customer get(Integer id) {
        return entityManager.find(Customer.class, id);
    }

    @Override
    public Set<cat.uvic.teknos.library.models.Customer> getAll() {
        List<cat.uvic.teknos.library.models.Customer> customerList = entityManager.createQuery("SELECT c FROM Customer c", cat.uvic.teknos.library.models.Customer.class).getResultList();
        return new HashSet<>(customerList);
    }
}