package net.mint.java.mapper;

import net.mint.java.domain.AccountEntity;
import net.mint.java.model.AccountDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "transactions", ignore = true)
    AccountDTO mapFrom(AccountEntity entity);

    @Mapping(target = "transactions", ignore = true)
    @InheritInverseConfiguration
    AccountEntity mapFrom(AccountDTO dto);
}
