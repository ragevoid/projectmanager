package com.desafio.projectmanager.handler;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDetails {
 private LocalDateTime time;
 private String message;
 private String details;
}
