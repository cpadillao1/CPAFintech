package com.fintech.core.customers.mapper;

import com.fintech.core.customers.domain.CustomerEntity;
import com.fintech.core.customers.domain.CustomerAddressEntity;
import com.fintech.core.customers.domain.CustomerPhoneEntity;
import com.fintech.core.customers.domain.CustomerEmailEntity;
import com.fintech.core.customers.dto.CustomerDTO;
import com.fintech.core.customers.dto.CustomerAddressDTO;
import com.fintech.core.customers.dto.CustomerPhoneDTO;
import com.fintech.core.customers.dto.CustomerEmailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

    // 1. Mapeo base del Cliente (Ignoramos las listas para mapearlas manualmente)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract CustomerEntity toEntity(CustomerDTO dto);

    // 2. Mapeo de vuelta a DTO (Aquí MapStruct lo hace automático)
    public abstract CustomerDTO toDto(CustomerEntity entity);

    // 3. Mapeos individuales de las entidades hijas
    protected abstract CustomerAddressEntity toEntity(CustomerAddressDTO dto);
    protected abstract CustomerPhoneEntity toEntity(CustomerPhoneDTO dto);
    protected abstract CustomerEmailEntity toEntity(CustomerEmailDTO dto);

    /**
     * ESTA ES LA CLAVE:
     * Al usar una clase abstracta, podemos definir el flujo exacto.
     * Después de que MapStruct genera el 'toEntity', Spring llamará a este metodo
     * gracias al ciclo de vida de MapStruct si lo usamos en el Service.
     */
    public CustomerEntity toFullEntity(CustomerDTO dto) {
        CustomerEntity entity = toEntity(dto);

        if (dto.addresses() != null) {
            dto.addresses().forEach(d -> entity.addAddress(toEntity(d)));
        }
        if (dto.phones() != null) {
            dto.phones().forEach(d -> entity.addPhone(toEntity(d)));
        }
        if (dto.emails() != null) {
            dto.emails().forEach(d -> entity.addEmail(toEntity(d)));
        }

        return entity;
    }
}
