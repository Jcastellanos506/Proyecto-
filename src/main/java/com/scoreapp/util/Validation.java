package com.scoreapp.util;

import com.scoreapp.exceptions.ValidationException;

public final class Validation {
    private Validation(){}

    public static void requirePositive(double value, String field){
        if(value <= 0) throw new ValidationException(field + " debe ser positivo");
    }
    public static void requireNonNegative(double value, String field){
        if(value < 0) throw new ValidationException(field + " no puede ser negativo");
    }
    public static void requireNonBlank(String value, String field){
        if(value == null || value.trim().isEmpty())
            throw new ValidationException(field + " no puede estar vacío");
    }
    public static void requireEmail(String email){
        // En cadenas Java, la regex usa doble backslash \\ para \w, etc.
        if(email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))
            throw new ValidationException("email inválido");
    }
}
