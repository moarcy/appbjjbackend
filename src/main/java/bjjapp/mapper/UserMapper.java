package bjjapp.mapper;

import bjjapp.dto.request.UserRequest;
import bjjapp.dto.response.UserResponse;
import bjjapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "turmas", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "plainPassword", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
}
