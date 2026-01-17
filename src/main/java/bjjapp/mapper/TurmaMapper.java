package bjjapp.mapper;

import bjjapp.dto.request.TurmaRequest;
import bjjapp.dto.response.TurmaResponse;
import bjjapp.entity.Turma;
import bjjapp.enums.Modalidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TurmaMapper {

    TurmaResponse toResponse(Turma turma);

    List<TurmaResponse> toResponseList(List<Turma> turmas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    @Mapping(target = "modalidade", source = "modalidade", qualifiedByName = "stringToModalidade")
    @Mapping(target = "alunos", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Turma toEntity(TurmaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "modalidade", source = "modalidade", qualifiedByName = "stringToModalidade")
    @Mapping(target = "alunos", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromRequest(TurmaRequest request, @MappingTarget Turma turma);

    @Named("stringToModalidade")
    default Modalidade stringToModalidade(String modalidade) {
        if (modalidade == null)
            return null;
        return Modalidade.fromDescricao(modalidade);
    }
}
