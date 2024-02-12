package net.mint.java.mapper;

import net.mint.java.domain.CustomerEntity;
import net.mint.java.model.CustomerDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO mapFrom(CustomerEntity entity);

    @InheritInverseConfiguration
    CustomerEntity mapFrom(CustomerDTO dto);
}
