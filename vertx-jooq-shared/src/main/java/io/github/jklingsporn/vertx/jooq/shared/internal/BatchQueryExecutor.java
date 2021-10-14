package io.github.jklingsporn.vertx.jooq.shared.internal;

import org.jooq.DSLContext;
import org.jooq.Query;

import java.util.List;
import java.util.function.Function;

/**
 * A {@code QueryExecutor} to execute batch queries operation.
 * @param <EXECUTE> the result type returned for batch operation. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 */
public interface BatchQueryExecutor<EXECUTE> {

    /**
     * Executes batch queries and returns the result of the execution
     * @param queriesFunction the batch queries function
     * @return the result type returned for batch-operation.
     */
    EXECUTE executeBatch(Function<DSLContext, ? extends List<Query>> queriesFunction);
}
