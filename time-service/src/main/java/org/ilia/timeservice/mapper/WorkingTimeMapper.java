package org.ilia.timeservice.mapper;

import org.ilia.timeservice.controller.request.CreateWorkingTimeRequest;
import org.ilia.timeservice.entity.WorkingTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkingTimeMapper {

    @Mapping(target = "id", ignore = true)
    WorkingTime toWorkingTime(CreateWorkingTimeRequest createWorkingTimeRequest);
}
