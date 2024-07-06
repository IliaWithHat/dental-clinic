package org.ilia.timeservice.mapper;

import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.controller.response.WorkingTimeDto;
import org.ilia.timeservice.entity.WorkingTime;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface WorkingTimeMapper {

    WorkingTime toWorkingTime(CreateWorkingTimeDto createWorkingTimeDto);

    WorkingTimeDto toWorkingTimeDto(WorkingTime workingTime);
}
