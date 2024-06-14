package org.ilia.mailgeneratorservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDetails {

    private String receiverEmail;
    private String subject;
    private String content;
}
