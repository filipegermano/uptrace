package br.com.uppersystems.uptrace.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class that represents a custom Stack Trace.
 * Implements the {@link StackTrace} interface.
 *
 * @author Filipe Germano
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StackTrace {

    public String clazz;

    public String message;

    public String stack;
}
