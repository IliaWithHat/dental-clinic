package org.ilia.timeservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.controller.response.WorkingTimeDto;
import org.ilia.timeservice.enums.Role;
import org.ilia.timeservice.mapper.WorkingTimeMapper;
import org.ilia.timeservice.repository.WorkingTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class WorkingTimeService {

    WorkingTimeRepository workingTimeRepository;
    WorkingTimeMapper workingTimeMapper;

    public List<WorkingTimeDto> findByDoctorId(Role role, UUID doctorId) {
        return workingTimeRepository.findByDoctorId(doctorId).stream()
                .map(workingTimeMapper::toWorkingTimeDto)
                .toList();
    }

    public List<WorkingTimeDto> create(Role role, UUID doctorId, List<CreateWorkingTimeDto> createWorkingTimeDtos) {
        return createWorkingTimeDtos.stream()
                .map(workingTimeMapper::toWorkingTime)
                .peek(wt -> wt.setDoctorId(doctorId))
                .map(workingTimeRepository::save)
                .map(workingTimeMapper::toWorkingTimeDto)
                .toList();
    }

    public void deleteByDoctorId(Role role, UUID doctorId) {
        workingTimeRepository.deleteByDoctorId(doctorId);
    }
}
