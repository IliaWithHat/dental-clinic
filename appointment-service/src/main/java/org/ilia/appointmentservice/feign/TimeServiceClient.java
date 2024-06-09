package org.ilia.appointmentservice.feign;

import org.ilia.appointmentservice.entity.WorkingTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient("time-service")
public interface TimeServiceClient {

    @GetMapping("/v1/working-time/{doctorId}")
    List<WorkingTime> findByDoctorId(@PathVariable UUID doctorId);
}
