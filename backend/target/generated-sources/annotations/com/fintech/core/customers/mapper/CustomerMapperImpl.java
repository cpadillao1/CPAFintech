package com.fintech.core.customers.mapper;

import com.fintech.core.customers.domain.CustomerAddressEntity;
import com.fintech.core.customers.domain.CustomerEmailEntity;
import com.fintech.core.customers.domain.CustomerEntity;
import com.fintech.core.customers.domain.CustomerPhoneEntity;
import com.fintech.core.customers.dto.CustomerAddressDTO;
import com.fintech.core.customers.dto.CustomerDTO;
import com.fintech.core.customers.dto.CustomerEmailDTO;
import com.fintech.core.customers.dto.CustomerPhoneDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl extends CustomerMapper {

    @Override
    public CustomerEntity toEntity(CustomerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerEntity.CustomerEntityBuilder customerEntity = CustomerEntity.builder();

        customerEntity.id( dto.id() );
        customerEntity.firstName( dto.firstName() );
        customerEntity.lastName( dto.lastName() );
        customerEntity.documentTypeId( dto.documentTypeId() );
        customerEntity.documentNumber( dto.documentNumber() );
        customerEntity.birthDate( dto.birthDate() );
        customerEntity.genderId( dto.genderId() );
        customerEntity.maritalStatusId( dto.maritalStatusId() );
        customerEntity.nationalityId( dto.nationalityId() );
        customerEntity.statusId( dto.statusId() );
        customerEntity.createdBy( dto.createdBy() );

        return customerEntity.build();
    }

    @Override
    public CustomerDTO toDto(CustomerEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        Long documentTypeId = null;
        String documentNumber = null;
        LocalDate birthDate = null;
        Long genderId = null;
        Long statusId = null;
        Long maritalStatusId = null;
        Integer nationalityId = null;
        String createdBy = null;
        List<CustomerAddressDTO> addresses = null;
        List<CustomerPhoneDTO> phones = null;
        List<CustomerEmailDTO> emails = null;

        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        documentTypeId = entity.getDocumentTypeId();
        documentNumber = entity.getDocumentNumber();
        birthDate = entity.getBirthDate();
        genderId = entity.getGenderId();
        statusId = entity.getStatusId();
        maritalStatusId = entity.getMaritalStatusId();
        nationalityId = entity.getNationalityId();
        createdBy = entity.getCreatedBy();
        addresses = customerAddressEntityListToCustomerAddressDTOList( entity.getAddresses() );
        phones = customerPhoneEntityListToCustomerPhoneDTOList( entity.getPhones() );
        emails = customerEmailEntityListToCustomerEmailDTOList( entity.getEmails() );

        CustomerDTO customerDTO = new CustomerDTO( id, firstName, lastName, documentTypeId, documentNumber, birthDate, genderId, statusId, maritalStatusId, nationalityId, createdBy, addresses, phones, emails );

        return customerDTO;
    }

    @Override
    protected CustomerAddressEntity toEntity(CustomerAddressDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerAddressEntity.CustomerAddressEntityBuilder customerAddressEntity = CustomerAddressEntity.builder();

        customerAddressEntity.id( dto.id() );
        customerAddressEntity.addressLine( dto.addressLine() );
        customerAddressEntity.city( dto.city() );
        customerAddressEntity.state( dto.state() );
        customerAddressEntity.postalCode( dto.postalCode() );
        customerAddressEntity.country( dto.country() );
        customerAddressEntity.addressTypeId( dto.addressTypeId() );
        customerAddressEntity.isPrimary( dto.isPrimary() );

        return customerAddressEntity.build();
    }

    @Override
    protected CustomerPhoneEntity toEntity(CustomerPhoneDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerPhoneEntity.CustomerPhoneEntityBuilder customerPhoneEntity = CustomerPhoneEntity.builder();

        customerPhoneEntity.id( dto.id() );
        customerPhoneEntity.phoneNumber( dto.phoneNumber() );
        customerPhoneEntity.phoneTypeId( dto.phoneTypeId() );
        customerPhoneEntity.isPrimary( dto.isPrimary() );

        return customerPhoneEntity.build();
    }

    @Override
    protected CustomerEmailEntity toEntity(CustomerEmailDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerEmailEntity.CustomerEmailEntityBuilder customerEmailEntity = CustomerEmailEntity.builder();

        customerEmailEntity.id( dto.id() );
        customerEmailEntity.email( dto.email() );
        customerEmailEntity.emailTypeId( dto.emailTypeId() );
        customerEmailEntity.isPrimary( dto.isPrimary() );

        return customerEmailEntity.build();
    }

    protected CustomerAddressDTO customerAddressEntityToCustomerAddressDTO(CustomerAddressEntity customerAddressEntity) {
        if ( customerAddressEntity == null ) {
            return null;
        }

        Long id = null;
        String addressLine = null;
        String city = null;
        String state = null;
        String country = null;
        String postalCode = null;
        Long addressTypeId = null;
        Boolean isPrimary = null;

        id = customerAddressEntity.getId();
        addressLine = customerAddressEntity.getAddressLine();
        city = customerAddressEntity.getCity();
        state = customerAddressEntity.getState();
        country = customerAddressEntity.getCountry();
        postalCode = customerAddressEntity.getPostalCode();
        addressTypeId = customerAddressEntity.getAddressTypeId();
        isPrimary = customerAddressEntity.getIsPrimary();

        CustomerAddressDTO customerAddressDTO = new CustomerAddressDTO( id, addressLine, city, state, country, postalCode, addressTypeId, isPrimary );

        return customerAddressDTO;
    }

    protected List<CustomerAddressDTO> customerAddressEntityListToCustomerAddressDTOList(List<CustomerAddressEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CustomerAddressDTO> list1 = new ArrayList<CustomerAddressDTO>( list.size() );
        for ( CustomerAddressEntity customerAddressEntity : list ) {
            list1.add( customerAddressEntityToCustomerAddressDTO( customerAddressEntity ) );
        }

        return list1;
    }

    protected CustomerPhoneDTO customerPhoneEntityToCustomerPhoneDTO(CustomerPhoneEntity customerPhoneEntity) {
        if ( customerPhoneEntity == null ) {
            return null;
        }

        Long id = null;
        String phoneNumber = null;
        Long phoneTypeId = null;
        Boolean isPrimary = null;

        id = customerPhoneEntity.getId();
        phoneNumber = customerPhoneEntity.getPhoneNumber();
        phoneTypeId = customerPhoneEntity.getPhoneTypeId();
        isPrimary = customerPhoneEntity.getIsPrimary();

        CustomerPhoneDTO customerPhoneDTO = new CustomerPhoneDTO( id, phoneNumber, phoneTypeId, isPrimary );

        return customerPhoneDTO;
    }

    protected List<CustomerPhoneDTO> customerPhoneEntityListToCustomerPhoneDTOList(List<CustomerPhoneEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CustomerPhoneDTO> list1 = new ArrayList<CustomerPhoneDTO>( list.size() );
        for ( CustomerPhoneEntity customerPhoneEntity : list ) {
            list1.add( customerPhoneEntityToCustomerPhoneDTO( customerPhoneEntity ) );
        }

        return list1;
    }

    protected CustomerEmailDTO customerEmailEntityToCustomerEmailDTO(CustomerEmailEntity customerEmailEntity) {
        if ( customerEmailEntity == null ) {
            return null;
        }

        Long id = null;
        String email = null;
        Long emailTypeId = null;
        Boolean isPrimary = null;

        id = customerEmailEntity.getId();
        email = customerEmailEntity.getEmail();
        emailTypeId = customerEmailEntity.getEmailTypeId();
        isPrimary = customerEmailEntity.getIsPrimary();

        CustomerEmailDTO customerEmailDTO = new CustomerEmailDTO( id, email, emailTypeId, isPrimary );

        return customerEmailDTO;
    }

    protected List<CustomerEmailDTO> customerEmailEntityListToCustomerEmailDTOList(List<CustomerEmailEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CustomerEmailDTO> list1 = new ArrayList<CustomerEmailDTO>( list.size() );
        for ( CustomerEmailEntity customerEmailEntity : list ) {
            list1.add( customerEmailEntityToCustomerEmailDTO( customerEmailEntity ) );
        }

        return list1;
    }
}
