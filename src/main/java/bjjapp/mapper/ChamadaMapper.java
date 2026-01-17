package bjjapp.mapper;

import bjjapp.dto.response.ChamadaResponse;
import bjjapp.entity.Chamada;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = { TurmaMapper.class, ProfessorMapper.class,
        UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChamadaMapper {

    @Mapping(target = "totalPresentes", expression = "java(chamada.getTotalPresentes())")
    ChamadaResponse toResponse(Chamada chamada);

    List<ChamadaResponse> toResponseList(List<Chamada> chamadas);
}
