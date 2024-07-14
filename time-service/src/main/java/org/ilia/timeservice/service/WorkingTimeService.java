package org.ilia.timeservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.controller.request.CreateWorkingTimeDto;
import org.ilia.timeservice.controller.response.WorkingTimeDto;
import org.ilia.timeservice.enums.Role;
import org.ilia.timeservice.exception.UserNotFoundException;
import org.ilia.timeservice.exception.WorkingTimeAlreadyExistException;
import org.ilia.timeservice.exception.WorkingTimeNotFoundException;
import org.ilia.timeservice.feign.UserServiceClient;
import org.ilia.timeservice.feign.response.UserDto;
import org.ilia.timeservice.mapper.WorkingTimeMapper;
import org.ilia.timeservice.repository.WorkingTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.timeservice.constant.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
public class WorkingTimeService {

    WorkingTimeRepository workingTimeRepository;
    WorkingTimeMapper workingTimeMapper;
    UserServiceClient userServiceClient;

    public List<WorkingTimeDto> findByDoctorId(Role role, UUID doctorId) {
        verifyUserExistByRoleAndId(role, doctorId);

        return workingTimeRepository.findByDoctorId(doctorId).stream()
                .map(workingTimeMapper::toWorkingTimeDto)
                .toList();
    }

    public List<WorkingTimeDto> create(Role role, UUID doctorId, List<CreateWorkingTimeDto> createWorkingTimeDtoList) {
        verifyUserExistByRoleAndId(role, doctorId);
        verifyWorkingTimeNotExistById(doctorId);

        return createWorkingTimeDtoList.stream()
                .map(workingTimeMapper::toWorkingTime)
                .peek(wt -> wt.setDoctorId(doctorId))
                .map(workingTimeRepository::save)
                .map(workingTimeMapper::toWorkingTimeDto)
                .toList();
    }

    public void deleteByDoctorId(Role role, UUID doctorId) {
        verifyUserExistByRoleAndId(role, doctorId);
        verifyWorkingTimeExistById(doctorId);

        workingTimeRepository.deleteByDoctorId(doctorId);
    }

    private void verifyUserExistByRoleAndId(Role role, UUID id) {
        UserDto user = userServiceClient.findById(role, id);
        if (user.getRole() != role) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_ID_AND_ROLE.formatted(id, role));
        }
    }

    private void verifyWorkingTimeNotExistById(UUID doctorId) {
        if (!workingTimeRepository.findByDoctorId(doctorId).isEmpty()) {
            throw new WorkingTimeAlreadyExistException(WORKING_TIME_ALREADY_EXIST + doctorId);
        }
    }

    private void verifyWorkingTimeExistById(UUID doctorId) {
        if (workingTimeRepository.findByDoctorId(doctorId).isEmpty()) {
            throw new WorkingTimeNotFoundException(WORKING_TIME_NOT_FOUND + doctorId);
        }
    }
}
