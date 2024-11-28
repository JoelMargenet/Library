package cat.uvic.teknos.library.repositories;

import cat.uvic.teknos.library.models.Customer;

import java.util.Set;

public interface CustomerRepository extends Repository<Integer, Customer> {
    @Override
    void save(Customer model);

    @Override
    void delete(Customer model);

    @Override
    Customer get(Integer id);

    @Override
    Set<Customer> getAll();
}