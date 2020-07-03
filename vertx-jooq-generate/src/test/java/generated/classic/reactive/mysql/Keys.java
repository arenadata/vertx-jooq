/*
 * This file is generated by jOOQ.
 */
package generated.classic.reactive.mysql;


import generated.classic.reactive.mysql.tables.Something;
import generated.classic.reactive.mysql.tables.Somethingcomposite;
import generated.classic.reactive.mysql.tables.Somethingwithoutjson;
import generated.classic.reactive.mysql.tables.records.SomethingRecord;
import generated.classic.reactive.mysql.tables.records.SomethingcompositeRecord;
import generated.classic.reactive.mysql.tables.records.SomethingwithoutjsonRecord;

import org.jooq.Identity;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>vertx</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<SomethingRecord, Integer> IDENTITY_SOMETHING = Identities0.IDENTITY_SOMETHING;
    public static final Identity<SomethingwithoutjsonRecord, Integer> IDENTITY_SOMETHINGWITHOUTJSON = Identities0.IDENTITY_SOMETHINGWITHOUTJSON;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<SomethingRecord> KEY_SOMETHING_PRIMARY = UniqueKeys0.KEY_SOMETHING_PRIMARY;
    public static final UniqueKey<SomethingcompositeRecord> KEY_SOMETHINGCOMPOSITE_PRIMARY = UniqueKeys0.KEY_SOMETHINGCOMPOSITE_PRIMARY;
    public static final UniqueKey<SomethingwithoutjsonRecord> KEY_SOMETHINGWITHOUTJSON_PRIMARY = UniqueKeys0.KEY_SOMETHINGWITHOUTJSON_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<SomethingRecord, Integer> IDENTITY_SOMETHING = Internal.createIdentity(Something.SOMETHING, Something.SOMETHING.SOMEID);
        public static Identity<SomethingwithoutjsonRecord, Integer> IDENTITY_SOMETHINGWITHOUTJSON = Internal.createIdentity(Somethingwithoutjson.SOMETHINGWITHOUTJSON, Somethingwithoutjson.SOMETHINGWITHOUTJSON.SOMEID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<SomethingRecord> KEY_SOMETHING_PRIMARY = Internal.createUniqueKey(Something.SOMETHING, "KEY_something_PRIMARY", new TableField[] { Something.SOMETHING.SOMEID }, true);
        public static final UniqueKey<SomethingcompositeRecord> KEY_SOMETHINGCOMPOSITE_PRIMARY = Internal.createUniqueKey(Somethingcomposite.SOMETHINGCOMPOSITE, "KEY_somethingComposite_PRIMARY", new TableField[] { Somethingcomposite.SOMETHINGCOMPOSITE.SOMEID, Somethingcomposite.SOMETHINGCOMPOSITE.SOMESECONDID }, true);
        public static final UniqueKey<SomethingwithoutjsonRecord> KEY_SOMETHINGWITHOUTJSON_PRIMARY = Internal.createUniqueKey(Somethingwithoutjson.SOMETHINGWITHOUTJSON, "KEY_somethingWithoutJson_PRIMARY", new TableField[] { Somethingwithoutjson.SOMETHINGWITHOUTJSON.SOMEID }, true);
    }
}
