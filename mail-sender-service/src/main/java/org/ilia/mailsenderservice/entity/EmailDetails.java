package org.ilia.mailsenderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmailDetails {

    private String receiverEmail;
    private String subject;
    private String content;
}
