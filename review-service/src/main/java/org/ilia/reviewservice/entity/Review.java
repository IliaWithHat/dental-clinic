package org.ilia.reviewservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    Short mark;

    String title;

    String review;

    @CreationTimestamp
    LocalDateTime date;

    UUID patientId;

    UUID doctorId;
}
