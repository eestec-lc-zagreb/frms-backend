package hr.eestec_zg.frmsbackend.controllers;

import hr.eestec_zg.frmsbackend.domain.models.Company;
import hr.eestec_zg.frmsbackend.exceptions.CompanyNotFoundException;
import hr.eestec_zg.frmsbackend.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "/companies/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Company getCompany(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }
        Company company = companyService.getCompanyById(id);
        if (company==null)
            throw new CompanyNotFoundException();
        return company;
    }

    @RequestMapping(value = "/companies", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Company> getCompanies() {
        return companyService.getCompanies();
    }

    @RequestMapping(value = "/companies/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteCompany(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }
        companyService.deleteCompany(id);
    }

    @RequestMapping(value = "/companies", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Company postCompany(@RequestBody Company company) {
        companyService.createCompany(company);
        return company;
    }

    @RequestMapping(value = "/companies/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void putCompany(@PathVariable("id") Long id, @RequestBody Company company) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null value");
        }
        companyService.updateCompany(company);
    }

}
