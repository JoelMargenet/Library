package cat.uvic.teknos.library.repositories;

import cat.uvic.teknos.library.models.Loan;

import java.util.Set;

public interface LoanRepository extends Repository<Integer, Loan> {
    @Override
    void save(Loan model);

    @Override
    void delete(Loan model);

    @Override
    Loan get(Integer id);

    @Override
    Set<Loan> getAll();
}