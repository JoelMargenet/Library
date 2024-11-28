package cat.uvic.teknos.library.domain.jpa.resources.repositories;

import cat.uvic.teknos.library.domain.jpa.resources.models.Loan;
import cat.uvic.teknos.library.repositories.LoanRepository;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaLoanRepository implements LoanRepository {

    private final EntityManager entityManager;

    public JpaLoanRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(cat.uvic.teknos.library.models.Loan model) {
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else if (!entityManager.contains(model)) {
                var loan = entityManager.find(Loan.class, model.getId());
                if (model.getLoanDate() == null) {
                    model.setLoanDate(loan.getLoanDate());
                }
                if (model.getReturnDate() == null) {
                    model.setReturnDate(loan.getReturnDate());
                }
                if (model.getBook() == null) {
                    model.setBook(loan.getBook());
                }
                if (model.getCustomer() == null) {
                    model.setCustomer(loan.getCustomer());
                }
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void delete(cat.uvic.teknos.library.models.Loan model) {
        try {
            entityManager.getTransaction().begin();
            cat.uvic.teknos.library.models.Loan loan = entityManager.find(Loan.class, model.getId());
            entityManager.remove(loan);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("ERROR");
        }
    }

    @Override
    public cat.uvic.teknos.library.models.Loan get(Integer id) {
        return entityManager.find(Loan.class, id);
    }

    @Override
    public Set<cat.uvic.teknos.library.models.Loan> getAll() {
        List<cat.uvic.teknos.library.models.Loan> loanList = entityManager.createQuery("SELECT l FROM Loan l", cat.uvic.teknos.library.models.Loan.class).getResultList();
        return new HashSet<>(loanList);
    }
}