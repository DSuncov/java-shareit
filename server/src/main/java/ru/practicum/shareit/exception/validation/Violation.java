package ru.practicum.shareit.exception.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Violation {
    private final String unvalidatedFieldName;
    private final String message;
}
