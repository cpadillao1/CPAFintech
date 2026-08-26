package com.fintech.management.users.mapper;

import com.fintech.management.branches.domain.BranchEntity;
import com.fintech.management.branches.mapper.BranchMapper;
import com.fintech.management.users.domain.UserEntity;
import com.fintech.management.users.dto.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:58-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private BranchMapper branchMapper;

    @Override
    public UserDTO toDto(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setBranchId( userEntityBranchId( userEntity ) );
        userDTO.setId( userEntity.getId() );
        userDTO.setFirstName( userEntity.getFirstName() );
        userDTO.setLastName( userEntity.getLastName() );
        userDTO.setLogin( userEntity.getLogin() );
        userDTO.setEmail( userEntity.getEmail() );
        userDTO.setActive( userEntity.getActive() );
        userDTO.setBranch( branchMapper.toDto( userEntity.getBranch() ) );
        userDTO.setCreatedAt( userEntity.getCreatedAt() );

        return userDTO;
    }

    @Override
    public UserEntity toEntity(UserDTO userDto) {
        if ( userDto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setBranch( userDTOToBranchEntity( userDto ) );
        userEntity.setId( userDto.getId() );
        userEntity.setFirstName( userDto.getFirstName() );
        userEntity.setLastName( userDto.getLastName() );
        userEntity.setLogin( userDto.getLogin() );
        userEntity.setEmail( userDto.getEmail() );
        userEntity.setActive( userDto.getActive() );
        userEntity.setCreatedAt( userDto.getCreatedAt() );

        return userEntity;
    }

    private Integer userEntityBranchId(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }
        BranchEntity branch = userEntity.getBranch();
        if ( branch == null ) {
            return null;
        }
        Integer id = branch.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected BranchEntity userDTOToBranchEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        BranchEntity branchEntity = new BranchEntity();

        branchEntity.setId( userDTO.getBranchId() );

        return branchEntity;
    }
}
