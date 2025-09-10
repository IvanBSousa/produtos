package caixa.caixaverso.anotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * An advisory annotation indicating that the given variable, method, or parameter is not numeric.
 */
@Documented
@Constraint(validatedBy = NumericValidator.class) // Indica qual classe fará a validação
@Target({ ElementType.FIELD, ElementType.PARAMETER }) // Pode ser usada em atributos e parâmetros
@Retention(RetentionPolicy.RUNTIME)
public @interface IsNumeric {

    String message() default "O valor deve ser numérico";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}