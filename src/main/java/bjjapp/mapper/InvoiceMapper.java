package bjjapp.mapper;

import bjjapp.dto.response.InvoiceResponse;
import bjjapp.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "schoolId", source = "school.id")
    @Mapping(target = "schoolName", source = "school.name")
    @Mapping(target = "referenceMonth", expression = "java(invoice.getReferenceMonth() != null ? invoice.getReferenceMonth().toString() : null)")
    InvoiceResponse toResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> invoices);
}
