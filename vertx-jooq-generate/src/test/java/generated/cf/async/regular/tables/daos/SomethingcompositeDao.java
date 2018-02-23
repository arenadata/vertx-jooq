/*
 * This file is generated by jOOQ.
*/
package generated.cf.async.regular.tables.daos;


import generated.cf.async.regular.tables.Somethingcomposite;
import generated.cf.async.regular.tables.records.SomethingcompositeRecord;

import io.github.jklingsporn.vertx.jooq.shared.internal.async.AbstractAsyncVertxDAO;
import io.vertx.core.json.JsonObject;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;


import java.util.concurrent.CompletableFuture;
import io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO;
import io.github.jklingsporn.vertx.jooq.completablefuture.async.AsyncCompletableFutureQueryExecutor;
/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SomethingcompositeDao extends AbstractAsyncVertxDAO<SomethingcompositeRecord, generated.cf.async.regular.tables.pojos.Somethingcomposite, Record2<Integer, Integer>, CompletableFuture<List<generated.cf.async.regular.tables.pojos.Somethingcomposite>>, CompletableFuture<generated.cf.async.regular.tables.pojos.Somethingcomposite>, CompletableFuture<Integer>, CompletableFuture<Record2<Integer, Integer>>> implements io.github.jklingsporn.vertx.jooq.completablefuture.VertxDAO<SomethingcompositeRecord,generated.cf.async.regular.tables.pojos.Somethingcomposite,Record2<Integer, Integer>> {

    /**
     * @param configuration Used for rendering, so only SQLDialect must be set and must be one of the MYSQL types or POSTGRES.
     * @param vertx the vertx instance
     * @param delegate A configured AsyncSQLClient that is used for query execution
     */
    public SomethingcompositeDao(Configuration configuration, io.vertx.core.Vertx vertx, io.vertx.ext.asyncsql.AsyncSQLClient delegate) {
        super(Somethingcomposite.SOMETHINGCOMPOSITE, generated.cf.async.regular.tables.pojos.Somethingcomposite.class, new AsyncCompletableFutureQueryExecutor<SomethingcompositeRecord,generated.cf.async.regular.tables.pojos.Somethingcomposite,Record2<Integer, Integer>>(vertx,delegate,generated.cf.async.regular.tables.pojos.Somethingcomposite::new, Somethingcomposite.SOMETHINGCOMPOSITE), configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Record2<Integer, Integer> getId(generated.cf.async.regular.tables.pojos.Somethingcomposite object) {
        return compositeKeyRecord(object.getSomeid(), object.getSomesecondid());
    }

    /**
     * Find records that have <code>someSecondId IN (values)</code> asynchronously
     */
    public CompletableFuture<List<generated.cf.async.regular.tables.pojos.Somethingcomposite>> findManyBySomesecondid(List<Integer> values) {
        return findManyByCondition(Somethingcomposite.SOMETHINGCOMPOSITE.SOMESECONDID.in(values));
    }

    /**
     * Find records that have <code>someJsonObject IN (values)</code> asynchronously
     */
    public CompletableFuture<List<generated.cf.async.regular.tables.pojos.Somethingcomposite>> findManyBySomejsonobject(List<JsonObject> values) {
        return findManyByCondition(Somethingcomposite.SOMETHINGCOMPOSITE.SOMEJSONOBJECT.in(values));
    }

    @Override
    public CompletableFuture<Record2<Integer, Integer>> insertReturningPrimary(generated.cf.async.regular.tables.pojos.Somethingcomposite pojo){
        CompletableFuture<Record2<Integer, Integer>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new UnsupportedOperationException("PK not numeric"));
        return failed;
    }
}
