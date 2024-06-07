package org.ilia.timeservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.timeservice.repository.WorkingTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkingTimeService {

    private final WorkingTimeRepository workingTimeRepository;
}
