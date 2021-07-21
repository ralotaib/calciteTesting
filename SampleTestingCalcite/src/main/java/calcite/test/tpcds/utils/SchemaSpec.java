package calcite.test.tpcds.utils;
/** Specification for common test schemas. */
public enum SchemaSpec {
    TPCDS("TPCDS_5");
    /** The name of the schema that is usually created from this specification.
     * (Names are not unique, and you can use another name if you wish.) */
    public final String schemaName;
    SchemaSpec(String schemaName) {
        this.schemaName = schemaName;
    }
}