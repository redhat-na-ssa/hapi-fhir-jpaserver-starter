package ca.uhn.fhir.jpa.starter;

import java.sql.Types;

import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/*  Background:
 *    The HAPI FHIR server stores FHIR resources in a relational database as a GZipped byte array. (1)
 *    There is interest in event streaming this FHIR data for consumption by other modern health-care related services.
 *    This event streaming is done using Debezium.
 *    Current focus is on the use of PostgreSQL 12 as the relational database backend for the HAPI FHIR server.
 * 
 *    By default, Hibernate JPA  (the Object-Relational-Mapping technology used in HAPI Fhir) implements a @Lob byte array as a PostgreSQL "oid" type. (2)
 *    (The alternative being a PostgreSQL "bytea" type).
 * 
 * Purpose:
 *   Debezium Postgresql connector does not implement a Type mapping for PostgreSQL "oid".
 *   Debezium Postgresql connector does however implement a Type mapping for PostgreSQL "bytea". (3)
 *   Subsequently, the purpose of this class is to over-ride the Hibernate PostgresqlDialect default use of "oid" in favor of "bytea" for @Lob types. (4)
 *
 * Reference:
 *  1) https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-jpaserver-model/src/main/java/ca/uhn/fhir/jpa/model/entity/ResourceHistoryTable.java#L94-L102
 *  2) https://www.laliluna.de/jpa-hibernate-guide/ch10.html
 *  3) https://debezium.io/documentation/reference/connectors/postgresql.html#postgresql-basic-types
 *  4) https://github.com/hibernate/hibernate-orm/blob/main/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java#L101
 *  5) https://dzone.com/articles/postgres-and-oracle
 *  
 */
public class CustomPostgreSQLDialect extends org.hibernate.dialect.PostgreSQL10Dialect {
    
    public CustomPostgreSQLDialect() {
        super();
        registerColumnType( Types.BLOB, "bytea" );
    }

    @Override
    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
    if (sqlTypeDescriptor.getSqlType() == java.sql.Types.BLOB) {
      return BinaryTypeDescriptor.INSTANCE;
    }
    return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
    
  }
}
