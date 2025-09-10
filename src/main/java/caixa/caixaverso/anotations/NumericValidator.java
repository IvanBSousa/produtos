package caixa.caixaverso.anotations;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumericValidator implements ConstraintValidator<IsNumeric, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value instanceof BigDecimal) {
            return true;
        }
        return false; // verifica se contém apenas dígitos
    }
}

