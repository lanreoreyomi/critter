package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.Model.Customer;
import com.udacity.jdnd.course3.critter.Model.Employees;
import com.udacity.jdnd.course3.critter.Model.Pet;
import com.udacity.jdnd.course3.critter.Repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.Repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.Repository.PetRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 * <p>
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    CustomerRepository customerRepo;
    EmployeeRepository employeeRepo;
    PetRepository petRepo;

    public UserController(CustomerRepository customerRepo,
                          EmployeeRepository employeeRepo,
                          PetRepository petRepo) {
        this.customerRepo = customerRepo;
        this.employeeRepo = employeeRepo;
        this.petRepo = petRepo;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer savedCustomer = customerRepo.addCustomer(this.convertCustomerDTOtoCustomer(customerDTO));
        customerDTO.setId(savedCustomer.getId());
        return customerDTO;
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers() {
        return this.convertListOfCustomerToCustomerDTO(customerRepo.getAllCustomers());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) {
        return convertCustomerToCustomerDTO(customerRepo.getOwnerByPet(petId));
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employees savedEmployee = employeeRepo.addEmployee(this.convertEmployeeDTOtoEmployee(employeeDTO));
        employeeDTO.setId(savedEmployee.getId());
        return employeeDTO;
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        return this.convertEmployeeToEmployeeDTO(employeeRepo.findEmployeeById(employeeId));
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        employeeRepo.setAvailability(daysAvailable, employeeId);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        DayOfWeek daysAvailable = employeeDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> skills = employeeDTO.getSkills();
        List<Employees> employeeList = employeeRepo.getEmployeesForService(skills, daysAvailable);

        List<EmployeeDTO> employeeDtotoList = new ArrayList<>();
        if (!employeeList.isEmpty()) {

            employeeList.forEach(employee -> {
                EmployeeDTO empDto = convertEmployeeToEmployeeDTO(employee);
                employeeDtotoList.add(empDto);
            });

        }
        return employeeDtotoList;
    }

    private Customer convertCustomerDTOtoCustomer(CustomerDTO customerDto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDto, customer);
        return customer;
    }

    private List<CustomerDTO> convertListOfCustomerToCustomerDTO(List<Customer> customer) {
        List<CustomerDTO> customerDtoList = new ArrayList<>();
        customer.forEach(e -> {
            CustomerDTO customerDto = new CustomerDTO();
            BeanUtils.copyProperties(e, customerDto);

            List<Pet> pets = petRepo.getOwnerByPet(e.getId());
            List<Long> petId;
            if (!pets.isEmpty()) {
                petId = new ArrayList<>();
                pets.forEach(p -> {
                    petId.add(p.getId());
                    customerDto.setPetIds(petId);
                });

            }
            customerDtoList.add(customerDto);
        });

        return customerDtoList;
    }

    private CustomerDTO convertCustomerToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        if (customer.getPets() != null) {
            customerDTO.setPetIds(customer.getPets().stream().map(Pet::getId).collect(Collectors.toList()));
        }
        return customerDTO;
    }

    private Employees convertEmployeeDTOtoEmployee(EmployeeDTO employeeDTO) {
        Employees employee = new Employees();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    private List<EmployeeDTO> employeeToDTO(List<Employees> employee) {
        List<EmployeeDTO> employeeDToList = new ArrayList<>();
        employee.forEach(e -> {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            BeanUtils.copyProperties(e, employeeDTO);
            employeeDToList.add(employeeDTO);
        });

        return employeeDToList;
    }


    private EmployeeDTO convertEmployeeToEmployeeDTO(Employees employee) {
        EmployeeDTO emp = new EmployeeDTO();
        BeanUtils.copyProperties(employee, emp);
        return emp;
    }


    private List<EmployeeDTO> convertListOfEmployeeToEmployeeDTO(List<Employees> employee) {
        List<EmployeeDTO> employeeDtoList = new ArrayList<>();
        employee.forEach(e -> {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            BeanUtils.copyProperties(e, employeeDTO);
            employeeDtoList.add(employeeDTO);
        });
        return employeeDtoList;
    }

}
