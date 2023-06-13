package com.appointmentManagementSystem.payload;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddDictionaryWordPayload {

    public Long id;
    public String word;
    public String meaning;
}
