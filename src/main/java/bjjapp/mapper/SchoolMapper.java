package bjjapp.mapper;

import bjjapp.dto.request.SchoolCreationRequest;
import bjjapp.dto.response.SchoolOwnerResponse;
import bjjapp.dto.response.SchoolResponse;
import bjjapp.dto.response.SubscriptionResponse;
import bjjapp.entity.School;
import bjjapp.entity.SchoolOwner;
import bjjapp.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolMapper {

    SchoolResponse toResponse(School school);

    List<SchoolResponse> toResponseList(List<School> schools);

    SchoolOwnerResponse toOwnerResponse(SchoolOwner owner);

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "name", source = "schoolName")
    @Mapping(target = "slug", source = "schoolSlug")
    @Mapping(target = "phone", source = "schoolPhone")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    @Mapping(target = "trialEndDate", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    School toEntity(SchoolCreationRequest request);
}
