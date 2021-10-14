package io.github.jklingsporn.vertx.jooq.classic.reactivepg;

import io.github.jklingsporn.vertx.jooq.classic.ClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.BatchQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryResult;
import io.github.jklingsporn.vertx.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.reactive.ReactiveQueryResult;
import io.vertx.core.Future;
import io.vertx.sqlclient.Cursor;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.*;
import org.jooq.Query;
import org.jooq.*;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by jensklingsporn on 01.03.18.
 */
public class ReactiveClassicGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Future<List<Row>>,Future<Row>,Future<Integer>>,ClassicQueryExecutor,BatchQueryExecutor<Future<RowSet<Row>>> {

    protected final SqlClient delegate;
    protected final Transaction transaction;

    public ReactiveClassicGenericQueryExecutor(Configuration configuration, SqlClient delegate) {
        this(configuration, delegate, null);
    }

    ReactiveClassicGenericQueryExecutor(Configuration configuration, SqlClient delegate, Transaction transaction) {
        super(configuration);
        this.delegate = delegate;
        this.transaction = transaction;
    }


    @Override
    public <Q extends Record> Future<List<Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return executeAny(queryFunction).map(res -> StreamSupport
                .stream(res.spliterator(), false)
                .collect(Collectors.toList()));
    }

    @Override
    public <Q extends Record> Future<Row> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return executeAny(queryFunction).map(res -> {
            switch (res.size()) {
                case 0:
                    return null;
                case 1:
                    return res.iterator().next();
                default:
                    throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Future<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeAny(queryFunction).map(SqlResult::rowCount);
    }


    @Override
    public <R extends Record> Future<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return executeAny(queryFunction).map(ReactiveQueryResult::new);
    }

    /**
     * @return an instance of a <code>ReactiveClassicGenericQueryExecutor</code> that performs all CRUD
     * functions in the scope of a transaction. The transaction has to be committed/rolled back by calling <code>commit</code>
     * or <code>rollback</code> on the QueryExecutor returned.
     */
    public Future<? extends ReactiveClassicGenericQueryExecutor> beginTransaction(){
        if(transaction!=null){
            return Future.failedFuture(new IllegalStateException("Already in transaction"));
        }
        if (!(delegate instanceof Pool)) {
           return Future.failedFuture(new IllegalStateException("pool not given"));
        }
        Pool pool = (Pool)delegate;
        return pool.getConnection()
            .compose(conn-> conn.begin().map(newInstance(conn)));
    }

    protected Function<Transaction, ? extends ReactiveClassicGenericQueryExecutor> newInstance(SqlClient connection) {
        return transaction -> new ReactiveClassicGenericQueryExecutor(configuration(), connection, transaction);
    }

    /**
     * Commits a transaction.
     * @return a <code>Future</code> that completes when the transaction has been committed.
     * @throws IllegalStateException if not called <code>beginTransaction</code> before.
     */
    public Future<Void> commit(){
        if(transaction==null){
            return Future.failedFuture(new IllegalStateException("Not in transaction"));
        }
        return transaction.commit().compose(v->delegate.close());
    }

    /**
     * Rolls a transaction back.
     * @return a <code>Future</code> that completes when the transaction has been rolled back.
     * @throws IllegalStateException if not called <code>beginTransaction</code> before.
     */
    public Future<Void> rollback(){
	    if(transaction==null){
		    return Future.failedFuture(new IllegalStateException("Not in transaction"));
	    }
	    return transaction.rollback();
    }

    /**
     * Convenience method to perform multiple calls on a transactional QueryExecutor, committing the transaction and
     * returning a result.
     * @param transaction your code using a transactional QueryExecutor.
     *                    <pre>
     *                    {@code
     *                    ReactiveClassicGenericQueryExecutor nonTransactionalQueryExecutor...;
     *                    Future<QueryResult> resultOfTransaction = nonTransactionalQueryExecutor.transaction(transactionalQueryExecutor ->
     *                      {
     *                          //make all calls on the provided QueryExecutor that runs all code in a transaction
     *                          return transactionalQueryExecutor.execute(dslContext -> dslContext.insertInto(Tables.XYZ)...)
     *                              .compose(i -> transactionalQueryExecutor.query(dslContext -> dslContext.selectFrom(Tables.XYZ).where(Tables.XYZ.SOME_VALUE.eq("FOO")));
     *                      }
     *                    );
     *                    }
     *                    </pre>
     * @param <U> the return type.
     * @return the result of the transaction.
     */
    public <U> Future<U> transaction(Function<ReactiveClassicGenericQueryExecutor, Future<U>> transaction){
        return beginTransaction()
                .compose(queryExecutor -> transaction.apply(queryExecutor) //perform user tasks
                        .compose(res -> queryExecutor.commit() //commit the transaction
                                .map(v -> res))); //and return the result
    }

    /**
     * Executes the given queryFunction and returns a <code>RowSet</code>
     * @param queryFunction the query to execute
     * @return the results, never null
     */
    public Future<RowSet<Row>> executeAny(Function<DSLContext, ? extends Query> queryFunction) {
        Query query = createQuery(queryFunction);
        log(query);
        return delegate.preparedQuery(toPreparedQuery(query)).execute(getBindValues(query));
    }

    /**
     * A convenient function to process a large result set using a {@link io.vertx.sqlclient.Cursor}.
     * @param queryFunction The function that fetches the result set.
     * @param cursorFunction The function that processes the result set.
     * @return a <code>Future</code> that is completed when the <code>Cursor</code> has been processed.
     */
    public <T> Future<Void> withCursor(Function<DSLContext, ? extends Query> queryFunction, Function<Cursor,Future<T>> cursorFunction){
        Query query = createQuery(queryFunction);
        return ((Pool)delegate).withTransaction(
                sqlConnection -> sqlConnection
                    .prepare(toPreparedQuery(query))
                    .map(PreparedStatement::cursor)
                    .compose(cursor -> cursorFunction
                            .apply(cursor)
                            .transform(x -> cursor.close())
                    )
                );
    }

    /**
     * A convenient function to process a large result set using a {@link io.vertx.sqlclient.RowStream}.
     * @param queryFunction The function that fetches the result set.
     * @param streamFunction The function that processes the result set.
     * @param fetchSize the amount to fetch
     * @return a <code>Future</code> that is completed when the <code>RowStream</code> has been processed.
     */
    public <T> Future<Void> withRowStream(Function<DSLContext, ? extends Query> queryFunction, Function<RowStream<Row>,Future<T>> streamFunction, int fetchSize){
        Query query = createQuery(queryFunction);
        return ((Pool)delegate).withTransaction(
                sqlConnection -> sqlConnection
                        .prepare(toPreparedQuery(query))
                        .map(ps -> ps.createStream(fetchSize))
                        .compose(rowStream -> streamFunction
                                .apply(rowStream)
                                .transform(x -> rowStream.close())
                        )
        );
    }

    /**
     * Executes the given queriesFunction as batch and returns a <code>RowSet</code>
     * @param queriesFunction the queries to execute as batch
     * @return the results, never null
     */
    @Override
    public Future<RowSet<Row>> executeBatch(Function<DSLContext, ? extends List<Query>> queriesFunction) {
        List<Query> queries = queriesFunction.apply(DSL.using(this.configuration()));
        if (queries.isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("Query list must not be empty"));
        }
        Query query = queries.get(0);
        List<Tuple> tuples = queries.stream()
                .map(this::getBindValues)
                .collect(Collectors.toList());
        return delegate.preparedQuery(this.toPreparedQuery(query)).executeBatch(tuples);
    }
}
