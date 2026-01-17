package bjjapp.mapper;

import bjjapp.dto.request.ProfessorRequest;
import bjjapp.dto.response.ProfessorResponse;
import bjjapp.entity.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorMapper {

    ProfessorResponse toResponse(Professor professor);

    List<ProfessorResponse> toResponseList(List<Professor> professors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "school", ignore = true)
    Professor toEntity(ProfessorRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "school", ignore = true)
    void updateEntityFromRequest(ProfessorRequest request, @MappingTarget Professor professor);
}
