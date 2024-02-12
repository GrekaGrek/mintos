package net.mint.java.mapper;

import net.mint.java.domain.TransferEntity;
import net.mint.java.model.TransferDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "sourceAccount", ignore = true)
    @Mapping(target = "targetAccount", ignore = true)
    TransferDTO mapFrom(TransferEntity entity);

    @Mapping(target = "sourceAccount", ignore = true)
    @Mapping(target = "targetAccount", ignore = true)
    @InheritInverseConfiguration
    TransferEntity mapFrom(TransferDTO dto);
}
