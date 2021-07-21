package calcite.test.tpcds.utils;

import com.google.common.collect.ImmutableSet;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.tpcds.TpcdsSchema;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

public class PlannerUtil {
    private static final SqlToRelConverter.Config DEFAULT_REL_CONFIG =
            SqlToRelConverter.config().withTrimUnusedFields(false);
    private static final ImmutableSet<RelOptRule> RULE_SET = ImmutableSet.of(
            CoreRules.PROJECT_TO_LOGICAL_PROJECT_AND_WINDOW,
            CoreRules.FILTER_INTO_JOIN,
            CoreRules.SORT_REMOVE,
            CoreRules.SORT_JOIN_TRANSPOSE,
            CoreRules.SORT_PROJECT_TRANSPOSE,
            CoreRules.SORT_UNION_TRANSPOSE,
            CoreRules.SORT_EXCHANGE_REMOVE_CONSTANT_KEYS,
            CoreRules.SORT_JOIN_COPY,
            CoreRules.SORT_UNION_TRANSPOSE_MATCH_NULL_FETCH,
            CoreRules.SORT_REMOVE_CONSTANT_KEYS,
            EnumerableRules.ENUMERABLE_JOIN_RULE,
            EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE,
            EnumerableRules.ENUMERABLE_CORRELATE_RULE,
            EnumerableRules.ENUMERABLE_PROJECT_RULE,
            EnumerableRules.ENUMERABLE_FILTER_RULE,
            EnumerableRules.ENUMERABLE_CALC_RULE,
            EnumerableRules.ENUMERABLE_AGGREGATE_RULE,
            EnumerableRules.ENUMERABLE_SORT_RULE,
            EnumerableRules.ENUMERABLE_LIMIT_RULE,
            EnumerableRules.ENUMERABLE_COLLECT_RULE,
            EnumerableRules.ENUMERABLE_UNCOLLECT_RULE,
            EnumerableRules.ENUMERABLE_MERGE_UNION_RULE,
            EnumerableRules.ENUMERABLE_UNION_RULE,
            EnumerableRules.ENUMERABLE_REPEAT_UNION_RULE,
            EnumerableRules.ENUMERABLE_TABLE_SPOOL_RULE,
            EnumerableRules.ENUMERABLE_INTERSECT_RULE,
            EnumerableRules.ENUMERABLE_MINUS_RULE,
            EnumerableRules.ENUMERABLE_TABLE_MODIFICATION_RULE,
            EnumerableRules.ENUMERABLE_VALUES_RULE,
            EnumerableRules.ENUMERABLE_WINDOW_RULE,
            EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE,
            EnumerableRules.ENUMERABLE_TABLE_FUNCTION_SCAN_RULE,
            EnumerableRules.ENUMERABLE_MATCH_RULE);

    /**
     * Get TPC-DS queries default planner
     *
     * @return planner
     */
    public static Planner getPlannerDefaultTPCDSQueries() {
        return Frameworks.getPlanner(frameworkConfigTPCDSQueries());
    }


    /**
     * TPC-DS queries default framework config
     *
     * @return default framework config
     */
    private static FrameworkConfig frameworkConfigTPCDSQueries() {
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        final SchemaPlus schema = rootSchema.add(SchemaSpec.TPCDS.schemaName, new TpcdsSchema(5));
        return Frameworks.newConfigBuilder()
                .defaultSchema(schema)
                .sqlToRelConverterConfig(DEFAULT_REL_CONFIG)
                .build();
    }

    /**
     * Get rules
     *
     * @return rules
     */
    public static ImmutableSet<RelOptRule> getRuleSet() {

        return RULE_SET;
    }
}
